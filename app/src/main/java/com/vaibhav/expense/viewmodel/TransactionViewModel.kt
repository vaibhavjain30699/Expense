package com.vaibhav.expense

import androidx.lifecycle.*
import com.vaibhav.expense.Model.Transaction
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val allTransactions: LiveData<List<Transaction>> = repository.allTransactions.asLiveData()
    val totalSpent: LiveData<Int> = repository.totalSpent.asLiveData()

    fun getTodaySpent(time: Long): LiveData<Int>{
        return repository.getTodaySpent(time).asLiveData()
    }

    fun insert(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }

    fun delete(transaction: Transaction) = viewModelScope.launch {
        repository.delete(transaction)
    }

}

class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}