package com.example.bcbt

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.webkit.*
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: androidx.navigation.NavController) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Improved Back Handling
    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            navController.navigate(Routes.splash) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
  StatusBar()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.gradeSplash)
                },
                modifier = Modifier.padding(16.dp),
                containerColor = GradeMateColors.Primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.grademate),
                    contentDescription = "Open GradeMate",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(GradeMateColors.Primary)
            )
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                isRefreshing = true
                webView.reload()
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    webView.apply {
                        settings.javaScriptEnabled = true
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val url = request?.url.toString()
                                return if (url.startsWith("http")) {
                                    false
                                } else {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                        context.startActivity(intent)
                                        true
                                    } catch (e: ActivityNotFoundException) {
                                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                        true
                                    }
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                coroutineScope.launch {
                                    isRefreshing = false
                                }
                            }
                        }

                        webChromeClient = WebChromeClient()

                        setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
                            try {
                                val request = DownloadManager.Request(url.toUri())
                                request.setMimeType(mimeType)
                                request.addRequestHeader("User-Agent", userAgent)
                                request.setDescription("Downloading file...")
                                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
                                request.allowScanningByMediaScanner()
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                request.setDestinationInExternalPublicDir(
                                    android.os.Environment.DIRECTORY_DOWNLOADS,
                                    URLUtil.guessFileName(url, contentDisposition, mimeType)
                                )

                                val dm = context.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as DownloadManager
                                dm.enqueue(request)

                                Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }

                        if (url == null || url != "https://bcbtcollege.ac.tz/") {
                            loadUrl("https://bcbtcollege.ac.tz/")
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
@Composable
fun StatusBar(){
    val systemUiController = rememberSystemUiController()

    // Change status bar color
    SideEffect {
        systemUiController.setStatusBarColor(
            color = GradeMateColors.Primary, // your desired color
            darkIcons = true // true = dark icons, false = light icons
        )
    }
}
@Preview
@Composable
fun HomePreview() {
    Home(navController = rememberNavController())
}
