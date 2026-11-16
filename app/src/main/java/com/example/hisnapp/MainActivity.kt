package com.example.hisnapp

import android.icu.util.ULocale
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.icu.text.SimpleDateFormat as IcuFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = 0xFF03201B.toInt()
        window.navigationBarColor = 0xFF03201B.toInt()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        scheduleAdhan(this)
        setContent {
            AppTheme {
                var screen by remember { mutableStateOf("home") }
                when (screen) {
                    "morning" -> AzkarScreen("أذكار الصباح", morningAzkar) { screen = "home" }
                    "evening" -> AzkarScreen("أذكار المساء", eveningAzkar) { screen = "home" }
                    "dua" -> AzkarScreen("أدعية مختارة", duaList) { screen = "home" }
                    "prayer" -> PrayerScreen { screen = "home" }
                    else -> HomeScreen { screen = it }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onOpen: (String) -> Unit) {
    val gold = MaterialTheme.colorScheme.primary
    val bg = MaterialTheme.colorScheme.background
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(1000L)
        }
    }
    val timeFmt = remember { SimpleDateFormat("hh:mm a", Locale("ar")) }
    val hijriFmt = remember { IcuFormat("d MMMM y", ULocale("ar@calendar=islamic-umalqura")) }
    val gregFmt = remember { SimpleDateFormat("d MMMM y", Locale("ar")) }

    val items = listOf(
        Triple("🌅", "أذكار الصباح", "morning"),
        Triple("🌙", "أذكار المساء", "evening"),
        Triple("🤲", "أدعية مختارة", "dua"),
        Triple("🕌", "مواقيت الصلاة", "prayer")
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF0E4A3F), bg)))
                .padding(top = 40.dp, bottom = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("حصن المسلم", color = gold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))
                Text(timeFmt.format(now), color = Color(0xFFEFE6CF), fontSize = 46.sp)
                Spacer(Modifier.height(6.dp))
                Text(hijriFmt.format(now) + " هـ", color = gold, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                Text(gregFmt.format(now), color = Color(0xFFEFE6CF).copy(alpha = 0.8f), fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { (icon, title, key) ->
                        Tile(icon, title, Modifier.weight(1f)) { onOpen(key) }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun Tile(icon: String, title: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 22.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 36.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            title,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
