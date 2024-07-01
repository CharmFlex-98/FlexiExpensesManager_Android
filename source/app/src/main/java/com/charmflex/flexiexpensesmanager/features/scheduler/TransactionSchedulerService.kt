package com.charmflex.flexiexpensesmanager.features.scheduler

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.charmflex.flexiexpensesmanager.core.scheduler.FEScheduler
import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
import com.charmflex.flexiexpensesmanager.core.utils.toLocalDate
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.ScheduledTransaction
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod
import com.google.android.datatransport.runtime.scheduling.Scheduler
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

//private val UNIQUE_WORK_ID = "%d_WORK_ID"
//
//@Singleton
//internal class TransactionSchedulerService @Inject constructor(
//    private val appContext: Context,
//) : FEScheduler<ScheduledTransaction> {
//
//    override fun schedule(model: ScheduledTransaction) {
//        val inputData = Data.Builder().putAll(mapOf(InputDataKey to Json.encodeToString(model))).build()
//        val workRequest = PeriodicWorkRequestBuilder<TransactionSchedulerWorker>(1, TimeUnit.DAYS)
//            .apply {
//                setInitialDelay(getInitialDelay(model))
//            }
//            .setInputData(inputData)
//            .build()
//        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork("${model.id}_$UNIQUE_WORK_ID", ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, workRequest)
//    }
//
//    override fun unSchedule(id: Int) {
//        WorkManager.getInstance(appContext).cancelUniqueWork("${id}_$UNIQUE_WORK_ID")
//    }
//
//    private fun getInitialDelay(scheduledTransaction: ScheduledTransaction): Duration {
//        val localDateTimeNow = LocalDateTime.now()
//        val startDateTime = scheduledTransaction.startDate.toLocalDate(DATE_ONLY_DEFAULT_PATTERN)!!.atStartOfDay()
//
//        if (startDateTime.isAfter(localDateTimeNow)) {
//            return when (scheduledTransaction.schedulerPeriod) {
//                SchedulerPeriod.DAILY -> {
//                    Duration.between(localDateTimeNow, localDateTimeNow.toLocalDate().plusDays(1).atStartOfDay())
//                }
//                SchedulerPeriod.MONTHLY -> {
//                    Duration.between(localDateTimeNow, localDateTimeNow.toLocalDate().plusMonths(1).atStartOfDay())
//                }
//                SchedulerPeriod.YEARLY -> {
//                    Duration.between(localDateTimeNow, localDateTimeNow.toLocalDate().plusYears(1).atStartOfDay())
//                }
//                else -> {Duration.of(0, ChronoUnit.SECONDS)}
//            }
//        }
//
//        return when (scheduledTransaction.schedulerPeriod) {
//            SchedulerPeriod.DAILY -> {
//                Duration.between(localDateTimeNow, startDateTime.plusDays(1))
//            }
//
//            SchedulerPeriod.MONTHLY -> {
//                Duration.between(localDateTimeNow, startDateTime.plusMonths(1))
//            }
//
//            SchedulerPeriod.YEARLY -> {
//                Duration.between(localDateTimeNow, startDateTime.plusYears(1))
//            }
//            else -> {Duration.of(0, ChronoUnit.SECONDS)}
//        }
//    }
//}
//
//internal const val InputDataKey = "INPUT_DATA_KEY"