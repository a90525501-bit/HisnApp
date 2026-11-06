package com.example.hisnapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PrayerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleAdhan(context)
            return
        }
        val name = intent.getStringExtra("name") ?: "الصلاة"
        showAdhanNotification(context, name)
        scheduleAdhan(context)
    }
}
