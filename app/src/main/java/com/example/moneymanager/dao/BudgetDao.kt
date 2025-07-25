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

    @Query("SELECT * FROM budget WHERE dateTime = :dateTime AND type = 1 LIMIT 1")
    suspend fun getBudgetByDate(dateTime: String): BudgetModel?

    @Update
    suspend fun updateBudget(budget: BudgetModel)

    @Query("SELECT * FROM budget WHERE dateTime = :dateTime AND type = 0")
    suspend fun getListBudgetByDate(dateTime: String): List<BudgetModel>?

    @Query("SELECT * FROM budget WHERE name = :name AND dateTime = :dateTime LIMIT 1")
    suspend fun getBudgetByName(name: String, dateTime: String): BudgetModel?
}