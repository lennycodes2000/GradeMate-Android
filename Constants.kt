package com.example.bcbt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.collections.get

object Constants {
    const val title = "BCBT Grademate"
    val myGPA = mutableDoubleStateOf(0.0)
    val ntaLevel = mutableIntStateOf(studentList[0].ntaLevel.toInt())
    val mySemester = mutableIntStateOf(studentList[0].ntaLevel.toInt())
    val completed = mutableIntStateOf(0)
    val supportNumber = "+255686777093"

}

