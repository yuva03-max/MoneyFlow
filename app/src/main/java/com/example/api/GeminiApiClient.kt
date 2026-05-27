package com.example.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.moneyflow.BudgetEntity
import com.example.data.moneyflow.GoalEntity
import com.example.data.moneyflow.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getSmartInsights(
        transactions: List<TransactionEntity>,
        budgets: List<BudgetEntity>,
        goals: List<GoalEntity>
    ): SmartAdvice = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("placeholder", ignoreCase = true)) {
            Log.w(TAG, "Gemini API key is not configured. Falling back to local heuristic analysis.")
            return@withContext getLocalHeuristics(transactions, budgets, goals)
        }

        // Formulate prompt with structured financial data
        val prompt = buildPrompt(transactions, budgets, goals)

        try {
            // Build the JSON request body
            // Format: { "contents": [{ "parts":[{ "text": "prompt" }] }] }
            val requestBodyJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                // Optional: add system instruction for fintech roleplay
                val sysInstructionObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", "You are MoneyFlow AI, a brilliant personal finance advisor. Provide structured, concise, friendly insights. Use markdown bullet points. Do not mention your model name or systemic limitations. Be extremely direct.")
                        }
                        put(partObj)
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", sysInstructionObj)
            }

            val requestBody = requestBodyJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyString = response.body?.string() ?: ""
                Log.d(TAG, "Response from Gemini: $bodyString")
                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text", "")
                            if (text.isNotEmpty()) {
                                return@withContext parseGeminiOutput(text)
                            }
                        }
                    }
                }
            } else {
                Log.e(TAG, "Request failed with code: ${response.code}, message: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Gemini API: ${e.message}", e)
        }

        // Fallback helper
        getLocalHeuristics(transactions, budgets, goals)
    }

    private fun buildPrompt(
        transactions: List<TransactionEntity>,
        budgets: List<BudgetEntity>,
        goals: List<GoalEntity>
    ): String {
        val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

        val categorySpentMap = transactions.filter { it.type == "EXPENSE" }
            .groupBy { it.category }
            .mapValues { (_, txs) -> txs.sumOf { it.amount } }

        val categorySpentStr = categorySpentMap.entries.joinToString("\n") { "Category: ${it.key}, Spent: $${String.format("%.2f", it.value)}" }
        val budgetsStr = budgets.joinToString("\n") { "Category: ${it.category}, Limit: $${String.format("%.2f", it.limitAmount)}" }
        val goalsStr = goals.joinToString("\n") { "Goal: ${it.name}, Target: $${String.format("%.2f", it.targetAmount)}, Saved: $${String.format("%.2f", it.savedAmount)}" }

        return """
            Analyze this user's current financial behavior and return three distinct items, separated by triple hyphens '---'.
            
            USER DATA:
            - Monthly Income: $${String.format("%.2f", totalIncome)}
            - Monthly Expenses: $${String.format("%.2f", totalExpense)}
            - Spending breakdown:
            $categorySpentStr
            
            - Budgets:
            $budgetsStr
            
            - Goals:
            $goalsStr
            
            Please reply exactly with this structure:
            [A single overspending summary/alert, 1-2 sentences]
            ---
            [1-2 smart actionable suggestions on how to save based on categories]
            ---
            [A playful prediction of their end-of-month financial status or future expense estimation]
            
            Example Format:
            ⚠️ High alert on Dining! You are about to hit your food limit.
            ---
            💡 Consider swapping Starbucks for brewing at home. This can unlock $45/month to speed up your emergency fund goal.
            ---
            🔮 Future Forecast: If this pace index continues, you'll reach your MacBook goal 12 days earlier than scheduled! Keep pushing!
        """.trimIndent()
    }

    private fun parseGeminiOutput(text: String): SmartAdvice {
        val parts = text.split("---")
        return if (parts.size >= 3) {
            SmartAdvice(
                overspendingAlert = parts[0].trim(),
                savingsAdvice = parts[1].trim(),
                futurePrediction = parts[2].trim()
            )
        } else {
            // Raw text response - divide manually or use it for all
            SmartAdvice(
                overspendingAlert = "💡 Analysis Checklist",
                savingsAdvice = text.trim(),
                futurePrediction = "🔮 Practice disciplined habits daily!"
            )
        }
    }

    private fun getLocalHeuristics(
        transactions: List<TransactionEntity>,
        budgets: List<BudgetEntity>,
        goals: List<GoalEntity>
    ): SmartAdvice {
        val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

        val highestCategory = transactions.filter { it.type == "EXPENSE" }
            .groupBy { it.category }
            .maxByOrNull { (_, txs) -> txs.sumOf { it.amount } }?.key ?: "Transport"

        val warning = if (totalExpense > totalIncome * 0.8) {
            "⚠️ Overspending Alert: Your overall monthly burn rate is high ($${String.format("%.1f", totalExpense)}), eating into 80%+ of your income index."
        } else {
            "✅ Safe Zone: Your overall burning rate is under 80%. Exceptional discipline!"
        }

        val advice = when (highestCategory) {
            "Food" -> "💡 Smart Savings Hint: Swapping coffee and dining out with self-cooked meals may unlock up to $150/month towards saving goals."
            "Shopping" -> "💡 Smart Savings Hint: Try setting a 48-hour cool-off rule before purchasing non-essential items to reduce impulsive buying."
            "Bills" -> "💡 Smart Savings Hint: Review active subscriptions and cancel unused automatic memberships to save immediate fixed costs."
            else -> "💡 Smart Savings Hint: Setting up automated savings goal sweep accounts speeds up emergency cushion accumulation by 14%."
        }

        val goalCompletes = goals.count { it.savedAmount >= it.targetAmount }
        val prediction = if (goalCompletes > 0) {
            "🔮 Financial Forecast: You are hitting streaks! Maintaining this savings multiplier will elevate your Fintech wealth index level."
        } else {
            "🔮 Financial Forecast: With an estimated surplus of $${String.format("%.2f", (totalIncome - totalExpense).coerceAtLeast(0.0))}, you're on course to reach goals next month!"
        }

        return SmartAdvice(warning, advice, prediction)
    }
}

data class SmartAdvice(
    val overspendingAlert: String,
    val savingsAdvice: String,
    val futurePrediction: String
)
