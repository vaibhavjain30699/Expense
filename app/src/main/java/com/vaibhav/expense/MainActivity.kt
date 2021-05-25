package com.vaibhav.expense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val transactionViewModel : TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as TransactionApplication).repository)
    }

    private val newTransactionActivityRequestCode = 1

    private var food_count: Int = 0;
    private var entertainment_count: Int = 0;
    private var shoppping_count: Int = 0;
    private var utility_count: Int = 0;
    private var rent_count: Int = 0;
    private var others_count: Int = 0;

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
                adapter.submitList(it.asReversed())
                transactions = it
                count(transactions)
            }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            val intent = Intent(this,NewTransactionActivity::class.java)
            startActivityForResult(intent,newTransactionActivityRequestCode)
        }

        count(transactions)

        val overview: TextView = findViewById(R.id.overview)
        overview.text = "$food_count $shoppping_count $entertainment_count $rent_count $utility_count $others_count ${transactions.size}"

    }

    fun count(t:List<Transaction>){
        food_count = 0
        entertainment_count = 0
        rent_count = 0
        utility_count = 0
        shoppping_count = 0
        others_count = 0

        for(ttt:Transaction in t){
            if(ttt.type=="Food")
                food_count+=ttt.amount
            else if(ttt.type=="Entertainment")
                entertainment_count+=ttt.amount
            else if(ttt.type=="Rent")
                rent_count+=ttt.amount
            else if(ttt.type=="Utility Bill")
                utility_count+=ttt.amount
            else if(ttt.type=="Shopping")
                shoppping_count+=ttt.amount
            else
                others_count+=ttt.amount
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
//            Toast.makeText(this,"Transaction Failed",Toast.LENGTH_LONG).show()
        }
    }

}