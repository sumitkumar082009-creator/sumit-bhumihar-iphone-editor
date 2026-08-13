package com.example

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.EditorScreen
import com.example.ui.MainScreen
import com.example.ui.theme.IosDarkBackground
import com.example.ui.theme.SumitEditorTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : ComponentActivity() {
            private var mInterstitialAd: InterstitialAd? = null

                override fun onCreate(savedInstanceState: Bundle?) {
                                super.onCreate(savedInstanceState)
                                        
                                                // 1. AdMob चालू करना (Initialize)
                                                        MobileAds.initialize(this) {}
                                                                loadInterstitialAd()

                                                                        enableEdgeToEdge()
                                                                                setContent {
                                                                                                    SumitEditorTheme {
                                                                                                                        Surface(
                                                                                                                                                    modifier = Modifier.fillMaxSize(),
                                                                                                                                                                        color = IosDarkBackground
                                                                                                                        ) {
                                                                                                                                                    SumitEditorApp(
                                                                                                                                                                                onShowInterstitial = { showInterstitialAd() }
                                                                                                                                                    )
                                                                                                                        }
                                                                                                    }
                                                                                }
                }

                    // इंटरस्टीशियल ऐड लोड करने का फंक्शन
                        private fun loadInterstitialAd() {
                                        val adRequest = AdRequest.Builder().build()
                                                // यहाँ आपकी असली Interstitial ID डाली गई है
                                                        InterstitialAd.load(this, "ca-app-pub-5440426791493208/6174540968", adRequest, object : InterstitialAdLoadCallback() {
                                                                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                                                                                mInterstitialAd = null
                                                                            }
                                                                                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                                                                                                mInterstitialAd = interstitialAd
                                                                                        }
                                                        })
                        }

                            // इंटरस्टीशियल ऐड दिखाने का फंक्शन
                                private fun showInterstitialAd() {
                                                if (mInterstitialAd != null) {
                                                                    mInterstitialAd?.show(this)
                                                                                loadInterstitialAd() // एक बार दिखने के बाद अगला ऐड लोड करने के लिए 
                                                } else {
                                                                    Log.d("AdMob", "Ad abhi load nahi hua hai.")
                                                }
                                }
}

@Composable
fun SumitEditorApp(onShowInterstitial: () -> Unit) {
            var activeImageUri by remember { mutableStateOf<Uri?>(null) }

                // पूरी स्क्रीन को दो हिस्सों में बाँटना: ऊपर ऐप, नीचे बैनर ऐड
                    Column(modifier = Modifier.fillMaxSize()) {
                                
                                        // मुख्य ऐप (यह बची हुई पूरी जगह लेगा)
                                                Box(modifier = Modifier.weight(1f)) {
                                                                    Crossfade(
                                                                                        targetState = activeImageUri,
                                                                                                        label = "ScreenTransition"
                                                                    ) { uri ->
                                                                                    if (uri == null) {
                                                                                                            MainScreen(
                                                                                                                                        onNavigateToEditor = { selectedUri ->
                                                                                                                                                                    // जब भी फोटो सिलेक्ट करेंगे, फुल स्क्रीन ऐड दिखेगा!
                                                                                                                                                                                                onShowInterstitial() 
                                                                                                                                                                                                                            activeImageUri = selectedUri
                                                                                                                                                                                                                                                    }
                                                                                                            )
                                                                                    } else {
                                                                                                            EditorScreen(
                                                                                                                                        imageUri = uri,
                                                                                                                                                                onBack = {
                                                                                                                                                                                                    activeImageUri = null
                                                                                                                                                                }
                                                                                                            )
                                                                                    }
                                                                    }
                                                }

                                                        // 2. बैनर ऐड का कोड (स्क्रीन के सबसे नीचे)
                                                                AndroidView(
                                                                                    modifier = Modifier.fillMaxWidth(),
                                                                                                factory = { context ->
                                                                                                                AdView(context).apply {
                                                                                                                                            setAdSize(AdSize.BANNER)
                                                                                                                                                                // यहाँ आपकी असली Banner ID डाली गई है
                                                                                                                                                                                    adUnitId = "ca-app-pub-5440426791493208/5701461344"
                                                                                                                                                                                                        loadAd(AdRequest.Builder().build())
                                                                                                                }
                                                                                                }
                                                                )
                    }
}
