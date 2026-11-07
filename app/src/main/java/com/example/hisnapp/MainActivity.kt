package com.example.hisnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        scheduleAdhan(this)
        setContent {
            MaterialTheme {
                var screen by remember { mutableStateOf("home") }
                Column(Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f)) {
                        when (screen) {
                            "morning" -> AzkarScreen("أذكار الصباح", morningAzkar) { screen = "home" }
                            "evening" -> AzkarScreen("أذكار المساء", eveningAzkar) { screen = "home" }
                            "dua" -> AzkarScreen("أدعية مختارة", duaList) { screen = "home" }
                            "prayer" -> PrayerScreen { screen = "home" }
                            else -> HomeScreen { screen = it }
                        }
                    }
                    AdBanner()
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onOpen: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("حصن المسلم", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))
        Button(onClick = { onOpen("morning") }, modifier = Modifier.fillMaxWidth()) {
            Text("أذكار الصباح")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onOpen("evening") }, modifier = Modifier.fillMaxWidth()) {
            Text("أذكار المساء")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onOpen("dua") }, modifier = Modifier.fillMaxWidth()) {
            Text("أدعية مختارة")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onOpen("prayer") }, modifier = Modifier.fillMaxWidth()) {
            Text("مواقيت الصلاة")
        }
    }
}
