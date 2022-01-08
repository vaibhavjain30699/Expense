package com.vaibhav.expense

import android.app.AlertDialog
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vaibhav.expense.Model.Transaction
import java.text.SimpleDateFormat
import java.util.*

class RecyclerViewAdapter : ListAdapter<Transaction,RecyclerViewAdapter.TransactionViewHolder>(
    WORDS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder.create(parent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val amount: TextView = itemView.findViewById(R.id.amount_type)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val delete: Button = itemView.findViewById(R.id.delete)
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)

        fun bind(transaction: Transaction?) {
            val amountAndType = transaction!!.amount.toString() + "Rs, " + transaction.type
            amount.text = amountAndType
            val format = SimpleDateFormat("dd/MM/yyyy hh:mm")
            val cal = Calendar.getInstance()
            cal.timeInMillis = transaction.time
            this.time.text = format.format(cal.time)

            val color = if(transaction.type=="Food"){
                itemView.resources.getColor(R.color.Food)
            }else if(transaction.type=="Shopping"){
                itemView.resources.getColor(R.color.Shopping)
            }else if(transaction.type=="Utility Bill"){
                itemView.resources.getColor(R.color.Utility_Bill)
            }else if(transaction.type=="Rent"){
                itemView.resources.getColor(R.color.Rent)
            }else if(transaction.type=="Entertainment"){
                itemView.resources.getColor(R.color.Entertainment)
            }else {
                itemView.resources.getColor(R.color.Others)
            }

            linearLayout.setBackgroundColor(color)

            delete.setOnClickListener {
                val dialog = AlertDialog.Builder(itemView.context)
                    .setTitle("Delete Transaction?")
                    .setMessage("Are you sure you want to delete the transaction?")
                    .setPositiveButton("Yes"){ dialog,which ->
                        val transactionViewModel = TransactionViewModel(TransactionApplication().repository)
                        transactionViewModel.delete(transaction = transaction)
                    }
                    .setNegativeButton("No"){dialog,which ->
                        dialog.cancel()
                    }
                    .create()
                dialog.show()
            }

        }

        companion object {
            fun create(parent: ViewGroup): TransactionViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.transaction_item, parent, false)
                return TransactionViewHolder(view)
            }
        }
    }

    companion object {
        private val WORDS_COMPARATOR = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}