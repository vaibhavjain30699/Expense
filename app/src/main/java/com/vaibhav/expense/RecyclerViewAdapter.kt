package com.vaibhav.expense

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.vaibhav.expense.Model.Transaction
import java.text.SimpleDateFormat
import java.util.*

class RecyclerViewAdapter(private val onDeleteClicked: (Transaction) -> Unit) :
    ListAdapter<Transaction, RecyclerViewAdapter.TransactionViewHolder>(
        TRANSACTION_COMPARATOR
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onDeleteClicked)
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val amount: TextView = itemView.findViewById(R.id.amount_type)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val delete: Button = itemView.findViewById(R.id.delete)
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)

        fun bind(transaction: Transaction?, onDeleteClicked: (Transaction) -> Unit) {
            if (transaction == null) return
            val amountAndType = "${transaction.amount}Rs, ${transaction.type}"
            amount.text = amountAndType
            val format = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.timeInMillis = transaction.time
            this.time.text = format.format(cal.time)

            val colorRes = when (transaction.type) {
                "Food" -> R.color.Food
                "Shopping" -> R.color.Shopping
                "Utility Bill" -> R.color.Utility_Bill
                "Rent" -> R.color.Rent
                "Entertainment" -> R.color.Entertainment
                else -> R.color.Others
            }

            linearLayout.setBackgroundColor(ContextCompat.getColor(itemView.context, colorRes))

            delete.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Delete Transaction?")
                    .setMessage("Are you sure you want to delete the transaction?")
                    .setPositiveButton("Yes") { _, _ ->
                        onDeleteClicked(transaction)
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
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
        private val TRANSACTION_COMPARATOR = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }
        }
    }
}