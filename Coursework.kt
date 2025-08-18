package com.example.bcbt

import GradeCard
import GradeSummary
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bcbt.Constants.mySemester
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.bcbt.Constants.ntaLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun Coursework(
    modifier: Modifier = Modifier,
    studentList: List<Student>,
    gradeList: List<Grade>,
    isLoading: Boolean,
    loadStudent: suspend () -> Unit,
    loadGrades: suspend (Int, Int) -> Unit,
) {
    var ntaLevel by remember {
        mutableIntStateOf(0)
    }
    var semester by remember {
        mutableIntStateOf(0)
    }
    var coroutineScope = rememberCoroutineScope ()
    // Extract student's NTA level and semester safely
    if(studentList.isNotEmpty()){
         ntaLevel = studentList.firstOrNull()?.ntaLevel?.trim()?.toIntOrNull() ?: 4
         semester = studentList.firstOrNull()?.semester?.trim()?.toIntOrNull() ?: 1
    }


    // Sorting options
    val sorting = listOf(
        "Level 4 Semester 1",
        "Level 4 Semester 2",
        "Level 5 Semester 1",
        "Level 5 Semester 2",
        "Level 6 Semester 1",
        "Level 6 Semester 2"
    )

    val sortingList = when {
        ntaLevel == 4 && semester == 1 -> sorting.subList(0, 1).reversed()
        ntaLevel == 4 && semester == 2 -> sorting.subList(0, 2).reversed()
        ntaLevel == 5 && semester == 1 -> sorting.subList(0, 3).reversed()
        ntaLevel == 5 && semester == 2 -> sorting.subList(0, 4).reversed()
        ntaLevel == 6 && semester == 1 -> sorting.subList(0, 5).reversed()
        ntaLevel == 6 && semester == 2 -> sorting.subList(0, 6).reversed()
        else -> sorting
    }

    var expanded by remember { mutableStateOf(false) }
    var sortItem by remember { mutableStateOf(sortingList.firstOrNull() ?: sorting.first()) }

    // Load data on first composition
    LaunchedEffect(Unit) {
        if (studentList.isEmpty()) {
            loadStudent()
        }
        // Load grades for initial selection
        val (initLevel, initSem) = parseSortItem(sortItem)
        loadGrades(initLevel, initSem)
    }

    // Show loading indicator if loading
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Show message if no grades
    if (gradeList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No grades found.")
        }
        return
    }

    // Calculate summary counts safely
    val totalPassed = gradeList.count { it.caMark >= 30 }
    val totalFailed = gradeList.count { it.caMark in 1.0..29.9 }
    val total = gradeList.size
    val totalPending = total - (totalFailed + totalPassed)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            GradeSummary(
                passed = totalPassed,
                failed = totalFailed,
                pending = totalPending,
                total = total
            )
        }
        item {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = sortItem,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Semester Coursework") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
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
                        unfocusedBorderColor = GradeMateColors.Primary ,
                               unfocusedLabelColor = GradeMateColors.Primary,
                        focusedLabelColor = GradeMateColors.Primary
                    ),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GradeMateColors.Primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    sortingList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                sortItem = item
                                expanded = false
                                val (level, sem) = parseSortItem(item)
                                coroutineScope.launch {
                                    loadGrades(level, sem)
                                }
                            }
                        )
                    }
                }
            }
        }

        items(gradeList) { grade ->
            GradeCard(grade)
        }
    }
}
fun parseSortItem(item: String): Pair<Int, Int> {
    val parts = item.split(" ")
    val level = parts.getOrNull(1)?.toIntOrNull() ?: 4  // Default to 4 if parsing fails
    val sem = parts.getOrNull(3)?.toIntOrNull() ?: 1    // Default to 1 if parsing fails
    return Pair(level, sem)
}




