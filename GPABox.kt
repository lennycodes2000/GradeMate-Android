package com.example.bcbt

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GPABox(
    gpaLoading: Boolean = false // pass true if GPA is being calculated
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .border(
                width = 6.dp,
                brush = GradeMateColors.gpaGradient,
                shape = CircleShape
            )
            .background(GradeMateColors.backGradient, CircleShape)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (gpaLoading) {
                BouncingDots()
            } else {
                Text(
                    text = if (Constants.myGPA.doubleValue > 0.0) Constants.myGPA.doubleValue.toString() else "...",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = GradeMateColors.Secondary,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }

            Text(
                text = "GPA",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = GradeMateColors.Primary,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
        }
    }
}
