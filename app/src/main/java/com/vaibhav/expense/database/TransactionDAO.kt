package com.vaibhav.expense.database

import androidx.room.*
import com.vaibhav.expense.Model.Transaction
import com.vaibhav.expense.table_name
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDAO {

    @Query("SELECT * FROM $table_name")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("DELETE FROM $table_name")
    suspend fun deleteAll()

    @Query("SELECT SUM(amount) FROM $table_name")
    fun getTotalSpent(): Flow<Int?>

    @Query("SELECT SUM(amount) FROM $table_name WHERE time >= :time")
    fun getTodaySpent(time: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}