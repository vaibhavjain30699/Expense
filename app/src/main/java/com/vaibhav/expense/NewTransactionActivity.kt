package com.vaibhav.expense

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.android.material.textfield.TextInputEditText

class NewTransactionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var amount: TextInputEditText
    private lateinit var spinner: Spinner
    private lateinit var add: Button
    var type: String = ""
    var money: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_transaction)

        initialize()

        add.setOnClickListener {
            val replyIntent = Intent()
            val amountStr = amount.text.toString()
            if (TextUtils.isEmpty(amountStr)) {
                setResult(RESULT_CANCELED, replyIntent)
            } else {
                try {
                    val amountInt = amountStr.toInt()
                    replyIntent.putExtra("Amount", amountInt)
                    replyIntent.putExtra("Type", type)
                    setResult(RESULT_OK, replyIntent)
                } catch (e: NumberFormatException) {
                    setResult(RESULT_CANCELED, replyIntent)
                }
            }
            finish()
        }

    }

    private fun initialize() {
        amount = findViewById(R.id.amount_input)
        add = findViewById(R.id.add_button)
        spinner = findViewById(R.id.dropdown_menu)
        ArrayAdapter
            .createFromResource(
                this,
                R.array.spinner_options,
                R.layout.item_spinner
            )
            .also { adapter ->
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        type = parent?.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}