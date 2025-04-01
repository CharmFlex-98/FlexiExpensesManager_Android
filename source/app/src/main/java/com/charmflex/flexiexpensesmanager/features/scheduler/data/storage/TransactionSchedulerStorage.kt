package com.charmflex.flexiexpensesmanager.features.scheduler.data.storage

import com.charmflex.flexiexpensesmanager.core.storage.SharedPrefs
import javax.inject.Inject

private const val TRANSACTION_SCHEDULER_LATEST_ID = "TRANSACTION_SCHEDULER_LATEST_ID"
internal interface TransactionSchedulerStorage {
    fun setLatestId(id: Int)

    fun getLatestId(): Int


}

internal class TransactionSchedulerStorageImpl @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : TransactionSchedulerStorage {
    override fun setLatestId(id: Int) {
        sharedPrefs.setInt(TRANSACTION_SCHEDULER_LATEST_ID, id)
    }

    override fun getLatestId(): Int {
        return sharedPrefs.getInt(TRANSACTION_SCHEDULER_LATEST_ID, -1)
    }
}