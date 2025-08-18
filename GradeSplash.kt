package com.example.bcbt

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay

@Composable
fun GradeSplash(navController: NavController){
    var typedText by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    val fullText = "GradeMate"
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800)
    )

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(800, easing = FastOutSlowInEasing)
    )
    LaunchedEffect(Unit) {
        visible = true

        // Typing effect
        for (i in 1..fullText.length) {
            typedText = fullText.substring(0, i)
            delay(100)
        }

        delay(3500) // Splash pause
        //going to gradeHome
        val user = Firebase.auth.currentUser

        if (user != null && user.email != null) {
            Auth.setId( convertEmailToRegNo(user.email!!))
            Log.d("Azir", Auth.id.value)
            loadStudent()            // asynchronously load student info

            navController.navigate(Routes.gradeHome) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(Routes.login) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

StatusBar()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GradeMateColors.Primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = typedText,
            fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LoadingDots()
    }
}
@Composable
fun LoadingDots() {
    val dotCount = 3
    val delayPerDot = 300L

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) { index ->
            val animatedAlpha = rememberInfiniteTransition().animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        easing = LinearEasing,
                        delayMillis = index * delayPerDot.toInt()
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .alpha(animatedAlpha.value)
                    .background(Color.White, shape = MaterialTheme.shapes.small)
            )
        }
    }
}
//Function to convert email to regno
fun convertEmailToRegNo(email: String): String {
    val username = email.substringBefore('@') // "ns217500242015"
    if (username.length != 14) return "Invalid format"

    val part1 = username.substring(0, 6).uppercase()      // NS2175
    val part2 = username.substring(6, 10)                 // 0024
    val part3 = username.substring(10, 14)                // 2015

    return "$part1/$part2/$part3"
}