package com.example.bcbt

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Landing() {
    val ElegantBorder = Color(0xFFCFD8DC)
    val CardBackground = Color.White

    // If no students yet, show loader and try to load them
    if (studentList.isEmpty()) {
        LaunchedEffect(Unit) {
            // Load once
            loadStudent()
            try {
                loadGrades(Constants.ntaLevel.intValue, Constants.mySemester.intValue)
            } catch (e: Exception) {
                Log.e("Landing", "loadGrades failed: $e")
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GradeMateColors.Primary)
        }
        return
    }

    // Now safe to access first student
    val student = remember(studentList) { studentList.first() }
    val charGender = remember(student) { if (student.gender == "Male") "M" else "F" }

    // Safe credit parsing (use defaults if not provided)
    val totalCredits = Auth.totalCredits.value.toString()
    val totalModules = Auth.noModules.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top section with Primary background
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GradeMateColors.Primary)
                .padding(top = 32.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            ) {
                // Profile Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                        .border(
                            width = 1.dp,
                            color = ElegantBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${student.studentName} ( $charGender )",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GradeMateColors.Primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = student.regNo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    }
                }

                // Overlapping Profile Image
                Image(
                    painter = painterResource(R.drawable.dp),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-48).dp)
                        .clip(CircleShape)
                        .border(3.dp, GradeMateColors.Primary, CircleShape)
                        .shadow(elevation = 8.dp, shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Spacer filled with primary color before white section
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(GradeMateColors.Primary)
        )

        // White bottom section with rounded top
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Row with SmallBoxes for NTA Level, Semester, Program
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallBox(
                    title = "NTA Level",
                    value = student.ntaLevel,
                    modifier = Modifier.weight(1f)
                )
                SmallBox(
                    title = "Semester",
                    value = student.semester,
                    modifier = Modifier.weight(1f)
                )
                SmallBox(
                    title = "Program",
                    value = student.program,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Semester Modules",
                fontSize = 18.sp,
                color = GradeMateColors.Primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            // Summary Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Modules: $totalModules",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Text(
                    text = "Total Credits: $totalCredits",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }

            // Module list - ModuleList should handle empty moduleList itself
            ModuleList(modules = moduleList, studentProgram = student.program)
        }
    }
}

@Composable
fun SmallBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1f) // Make it square
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, GradeMateColors.Primary, RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = GradeMateColors.Primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )
    }
}


