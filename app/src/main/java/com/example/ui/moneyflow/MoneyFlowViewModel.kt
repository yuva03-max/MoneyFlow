package com.example.ui.moneyflow

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiApiClient
import com.example.api.SmartAdvice
import com.example.data.moneyflow.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// Navigation states
enum class AppScreen {
    SPLASH,
    ONBOARDING,
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD,
    MAIN_APP
}

// Bottom Tab states
enum class MainTab {
    HOME,
    ANALYTICS,
    BUDGET,
    GOALS,
    SETTINGS
}

class MoneyFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MoneyFlowDatabase.getDatabase(application)
    private val repository = MoneyFlowRepository(db.moneyFlowDao())

    // --- Navigation & Flow State ---
    var currentScreen by mutableStateOf(AppScreen.SPLASH)
    var currentTab by mutableStateOf(MainTab.HOME)
    var onboardingPage by mutableStateOf(0)

    // --- Auth State ---
    var isLoggedIn by mutableStateOf(false)
    var userEmail by mutableStateOf("yuvayyss@gmail.com")
    var userName by mutableStateOf("Yuvay")
    var rememberMe by mutableStateOf(true)

    // --- Settings Preferences ---
    var darkThemeEnabled by mutableStateOf(false)
    var selectedCurrency by mutableStateOf("$")
    var selectedLanguage by mutableStateOf("English")

    // --- Database Flow Collections ---
    val transactions = repository.allTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val budgets = repository.allBudgets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val goals = repository.allGoals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val userStats = repository.userStats.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserStatsEntity())
    val notifications = repository.allNotifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- AI Insights State ---
    var isAiLoading by mutableStateOf(false)
    var aiAdvice by mutableStateOf<SmartAdvice?>(null)

    // --- Overlay sheets & dialogues ---
    var showAddTransactionDialog by mutableStateOf(false)
    var showAddBudgetDialog by mutableStateOf(false)
    var showAddGoalDialog by mutableStateOf(false)
    var showNotificationList by mutableStateOf(false)

    // --- Transaction Form Fields ---
    var isExpenseForm by mutableStateOf(true) // True = Expense, False = Income
    var transTitle by mutableStateOf("")
    var transAmount by mutableStateOf("")
    var transCategory by mutableStateOf("Food")
    var transPaymentMethod by mutableStateOf("Card")
    var transNotes by mutableStateOf("")
    var transDate by mutableStateOf(System.currentTimeMillis())
    var transBillImageUri by mutableStateOf<String?>(null)

    // --- Budget Form Fields ---
    var budgetCategory by mutableStateOf("Food")
    var budgetLimitAmount by mutableStateOf("")

    // --- Savings Goal Form Fields ---
    var goalName by mutableStateOf("")
    var goalTargetAmount by mutableStateOf("")
    var goalTargetDate by mutableStateOf(System.currentTimeMillis() + 30L * 24 * 3600 * 1000)

    // --- Feedback Alerts & Messages ---
    var feedbackMessage by mutableStateOf<String?>(null)

    init {
        // Initialize user profile DB preloads on startup
        viewModelScope.launch {
            repository.initializeUserIfNeeded()
            // Pull initial AI insights
            loadAiInsightsHeuristic()
        }

        // Animated Splash Transition
        viewModelScope.launch {
            delay(2200)
            // Navigate based on simulated session configurations
            val sharedPreferences = application.getSharedPreferences("moneyflow_prefs", Context.MODE_PRIVATE)
            val onboarded = sharedPreferences.getBoolean("onboarded", false)
            val remembered = sharedPreferences.getBoolean("remembered", false)

            if (!onboarded) {
                currentScreen = AppScreen.ONBOARDING
            } else if (remembered) {
                isLoggedIn = true
                currentScreen = AppScreen.MAIN_APP
            } else {
                currentScreen = AppScreen.LOGIN
            }
        }
    }

    // --- Theme Loader helper ---
    fun toggleDarkMode(enable: Boolean) {
        darkThemeEnabled = enable
    }

    // --- Simulated Auth Methods ---
    fun performLogin(email: String, password: String): Boolean {
        if (email.isNotEmpty() && password.length >= 4) {
            userEmail = email
            userName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            isLoggedIn = true
            currentScreen = AppScreen.MAIN_APP

            val sharedPreferences = getApplication<Application>().getSharedPreferences("moneyflow_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .putBoolean("remembered", rememberMe)
                .putString("email", userEmail)
                .putString("name", userName)
                .apply()

            triggerNotification("Login Successful Key", "Welcome back, $userName! Securely synchronizing database metrics.", "SYSTEM")
            triggerAiAnalysis()
            return true
        }
        return false
    }

    fun performSignup(name: String, email: String, password: String): Boolean {
        if (name.isNotEmpty() && email.isNotEmpty() && password.length >= 6) {
            userName = name
            userEmail = email
            isLoggedIn = true
            currentScreen = AppScreen.MAIN_APP

            val sharedPreferences = getApplication<Application>().getSharedPreferences("moneyflow_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .putBoolean("onboarded", true)
                .putBoolean("remembered", rememberMe)
                .putString("email", userEmail)
                .putString("name", userName)
                .apply()

            triggerNotification("Welcome onboard! 🎉", "Account successfully registered under $email. Let's tracking!", "SYSTEM")
            triggerAiAnalysis()
            return true
        }
        return false
    }

    fun performForgotPassword(email: String) {
        triggerNotification("Password Recovery Sent 📬", "A simulated recovery link was sent to $email.", "SYSTEM")
        currentScreen = AppScreen.LOGIN
    }

    fun logout() {
        isLoggedIn = false
        currentScreen = AppScreen.LOGIN
        val sharedPreferences = getApplication<Application>().getSharedPreferences("moneyflow_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("remembered", false).apply()
    }

    fun completeOnboarding() {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("moneyflow_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("onboarded", true).apply()
        currentScreen = AppScreen.LOGIN
    }

    // --- DB Transaction insertions ---
    fun addTransaction() {
        val parsedAmt = transAmount.toDoubleOrNull()
        if (transTitle.trim().isEmpty() || parsedAmt == null || parsedAmt <= 0) {
            feedbackMessage = "Invalid transaction Title or Amount."
            return
        }

        viewModelScope.launch {
            val entity = TransactionEntity(
                title = transTitle.trim(),
                amount = parsedAmt,
                type = if (isExpenseForm) "EXPENSE" else "INCOME",
                category = transCategory,
                paymentMethod = transPaymentMethod,
                notes = transNotes.trim(),
                date = transDate,
                billImageUri = transBillImageUri
            )
            repository.insertTransaction(entity)

            // Trigger immediate AI Analysis
            triggerAiAnalysis()

            // Reset Form Values
            transTitle = ""
            transAmount = ""
            transNotes = ""
            transBillImageUri = null
            showAddTransactionDialog = false
            feedbackMessage = "${entity.type} added successfully!"
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
            triggerAiAnalysis()
        }
    }

    // --- DB Budgets config ---
    fun addBudget() {
        val parsedAmt = budgetLimitAmount.toDoubleOrNull()
        if (parsedAmt == null || parsedAmt <= 0) {
            feedbackMessage = "Please insert a valid budget Limit."
            return
        }

        viewModelScope.launch {
            val entity = BudgetEntity(
                category = budgetCategory,
                limitAmount = parsedAmt,
                monthYear = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())
            )
            repository.insertBudget(entity)
            budgetLimitAmount = ""
            showAddBudgetDialog = false
            feedbackMessage = "Monthly budget configuration saved!"
        }
    }

    // --- DB Savings Goals ---
    fun addGoal() {
        val parsedAmt = goalTargetAmount.toDoubleOrNull()
        if (goalName.trim().isEmpty() || parsedAmt == null || parsedAmt <= 0) {
            feedbackMessage = "Please input a valid goal name and amount."
            return
        }

        viewModelScope.launch {
            val entity = GoalEntity(
                name = goalName.trim(),
                targetAmount = parsedAmt,
                savedAmount = 0.0,
                targetDate = goalTargetDate
            )
            repository.insertGoal(entity)
            goalName = ""
            goalTargetAmount = ""
            showAddGoalDialog = false
            feedbackMessage = "Savings Goal created!"
        }
    }

    fun addSavingsGoalFunds(goal: GoalEntity, amount: Double) {
        viewModelScope.launch {
            repository.addMoneyToGoal(goal.id, amount)
            triggerAiAnalysis()
        }
    }

    fun deleteGoal(g: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(g)
        }
    }

    // --- AI Smart Insights Caller ---
    fun triggerAiAnalysis() {
        viewModelScope.launch {
            isAiLoading = true
            val response = GeminiApiClient.getSmartInsights(
                transactions = transactions.value,
                budgets = budgets.value,
                goals = goals.value
            )
            aiAdvice = response
            isAiLoading = false
        }
    }

    private fun loadAiInsightsHeuristic() {
        viewModelScope.launch {
            // Wait for Room preloads to conclude
            delay(500)
            triggerAiAnalysis()
        }
    }

    // --- Notification helper ---
    fun triggerNotification(title: String, message: String, type: String) {
        viewModelScope.launch {
            repository.insertNotification(
                NotificationEntity(
                    title = title,
                    message = message,
                    type = type,
                    date = System.currentTimeMillis()
                )
            )
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            notifications.value.forEach {
                repository.markNotificationAsRead(it.id)
            }
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch {
            repository.deleteNotificationById(id)
        }
    }

    // --- Export, Backup Simulated logic ---
    fun exportDataAsCSV(): String {
        val txs = transactions.value
        val stringBuilder = StringBuilder()
        stringBuilder.append("ID,Title,Amount,Type,Category,PaymentMethod,Date,Notes\n")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        for (tx in txs) {
            val dateStr = sdf.format(Date(tx.date))
            stringBuilder.append("${tx.id},\"${tx.title.replace("\"", "\"\"")}\",${tx.amount},${tx.type},${tx.category},${tx.paymentMethod},\"$dateStr\",\"${tx.notes.replace("\"", "\"\"")}\"\n")
        }

        try {
            val context = getApplication<Application>()
            val file = File(context.filesDir, "moneyflow_export.csv")
            FileOutputStream(file).use {
                it.write(stringBuilder.toString().toByteArray())
            }
            feedbackMessage = "Data exported successfully! Saved: files/moneyflow_export.csv"
            return file.absolutePath
        } catch (e: Exception) {
            Log.e("ViewModel", "CSV export failed: ${e.message}", e)
            feedbackMessage = "Export failed error: ${e.message}"
        }
        return ""
    }

    fun triggerBackupRestoreSimulation() {
        viewModelScope.launch {
            feedbackMessage = "Initiating secure backup to cloud..."
            delay(1500)
            feedbackMessage = "Database synchronized! 100% cloud secure backup verified."
            triggerNotification("Cloud Backup Confirmed ☁️", "All local Transactions, Budgets, and Milestones successfully synced to Google cloud server.", "SYSTEM")
        }
    }
}
