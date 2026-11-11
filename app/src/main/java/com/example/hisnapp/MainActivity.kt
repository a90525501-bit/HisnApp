package com.example.hisnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("☪", fontSize = 40.sp, color = Color.White)
                Text(
                    "حصن المسلم",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "أذكار وأدعية ومواقيت الصلاة",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
            }
        }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuCard("🌅", "أذكار الصباح") { onOpen("morning") }
            MenuCard("🌙", "أذكار المساء") { onOpen("evening") }
            MenuCard("🤲", "أدعية مختارة") { onOpen("dua") }
            MenuCard("🕌", "مواقيت الصلاة") { onOpen("prayer") }
        }
    }
}

@Composable
fun MenuCard(icon: String, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.width(16.dp))
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }
    }
}
