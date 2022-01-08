package com.vaibhav.expense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import kotlin.time.milliseconds

class MainActivity : AppCompatActivity() {

    private val transactionViewModel : TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as TransactionApplication).repository)
    }

    private val newTransactionActivityRequestCode = 1

    lateinit var overview: TextView

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = RecyclerViewAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        var transactions : List<Transaction> = listOf()

        transactionViewModel.allTransactions.observe(this, Observer { t ->
            t?.let {
                transactions = it
                adapter.submitList(transactions.asReversed())
            }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            val intent = Intent(this,NewTransactionActivity::class.java)
            startActivityForResult(intent,newTransactionActivityRequestCode)
        }

        overview = findViewById(R.id.overview)
        val cal = Calendar.getInstance().time
        cal.hours = 0
        cal.minutes = 0
        cal.seconds = 0

        var total = transactionViewModel.totalSpent.value
        var today = transactionViewModel.getTodaySpent(cal.time.milliseconds.toLongMilliseconds()).value

        val liveDataMerger = MediatorLiveData<Int>()
        liveDataMerger.addSource(transactionViewModel.totalSpent){ value ->
            total = value
            combineValues(total,today)
            Toast.makeText(this,"TT",Toast.LENGTH_SHORT).show()
        }
        liveDataMerger.addSource(transactionViewModel.getTodaySpent(cal.time.milliseconds.toLongMilliseconds())){ value ->
            today = value
            combineValues(total,today)
            Toast.makeText(this,"TO",Toast.LENGTH_SHORT).show()
        }
        liveDataMerger.observe(this, Observer {})

    }

    private fun combineValues(total: Int?, today: Int?){
        if(total!=null  &&  today!=null) {
            val s = "Total Spent: $total\nToday's Spent: $today";
            overview.text = s
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==newTransactionActivityRequestCode  &&  resultCode== RESULT_OK){
            var a:Int?
            var t:String?
            data?.getIntExtra("Amount",-1).let {
                a = it
            }
            data?.getStringExtra("Type").let {
                t = it
            }

            if(a!=null  &&  t!=null){
                val date = System.currentTimeMillis()
                val transaction = Transaction(amount = a!!,type = t!!,time = date)
                transactionViewModel.insert(transaction)
            }else{
                Toast.makeText(this,"Transaction NULL",Toast.LENGTH_LONG).show()
            }

        }else{
            Toast.makeText(this,"Transaction Failed",Toast.LENGTH_LONG).show()
        }
    }

}