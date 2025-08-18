package com.example.bcbt


import CourseResult
import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bcbt.Constants.mySemester
import getModuleCreditSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode


val resultList = studentList

@SuppressLint("AutoboxingStateValueProperty", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultCard(resultList: List<Student>) {

    val initialLevel = studentList[0].ntaLevel.trim().toInt()
    val initialSemester = studentList[0].semester.trim().toInt()

    val sorting = listOf(
        "Level 4 Semester 1",
        "Level 4 Semester 2",
        "Level 5 Semester 1",
        "Level 5 Semester 2",
        "Level 6 Semester 1",
        "Level 6 Semester 2"
    )

    if (studentList.isEmpty() || gradeList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val sortingList = when {
        initialLevel == 4 && initialSemester == 1 -> sorting.subList(0, 1).reversed()
        initialLevel == 4 && initialSemester == 2 -> sorting.subList(0, 2).reversed()
        initialLevel == 5 && initialSemester == 1 -> sorting.subList(0, 3).reversed()
        initialLevel == 5 && initialSemester == 2 -> sorting.subList(0, 4).reversed()
        initialLevel == 6 && initialSemester == 1 -> sorting.subList(0, 5).reversed()
        initialLevel == 6 && initialSemester == 2 -> sorting.subList(0, 6).reversed()
        else -> sorting
    }

    var expanded by remember { mutableStateOf(false) }
    val gradeList = remember { mutableStateListOf<Grade>() }
    var sortItem by remember { mutableStateOf(sortingList[0]) }
    var gpaLoading by remember { mutableStateOf(false) }

    // Handle semester/level change
    LaunchedEffect(sortItem) {
        gpaLoading = true
        val (level, sem) = parseSortItem(sortItem)
        Constants.ntaLevel.intValue = level
        mySemester.intValue = sem
        Constants.completed.intValue = 0

        val loadedGrades = loadGradesSuspend(level, sem)
        gradeList.clear()
        gradeList.addAll(loadedGrades)

        // Calculate GPA
        Constants.myGPA.value = withContext(Dispatchers.Default) {
            var PN = 0.0
            var N = 0.0
            for (grade in loadedGrades) {
                val creditStr = getModuleCreditSuspend(grade.moduleCode)
                val credit = creditStr.toDoubleOrNull() ?: 0.0
                val examMark = grade.examMark.trim().toDoubleOrNull() ?: 0.0
                PN += credit * getPoint(level, grade.total, examMark)
                N += credit
            }
            if (N != 0.0) BigDecimal(PN / N).setScale(1, RoundingMode.HALF_UP).toDouble() else 0.0
        }
        gpaLoading = false
    }

    if (resultList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No results available",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val result = resultList[0]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Profile card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = GradeMateColors.back1),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .border(
                        width = 0.5.dp,
                        color = GradeMateColors.Primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.dp),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .border(3.dp, GradeMateColors.Primary, CircleShape)
                                .shadow(6.dp, CircleShape)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = result.studentName,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GradeMateColors.Primary,
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = result.regNo,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ResultSummary(R.drawable.tick)
                        ResultSummary(R.drawable.cross)
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (gpaLoading) {
                                BouncingDots()
                            } else {
                                GPABox()
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (Constants.myGPA.value > 0.0) awardName() else "",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = sortItem,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GradeMateColors.Primary
                    ),
                    label = {
                        Text(
                            "Semester Results",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GradeMateColors.Primary
                            )
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = GradeMateColors.Primary
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = GradeMateColors.Primary,
                        unfocusedBorderColor = GradeMateColors.Primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.Transparent)
                ) {
                    sortingList.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    item,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GradeMateColors.Primary
                                    )
                                )
                            },
                            onClick = {
                                sortItem = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .height(300.dp)
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                CourseResult(
                    grades = gradeList,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun BouncingDots(dotCount: Int = 3) {
    val infiniteTransition = rememberInfiniteTransition()
    val scales = List(dotCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = LinearEasing, delayMillis = index * 150),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(30.dp)
    ) {
        scales.forEach { scale ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(GradeMateColors.Primary)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
            )
        }
    }
}

