package com.example.hisnapp

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var city by remember { mutableStateOf(savedCity(context)) }
    var enabled by remember { mutableStateOf(prefs(context).getBoolean("adhan", false)) }
    var menu by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    val fmt = remember { SimpleDateFormat("hh:mm a", Locale("ar")) }
    val prayers = prayersFor(city, Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مواقيت الصلاة") },
                navigationIcon = { TextButton(onClick = onBack) { Text("رجوع") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box {
                OutlinedButton(onClick = { menu = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("المدينة: ${city.name}")
                }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    cities.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.name) },
                            onClick = {
                                city = c
                                menu = false
                                prefs(context).edit().putString("city", c.name).apply()
                                scheduleAdhan(context)
                            }
                        )
                    }
                }
            }

            prayers.forEach { p ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(p.name, style = MaterialTheme.typography.titleLarge)
                        Text(fmt.format(p.time), style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("تنبيه الأذان", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = enabled,
                    onCheckedChange = { on ->
                        enabled = on
                        prefs(context).edit().putBoolean("adhan", on).apply()
                        if (on && Build.VERSION.SDK_INT >= 33) {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        scheduleAdhan(context)
                    }
                )
            }

            OutlinedButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= 33) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    showAdhanNotification(context, "تجربة")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تجربة الأذان الآن")
            }
        }
    }
}
