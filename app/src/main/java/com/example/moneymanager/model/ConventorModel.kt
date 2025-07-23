package com.example.moneymanager.model

import androidx.room.PrimaryKey


data class ConventorModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contentDisplay: String,
    val content: String,
    var rate: Double,
    var isSelected: Boolean,
) {}