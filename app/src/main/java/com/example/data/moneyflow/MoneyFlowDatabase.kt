package com.example.data.moneyflow

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TransactionEntity::class,
        BudgetEntity::class,
        GoalEntity::class,
        UserStatsEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MoneyFlowDatabase : RoomDatabase() {

    abstract fun moneyFlowDao(): MoneyFlowDao

    companion object {
        @Volatile
        private var INSTANCE: MoneyFlowDatabase? = null

        fun getDatabase(context: Context): MoneyFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoneyFlowDatabase::class.java,
                    "moneyflow_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
