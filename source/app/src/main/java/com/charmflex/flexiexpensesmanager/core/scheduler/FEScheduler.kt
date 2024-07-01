package com.charmflex.flexiexpensesmanager.core.scheduler

import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerDomainModel

internal interface FEScheduler<T: SchedulerDomainModel> {
    fun schedule(model: T)

    fun unSchedule(id: Int)
}