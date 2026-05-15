package com.example.shishu_sneh_healthcare.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shishu_sneh_healthcare.domain.repository.VaccineRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat

@HiltWorker
class VaccineReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val vaccineRepository: VaccineRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val babyId =
            inputData.getLong(
                "babyId",
                -1L
            )

        if (babyId == -1L) {

            return Result.failure()
        }

        val vaccines =
            vaccineRepository
                .getOverdueVaccines(babyId)
                .first()

        if (vaccines.isNotEmpty()) {

            val vaccine =
                vaccines.first()

            showNotification(

                title = "Vaccine Reminder 💉",

                message =
                    "${vaccine.name} vaccine is overdue. Please schedule it soon."
            )
        }

        return Result.success()
    }private fun showNotification(

        title: String,

        message: String

    ) {

        val channelId =
            "vaccine_reminder_channel"

        val notificationManager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        // Create channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(

                channelId,

                "Vaccine Reminders",

                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description =
                "Notifications for overdue vaccines"

            notificationManager
                .createNotificationChannel(channel)
        }

        val notification =

            NotificationCompat.Builder(
                applicationContext,
                channelId
            )

                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )

                .setContentTitle(title)

                .setContentText(message)

                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )

                .setAutoCancel(true)

                .build()

        notificationManager.notify(
            1001,
            notification
        )
    }
}
