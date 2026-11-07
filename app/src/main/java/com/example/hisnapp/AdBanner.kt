package com.example.hisnapp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

// رقم وحدة الإعلان التجريبي. نستبدله برقمك الحقيقي لاحقاً
const val BANNER_ID = "ca-app-pub-3940256099942544/6300978111"

@Composable
fun AdBanner() {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = BANNER_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
