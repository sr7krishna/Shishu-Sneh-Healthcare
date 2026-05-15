package com.example.shishu_sneh_healthcare.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object VaccineReminderScheduler {

    fun scheduleReminder(

        context: Context,

        babyId: Long

    ) {

        val inputData = Data.Builder()

            .putLong("babyId", babyId)

            .build()

        val workRequest =

            PeriodicWorkRequestBuilder<
                    VaccineReminderWorker
                    >(
                24,
                TimeUnit.HOURS
            )

                .setInputData(inputData)

                .build()

        WorkManager.getInstance(context)

            .enqueueUniquePeriodicWork(

                "vaccine_reminder_$babyId",

                ExistingPeriodicWorkPolicy.UPDATE,

                workRequest
            )
    }
}