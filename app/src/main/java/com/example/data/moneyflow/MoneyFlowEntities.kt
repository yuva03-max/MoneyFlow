package com.example.data.moneyflow

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String,
    val paymentMethod: String,
    val notes: String,
    val date: Long,
    val billImageUri: String? = null
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // "ALL" or specific categories
    val limitAmount: Double,
    val monthYear: String // "MM-YYYY" formats
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val targetDate: Long
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: String = "current_user",
    val xp: Int = 0,
    val streak: Int = 0,
    val lastTransactionDate: Long = 0L,
    val unlockedBadgesString: String = "" // "STREAK_3,SAVER_100,AI_INSIGHT_FAN" etc.
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val type: String, // "BUDGET_ALERT", "BILL_REMINDER", "SAVINGS_MILESTONE", "CHALLENGE", "SYSTEM"
    val date: Long,
    val isRead: Boolean = false
)
