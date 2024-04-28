package com.charmflex.flexiexpensesmanager.features.home.ui.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AccountHomeViewModel @Inject constructor(
    val db: AppDatabase
) : ViewModel() {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val res = db.getAccountDao().getAllAccounts()
            res.forEach {
                Log.d("test", it.toString())
            }
        }
    }
}