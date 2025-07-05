package com.example.bcbt

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Splash(navController: NavController) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(20f) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        launch { scale.animateTo(1f, tween(1000, easing = EaseOutBounce)) }
        launch { rotation.animateTo(360f, tween(1000, easing = LinearOutSlowInEasing)) }
        launch { alpha.animateTo(1f, tween(800)) }
        launch { offsetY.animateTo(0f, tween(600, easing = FastOutSlowInEasing)) }

        delay(2500)
        navController.navigate(Routes.home) {
            popUpTo(Routes.splash) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A1B9A), // Purple top
                        Color(0xFF283593)  // Blue bottom
                    )
                )
            )
    ) {
        // Main content centered
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo), // your logo resource
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value,
                        rotationZ = rotation.value,
                        alpha = alpha.value
                    )
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom clickable footer
        Text(
            text = "Developed by lennycodes for BCBT © 2025 ❤️",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .alpha(alpha.value),
            style = MaterialTheme.typography.titleSmall,
            color = Color.LightGray
        )
    }
}
