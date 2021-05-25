package com.vaibhav.expense

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDAO {

    @Query("SELECT * FROM $table_name")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction)

    @Query("DELETE FROM $table_name")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(transaction: Transaction)

}