package com.example.moneymanager.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val img: Int,
  val name: String,
  val note: String,
  val idCatagory: Int,
  val time: String,
  val date: String,
  val amount: Float,
  val type: String,
)
