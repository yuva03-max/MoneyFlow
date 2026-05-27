package com.example.data.moneyflow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class MoneyFlowRepository(private val dao: MoneyFlowDao) {

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()
    val allGoals: Flow<List<GoalEntity>> = dao.getAllGoals()
    val userStats: Flow<UserStatsEntity?> = dao.getUserStatsFlow()
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()

    // --- Transactions & Side Effects ---
    suspend fun insertTransaction(transaction: TransactionEntity) {
        val transId = dao.insertTransaction(transaction)

        // Give XP for adding transactions!
        val stats = dao.getUserStats() ?: UserStatsEntity()
        var currentXp = stats.xp + 50
        var currentStreak = stats.streak

        // Streak detection logic based on days.
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        if (stats.lastTransactionDate == 0L) {
            currentStreak = 1
        } else {
            val lastTxCal = Calendar.getInstance()
            lastTxCal.timeInMillis = stats.lastTransactionDate
            lastTxCal.set(Calendar.HOUR_OF_DAY, 0)
            lastTxCal.set(Calendar.MINUTE, 0)
            lastTxCal.set(Calendar.SECOND, 0)
            lastTxCal.set(Calendar.MILLISECOND, 0)

            val diffDays = (today.timeInMillis - lastTxCal.timeInMillis) / (1000 * 60 * 60 * 24)
            if (diffDays == 1L) {
                currentStreak += 1
                // Bonus XP for maintaining streak!
                currentXp += 20 * currentStreak
                // Insert notification for milestone streak!
                if (currentStreak % 3 == 0) {
                    dao.insertNotification(
                        NotificationEntity(
                            title = "Streak Milestone! 🔥",
                            message = "You have recorded expenses for $currentStreak days in a row. +${20 * currentStreak} bonus XP awarded!",
                            type = "CHALLENGE",
                            date = System.currentTimeMillis()
                        )
                    )
                }
            } else if (diffDays > 1L) {
                // Reset streak if more than 1 day skipped
                currentStreak = 1
            }
        }

        // Update gamification credentials
        dao.insertUserStats(
            stats.copy(
                xp = currentXp,
                streak = currentStreak,
                lastTransactionDate = System.currentTimeMillis()
            )
        )

        // Check if transaction is an EXPENSE and violates any budget category limits!
        if (transaction.type == "EXPENSE") {
            // Find budget for this category
            val budget = dao.getBudgetByCategory(transaction.category)
            val allBudget = dao.getBudgetByCategory("ALL")

            val currentMonthTransactions = getCurrentMonthTransactions()

            if (budget != null) {
                val sumCategory = currentMonthTransactions
                    .filter { it.type == "EXPENSE" && it.category == transaction.category }
                    .sumOf { it.amount }

                if (sumCategory > budget.limitAmount) {
                    dao.insertNotification(
                        NotificationEntity(
                            title = "Budget Exceeded! ⚠️",
                            message = "${transaction.category} monthly budget of $${budget.limitAmount} has been exceeded! Spent: $${String.format("%.2f", sumCategory)}",
                            type = "BUDGET_ALERT",
                            date = System.currentTimeMillis()
                        )
                    )
                } else if (sumCategory >= budget.limitAmount * 0.8) {
                    dao.insertNotification(
                        NotificationEntity(
                            title = "Budget Warning! 🚨",
                            message = "${transaction.category} is at 80% or more of its monthly limit ($${budget.limitAmount}). Spent: $${String.format("%.2f", sumCategory)}",
                            type = "BUDGET_ALERT",
                            date = System.currentTimeMillis()
                        )
                    )
                }
            }

            if (allBudget != null) {
                val sumTotal = currentMonthTransactions
                    .filter { it.type == "EXPENSE" }
                    .sumOf { it.amount }

                if (sumTotal > allBudget.limitAmount) {
                    dao.insertNotification(
                        NotificationEntity(
                            title = "Overall Budget Alert! ⚠️",
                            message = "Your total monthly budget cap of $${allBudget.limitAmount} has been crossed. Total Spent: $${String.format("%.2f", sumTotal)}",
                            type = "BUDGET_ALERT",
                            date = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    private suspend fun getCurrentMonthTransactions(): List<TransactionEntity> {
        val allTx = dao.getAllTransactions().firstOrNull() ?: emptyList()
        val calendar = Calendar.getInstance()
        val thisMonth = calendar.get(Calendar.MONTH)
        val thisYear = calendar.get(Calendar.YEAR)

        return allTx.filter { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.date }
            txCal.get(Calendar.MONTH) == thisMonth && txCal.get(Calendar.YEAR) == thisYear
        }
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Int) {
        dao.deleteTransactionById(id)
    }

    // --- Budgets ---
    suspend fun insertBudget(budget: BudgetEntity) {
        dao.insertBudget(budget)
    }

    suspend fun deleteBudgetById(id: Int) {
        dao.deleteBudgetById(id)
    }

    // --- Goals ---
    suspend fun insertGoal(goal: GoalEntity) {
        val oldGoal = if (goal.id != 0) dao.getGoalById(goal.id) else null
        dao.insertGoal(goal)

        // Check if saving goal is now fully achieved!
        if (goal.savedAmount >= goal.targetAmount) {
            val wasAlreadyAchieved = oldGoal != null && oldGoal.savedAmount >= oldGoal.targetAmount
            if (!wasAlreadyAchieved) {
                // Add notifications and Award XP!
                val stats = dao.getUserStats() ?: UserStatsEntity()
                dao.insertUserStats(stats.copy(xp = stats.xp + 200)) // +200 XP for achieving savings goals!
                dao.insertNotification(
                    NotificationEntity(
                        title = "Goal Achieved! 🏆",
                        message = "Congratulations! You reached your savings goal: '${goal.name}' of $${goal.targetAmount}! +200 XP awarded.",
                        type = "SAVINGS_MILESTONE",
                        date = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    suspend fun addMoneyToGoal(goalId: Int, amount: Double) {
        val goal = dao.getGoalById(goalId) ?: return
        val newSaved = (goal.savedAmount + amount).coerceAtMost(goal.targetAmount)
        insertGoal(goal.copy(savedAmount = newSaved))
    }

    suspend fun deleteGoal(goal: GoalEntity) {
        dao.deleteGoal(goal)
    }

    // --- Stats ---
    suspend fun initializeUserIfNeeded() {
        val stats = dao.getUserStats()
        if (stats == null) {
            dao.insertUserStats(UserStatsEntity())
            // Insert mock notifications for first launch!
            dao.insertNotification(
                NotificationEntity(
                    title = "Welcome to MoneyFlow! 👋",
                    message = "Your premium white & blue fintech finance tracker is ready. Receive custom smart AI suggestions on your dashboard!",
                    type = "SYSTEM",
                    date = System.currentTimeMillis()
                )
            )
            // Add some initial mock transactions for beautiful UI rendering, but save locally on first-run only
            preloadMockData()
        }
    }

    private suspend fun preloadMockData() {
        val current = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L
        val mockTransactions = listOf(
            TransactionEntity(title = "Salary Payout", amount = 4500.0, type = "INCOME", category = "Salary", paymentMethod = "Bank Transfer", notes = "Regular Monthly Salary", date = current - 5 * oneDay),
            TransactionEntity(title = "Starbucks Coffee", amount = 6.50, type = "EXPENSE", category = "Food", paymentMethod = "Card", notes = "Mocha Latte", date = current - 4 * oneDay),
            TransactionEntity(title = "Nike Shoes", amount = 120.0, type = "EXPENSE", category = "Shopping", paymentMethod = "Card", notes = "Air Max", date = current - 3 * oneDay),
            TransactionEntity(title = "Uber Ride", amount = 22.40, type = "EXPENSE", category = "Transport", paymentMethod = "Cash", notes = "Commute to office", date = current - 2 * oneDay),
            TransactionEntity(title = "Freelance UI Project", amount = 850.0, type = "INCOME", category = "Freelance", paymentMethod = "Bank Transfer", notes = "Fintech dashboard mockups", date = current - oneDay),
            TransactionEntity(title = "Netflix Premium", amount = 15.99, type = "EXPENSE", category = "Bills", paymentMethod = "Card", notes = "Subscription renewal", date = current - 12 * 3600 * 1000)
        )
        for (tx in mockTransactions) {
            dao.insertTransaction(tx)
        }

        val mockBudgets = listOf(
            BudgetEntity(category = "ALL", limitAmount = 1500.0, monthYear = "05-2026"),
            BudgetEntity(category = "Food", limitAmount = 250.0, monthYear = "05-2026"),
            BudgetEntity(category = "Shopping", limitAmount = 400.0, monthYear = "05-2026"),
            BudgetEntity(category = "Bills", limitAmount = 150.0, monthYear = "05-2026")
        )
        for (bg in mockBudgets) {
            dao.insertBudget(bg)
        }

        val mockGoals = listOf(
            GoalEntity(name = "Emergency Fund", targetAmount = 5000.0, savedAmount = 1200.0, targetDate = current + 180 * oneDay),
            GoalEntity(name = "MacBook Pro M4", targetAmount = 2500.0, savedAmount = 1850.0, targetDate = current + 45 * oneDay),
            GoalEntity(name = "Summer Vacation", targetAmount = 3000.0, savedAmount = 450.0, targetDate = current + 60 * oneDay)
        )
        for (g in mockGoals) {
            dao.insertGoal(g)
        }
    }

    // --- Notifications ---
    suspend fun insertNotification(notification: NotificationEntity) {
        dao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: Int) {
        dao.markNotificationAsRead(id)
    }

    suspend fun deleteNotificationById(id: Int) {
        dao.deleteNotificationById(id)
    }
}
