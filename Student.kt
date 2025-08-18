package com.example.bcbt

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

data class Student(
    var studentName: String = "",
    var regNo: String = "",
    var gender: String = "",
    var ntaLevel: String = "",
    var semester: String = "",
    var program: String = ""
)
val studentList = mutableStateListOf<Student>()

fun loadStudent() {
    val db = Firebase.firestore
    val regNo = Auth.id.value

    if (regNo.isBlank()) {
        println("Auth ID is blank — cannot load student")
        return
    }

    db.collection("students")
        .whereEqualTo("regNo", regNo)
        .get()
        .addOnSuccessListener { result ->
            studentList.clear()
            for (document in result) {
                val student = document.toObject(Student::class.java)
                studentList.add(student)
                Constants.ntaLevel.intValue = studentList[0].ntaLevel.trim().toInt()
                Constants.mySemester.intValue = studentList[0].semester.trim().toInt()
            }

            if (studentList.isNotEmpty()) {
                println("Student loaded: ${studentList.first().studentName}")
                loadModules()
                loadGrades(   Constants.ntaLevel.intValue , Constants.mySemester.intValue )
                loadPublish()
            } else {
                println("No student found for regNo: $regNo")
            }
        }
        .addOnFailureListener { e ->
            println("Error loading student: $e")
        }
}


