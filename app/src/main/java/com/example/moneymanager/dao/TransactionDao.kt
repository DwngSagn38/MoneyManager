package com.example.moneymanager.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moneymanager.model.TransactionEntity

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY id DESC")
    suspend fun getTransactionsByType(type: String): List<TransactionEntity>
}
