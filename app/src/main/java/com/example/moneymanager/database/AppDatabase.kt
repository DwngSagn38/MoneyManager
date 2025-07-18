package com.example.moneymanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moneymanager.dao.BudgetDao
import com.example.moneymanager.dao.TransactionDao
import com.example.moneymanager.model.BudgetModel
import com.example.moneymanager.model.TransactionEntity

// AppDatabase.kt

@Database(entities = [TransactionEntity::class, BudgetModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_manager_db"
                ).build().also { instance = it }
            }
    }
}
