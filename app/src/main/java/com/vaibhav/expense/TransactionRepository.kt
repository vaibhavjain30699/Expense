package com.vaibhav.expense

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDAO: TransactionDAO) {


    val allTransactions: Flow<List<Transaction>> = transactionDAO.getAllTransactions()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(transaction: Transaction) {
        transactionDAO.insert(transaction)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(transaction: Transaction) {
        transactionDAO.delete(transaction)
    }

}