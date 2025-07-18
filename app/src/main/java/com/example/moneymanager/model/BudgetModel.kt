package com.example.moneymanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class BudgetModel(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val dateTime : String,
    val budget : Float,
)
