package com.example.hisnapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import java.util.Calendar
import java.util.Date

data class City(val name: String, val lat: Double, val lon: Double, val method: String)

data class PrayerItem(val name: String, val time: Date)

val cities = listOf(
    City("مكة المكرمة", 21.4225, 39.8262, "UMM_AL_QURA"),
    City("المدينة المنورة", 24.4686, 39.6142, "UMM_AL_QURA"),
    City("الرياض", 24.7136, 46.6753, "UMM_AL_QURA"),
    City("جدة", 21.5433, 39.1728, "UMM_AL_QURA"),
    City("الدمام", 26.4207, 50.0888, "UMM_AL_QURA"),
    City("صنعاء", 15.3694, 44.1910, "UMM_AL_QURA"),
    City("القاهرة", 30.0444, 31.2357, "EGYPTIAN"),
    City("الإسكندرية", 31.2001, 29.9187, "EGYPTIAN"),
    City("الخرطوم", 15.5007, 32.5599, "EGYPTIAN"),
    City("طرابلس", 32.8872, 13.1913, "EGYPTIAN"),
    City("دبي", 25.2048, 55.2708, "DUBAI"),
    City("أبوظبي", 24.4539, 54.3773, "DUBAI"),
    City("الدوحة", 25.2854, 51.5310, "QATAR"),
    City("الكويت", 29.3759, 47.9774, "KUWAIT"),
    City("المنامة", 26.2285, 50.5860, "MUSLIM_WORLD_LEAGUE"),
    City("مسقط", 23.5880, 58.3829, "MUSLIM_WORLD_LEAGUE"),
    City("عمّان", 31.9454, 35.9284, "MUSLIM_WORLD_LEAGUE"),
    City("القدس", 31.7683, 35.2137, "MUSLIM_WORLD_LEAGUE"),
    City("دمشق", 33.5138, 36.2765, "MUSLIM_WORLD_LEAGUE"),
    City("بيروت", 33.8938, 35.5018, "MUSLIM_WORLD_LEAGUE"),
    City("بغداد", 33.3152, 44.3661, "MUSLIM_WORLD_LEAGUE"),
    City("تونس", 36.8065, 10.1815, "MUSLIM_WORLD_LEAGUE"),
    City("الجزائر", 36.7538, 3.0588, "MUSLIM_WORLD_LEAGUE"),
    City("الرباط", 34.0209, -6.8416, "MUSLIM_WORLD_LEAGUE"),
    City("إسطنبول", 41.0082, 28.9784, "TURKEY")
)

fun prefs(c: Context) = c.getSharedPreferences("hisn", Context.MODE_PRIVATE)

fun savedCity(c: Context): City =
    cities.firstOrNull { it.name == prefs(c).getString("city", "") } ?: cities[0]

fun prayersFor(city: City, date: Date): List<PrayerItem> {
    val cal = Calendar.getInstance()
    cal.time = date
    val comps = DateComponents(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH)
    )
    val params = CalculationMethod.valueOf(city.method).parameters
    val pt = PrayerTimes(Coordinates(city.lat, city.lon), comps, params)
    return listOf(
        PrayerItem("الفجر", pt.fajr),
        PrayerItem("الظهر", pt.dhuhr),
        PrayerItem("العصر", pt.asr),
        PrayerItem("المغرب", pt.maghrib),
        PrayerItem("العشاء", pt.isha)
    )
}

private fun alarmIntent(context: Context, name: String): PendingIntent {
    val i = Intent(context, PrayerReceiver::class.java).putExtra("name", name)
    return PendingIntent.getBroadcast(
        context, 1, i,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

fun scheduleAdhan(context: Context) {
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (!prefs(context).getBoolean("adhan", false)) {
        am.cancel(alarmIntent(context, ""))
        return
    }

    val city = savedCity(context)
    val now = System.currentTimeMillis()
    val upcoming = mutableListOf<PrayerItem>()
    for (day in 0..1) {
        upcoming += prayersFor(city, Date(now + day * 86400000L))
    }
    val next = upcoming.filter { it.time.time > now + 1000 }.minByOrNull { it.time.time } ?: return

    val showIntent = PendingIntent.getActivity(
        context, 0, Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )
    am.setAlarmClock(
        AlarmManager.AlarmClockInfo(next.time.time, showIntent),
        alarmIntent(context, next.name)
    )
}

fun showAdhanNotification(context: Context, name: String) {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= 26) {
        val channel = NotificationChannel("adhan", "الأذان", NotificationManager.IMPORTANCE_HIGH)
        nm.createNotificationChannel(channel)
    }
    val open = PendingIntent.getActivity(
        context, 0, Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )
    val n = NotificationCompat.Builder(context, "adhan")
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("حان الآن موعد أذان $name")
        .setContentText("حيّ على الصلاة")
        .setContentIntent(open)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()
    nm.notify(100, n)
}
