package com.vaibhav.expense.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vaibhav.expense.Model.Transaction
import com.vaibhav.expense.database_name
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Transaction::class), version = 1, exportSchema = false)
public abstract class TransactionRoomDatabase : RoomDatabase() {

    abstract fun transactionDAO() : TransactionDAO

    private class TransactionDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.transactionDAO())
                }
            }
        }

        suspend fun populateDatabase(transactionDAO: TransactionDAO) {
            // Delete all content here.
            transactionDAO.deleteAll()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TransactionRoomDatabase? = null

        fun getDatabase(context: Context,scope: CoroutineScope): TransactionRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionRoomDatabase::class.java,
                    database_name
                )
                    .addCallback(TransactionDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}