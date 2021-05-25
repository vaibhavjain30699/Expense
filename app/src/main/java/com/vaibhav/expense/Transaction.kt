package com.vaibhav.expense

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = table_name)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val amount: Int,
    val type: String,
    val time: Long
)