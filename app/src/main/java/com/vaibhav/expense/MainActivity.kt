package com.vaibhav.expense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vaibhav.expense.Model.Transaction
import java.util.*
import kotlin.time.ExperimentalTime

class MainActivity : AppCompatActivity() {

    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as TransactionApplication).repository)
    }

    private val newTransactionActivityRequestCode = 1

    lateinit var overview: TextView

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = RecyclerViewAdapter { transaction ->
            transactionViewModel.delete(transaction)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val emptyTransactionImage = findViewById<LinearLayout>(R.id.emptyTransactionImage)

        transactionViewModel.allTransactions.observe(this, Observer { transactions ->
            if (transactions.isNullOrEmpty()) {
                emptyTransactionImage.visibility = ImageView.VISIBLE
                recyclerView.visibility = RecyclerView.GONE
            } else {
                emptyTransactionImage.visibility = ImageView.GONE
                recyclerView.visibility = RecyclerView.VISIBLE
                adapter.submitList(transactions.asReversed())
            }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, NewTransactionActivity::class.java)
            startActivityForResult(intent, newTransactionActivityRequestCode)
        }

        overview = findViewById(R.id.overview)
        setupOverview()
    }

    private fun setupOverview() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        transactionViewModel.setTodayTimestamp(cal.timeInMillis)

        val liveDataMerger = MediatorLiveData<Pair<Int?, Int?>>()
        liveDataMerger.addSource(transactionViewModel.totalSpent) { total ->
            liveDataMerger.value = Pair(total, transactionViewModel.todaySpent.value)
        }
        liveDataMerger.addSource(transactionViewModel.todaySpent) { today ->
            liveDataMerger.value = Pair(transactionViewModel.totalSpent.value, today)
        }

        liveDataMerger.observe(this) { pair ->
            val total = pair.first ?: 0
            val today = pair.second ?: 0
            overview.text = "Total Spent: $total\nToday's Spent: $today"
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == newTransactionActivityRequestCode && resultCode == RESULT_OK) {
            var a: Int?
            var t: String?
            data?.getIntExtra("Amount", -1).let {
                a = it
            }
            data?.getStringExtra("Type").let {
                t = it
            }

            if (a != null && t != null) {
                val date = System.currentTimeMillis()
                val transaction = Transaction(amount = a!!, type = t!!, time = date)
                transactionViewModel.insert(transaction)
            } else {
                Toast.makeText(this, "Transaction NULL", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this, "Transaction Failed", Toast.LENGTH_LONG).show()
        }
    }
}