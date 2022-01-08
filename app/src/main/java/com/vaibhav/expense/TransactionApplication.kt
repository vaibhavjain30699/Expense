package com.vaibhav.expense

import android.app.Application
import com.vaibhav.expense.database.TransactionRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TransactionApplication : Application(){

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { TransactionRoomDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { TransactionRepository(database.transactionDAO()) }

}