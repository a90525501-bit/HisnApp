package com.example.hisnapp

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.*

data class City(
    val name: String, val lat: Double, val lon: Double,
    val fajr: Double, val isha: Double, val ishaMin: Int = 0
)

data class PrayerItem(val name: String, val time: Date)

val cities = listOf(
    City("مكة المكرمة", 21.4225, 39.8262, 18.5, 0.0, 90),
    City("المدينة المنورة", 24.4686, 39.6142, 18.5, 0.0, 90),
    City("الرياض", 24.7136, 46.6753, 18.5, 0.0, 90),
    City("جدة", 21.5433, 39.1728, 18.5, 0.0, 90),
    City("الدمام", 26.4207, 50.0888, 18.5, 0.0, 90),
    City("صنعاء", 15.3694, 44.1910, 18.5, 0.0, 90),
    City("القاهرة", 30.0444, 31.2357, 19.5, 17.5),
    City("الإسكندرية", 31.2001, 29.9187, 19.5, 17.5),
    City("الخرطوم", 15.5007, 32.5599, 19.5, 17.5),
    City("طرابلس", 32.8872, 13.1913, 19.5, 17.5),
    City("دبي", 25.2048, 55.2708, 18.2, 18.2),
    City("أبوظبي", 24.4539, 54.3773, 18.2, 18.2),
    City("الدوحة", 25.2854, 51.5310, 18.0, 0.0, 90),
    City("الكويت", 29.3759, 47.9774, 18.0, 17.5),
    City("المنامة", 26.2285, 50.5860, 18.0, 17.0),
    City("مسقط", 23.5880, 58.3829, 18.0, 18.0),
    City("عمّان", 31.9454, 35.9284, 18.0, 18.0),
    City("القدس", 31.7683, 35.2137, 18.0, 18.0),
    City("دمشق", 33.5138, 36.2765, 18.0, 18.0),
    City("بيروت", 33.8938, 35.5018, 18.0, 18.0),
    City("بغداد", 33.3152, 44.3661, 18.0, 18.0),
    City("تونس", 36.8065, 10.1815, 18.0, 18.0),
    City("الجزائر", 36.7538, 3.0588, 18.0, 17.0),
    City("الرباط", 34.0209, -6.8416, 18.0, 17.0),
    City("إسطنبول", 41.0082, 28.9784, 18.0, 17.0)
)

fun prefs(c: Context) = c.getSharedPreferences("hisn", Context.MODE_PRIVATE)

fun savedCity(c: Context): City =
    cities.firstOrNull { it.name == prefs(c).getString("city", "") } ?: cities[0]

private fun rad(d: Double) = Math.toRadians(d)
private fun deg(r: Double) = Math.toDegrees(r)

fun prayersFor(city: City, date: Date): List<PrayerItem> {
    val cal = Calendar.getInstance()
    cal.time = date
    val y = cal.get(Calendar.YEAR)
    val m = cal.get(Calendar.MONTH) + 1
    val d = cal.get(Calendar.DAY_OF_MONTH)

    val a = (14 - m) / 12
    val yy = y + 4800 - a
    val mm = m + 12 * a - 3
    val jdn = d + (153 * mm + 2) / 5 + 365 * yy + yy / 4 - yy / 100 + yy / 400 - 32045
    val n = jdn - 2451545.0 - 0.5 + 0.0008

    val g = (357.5291 + 0.98560028 * n) % 360
    val q = (280.459 + 0.98564736 * n) % 360
    val lambda = q + 1.915 * sin(rad(g)) + 0.020 * sin(rad(2 * g))
    val eps = 23.439 - 0.00000036 * n
    val decl = asin(sin(rad(eps)) * sin(rad(lambda)))
    val ra = deg(atan2(cos(rad(eps)) * sin(rad(lambda)), cos(rad(lambda)))) / 15.0
    val eqt = q / 15.0 - ((ra % 24) + 24) % 24
    val eqtFix = if (eqt > 12) eqt - 24 else if (eqt < -12) eqt + 24 else eqt

    val noon = 12 - city.lon / 15.0 - eqtFix
    val latR = rad(city.lat)

    fun hourAngle(angle: Double): Double {
        val v = (sin(rad(-angle)) - sin(latR) * sin(decl)) / (cos(latR) * cos(decl))
        return deg(acos(v.coerceIn(-1.0, 1.0))) / 15.0
    }

    val sunset = noon + hourAngle(0.833)
    val fajr = noon - hourAngle(city.fajr)
    val asrAlt = deg(atan(1.0 / (1.0 + tan(abs(latR - decl)))))
    val asr = noon + hourAngle(-asrAlt)
    val isha = if (city.ishaMin > 0) sunset + city.ishaMin / 60.0 else noon + hourAngle(city.isha)

    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utc.clear()
    utc.set(y, m - 1, d, 0, 0, 0)
    val base = utc.timeInMillis

    fun toDate(h: Double) = Date(base + (h * 3600000.0).toLong())

    return listOf(
        PrayerItem("الفجر", toDate(fajr)),
        PrayerItem("الظهر", toDate(noon + 0.0333)),
        PrayerItem("العصر", toDate(asr)),
        PrayerItem("المغرب", toDate(sunset + 0.0333)),
        PrayerItem("العشاء", toDate(isha))
    )
}

private fun alarmIntent(context: Context, name: String): PendingIntent {
    val i = Intent(context, PrayerReceiver::class.java).putExtra("name", name)
    return PendingIntent.getBroadcast(
        context, 1, i,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

// يفتح صفحة إذن المنبّهات الدقيقة مرة واحدة فقط
private fun askExactAlarm(context: Context) {
    if (Build.VERSION.SDK_INT < 31 || context !is Activity) return
    if (prefs(context).getBoolean("asked_exact", false)) return
    prefs(context).edit().putBoolean("asked_exact", true).apply()
    try {
        context.startActivity(
            Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:" + context.packageName)
            )
        )
    } catch (e: Exception) {
    }
}

fun scheduleAdhan(context: Context) {
    try {
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
        val next = upcoming.filter { it.time.time > now + 1000 }
            .minByOrNull { it.time.time } ?: return

        val canExact = Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms()
        if (canExact) {
            val showIntent = PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
            am.setAlarmClock(
                AlarmManager.AlarmClockInfo(next.time.time, showIntent),
                alarmIntent(context, next.name)
            )
        } else {
            am.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, next.time.time, alarmIntent(context, next.name)
            )
            askExactAlarm(context)
        }
    } catch (e: Exception) {
        // لا نوقف التطبيق إن فشلت الجدولة
    }
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
