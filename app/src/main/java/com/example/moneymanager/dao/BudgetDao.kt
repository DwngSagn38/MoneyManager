package com.example.moneymanager.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moneymanager.model.BudgetModel

@Dao
interface BudgetDao {
    @Insert
    suspend fun insertBudget(budget: BudgetModel)

    @Query("SELECT * FROM budget ORDER BY id DESC")
    suspend fun getAllBudget(): List<BudgetModel>

    @Query("SELECT * FROM budget WHERE dateTime = :dateTime")
    suspend fun getBudgetByDate(dateTime: String): BudgetModel?

    @Update
    suspend fun updateBudget(budget: BudgetModel)
}