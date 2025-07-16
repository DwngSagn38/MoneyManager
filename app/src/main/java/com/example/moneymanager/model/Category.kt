package com.example.moneymanager.model

import java.io.Serializable

data class Category(
    val stt: Int,
    val name: String,
    val type: String,
    val imgResId: Int
): Serializable
