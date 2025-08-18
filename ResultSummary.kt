package com.example.bcbt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.round


@Composable
fun ResultSummary(
    iconResId: Int
) {
    //getting the total passed and total failed
    fun isPassed(c: Grade, minTotal: Int): Boolean {
        val total = c.total
        val ue = c.examMark.toDoubleOrNull() ?: 0.0
        return total >= minTotal && ue >= 20
    }

    fun isFailed(c: Grade, minTotal: Int): Boolean {
        val total = c.total
        val ue = c.examMark.toDoubleOrNull() ?: 0.0
        return total < minTotal || ue < 20
    }

    val (passMark, passMarkLevel6) = 50 to 45

    val totalPassed = gradeList.count { isPassed(it, passMark) }

    val totalFailed = gradeList.count { isFailed(it, passMark) }
    val totalPassedPercent = if (totalPassed + totalFailed > 0) {
        round((totalPassed.toDouble() / (totalPassed + totalFailed) * 100) * 10) / 10
    } else 0.0

    val totalFailedPercent = if (totalPassed + totalFailed > 0) {
        round((totalFailed.toDouble() / (totalPassed + totalFailed) * 100) * 10) / 10
    } else 0.0


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(GradeMateColors.backGradient, shape = RoundedCornerShape(12.dp))
            .padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
    ) {

        if(iconResId == R.drawable.tick){
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
                tint = GradeMateColors.Secondary ,
                modifier = Modifier.size(32.dp)
            )
        }
        else{
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = GradeMateColors.Error ,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if(iconResId == R.drawable.tick) "Passed" else "Failed",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if(iconResId == R.drawable.tick) GradeMateColors.Secondary else GradeMateColors.Error,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if(iconResId == R.drawable.tick) "$totalPassedPercent%" else "$totalFailedPercent%",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}