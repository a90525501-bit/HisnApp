package com.example.hisnapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarScreen(title: String, azkar: List<Zikr>, onBack: () -> Unit) {
    val counters = remember { mutableStateListOf<Int>().apply { azkar.forEach { add(it.count) } } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { TextButton(onClick = onBack) { Text("رجوع") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(azkar) { index, zikr ->
                val left = counters[index]
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        if (counters[index] > 0) counters[index] = counters[index] - 1
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (zikr.reward.isNotEmpty()) {
                            Text(zikr.reward, style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.height(6.dp))
                        }
                        Text(zikr.text, fontSize = 20.sp, lineHeight = 34.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (left == 0) "تم ✅" else "المتبقي: $left من ${zikr.count}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (left != zikr.count) {
                            TextButton(onClick = { counters[index] = zikr.count }) { Text("إعادة") }
                        }
                    }
                }
            }
            // مكان إعلان AdMob لاحقاً
            item { Spacer(Modifier.height(60.dp)) }
        }
    }
}
