package com.example.bcbt

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resumeWithException

class Grade(
    var studentId: String = "",
    var moduleCode: String = "",
    var caMark: Double= 0.0,
    var attendanceMark: String = "",
    var assignmentMark: String = "",
    var quizMark: String = "",
    var testOneMark: String = "",
    var testTwoMark: String = "",
    var examMark: String = "",
    var total: Double = 0.0
)

var gradeList = mutableStateListOf<Grade>()
var isLoading by mutableStateOf(true)

fun loadGrades(level: Int, semester: Int) {
    isLoading = true
    Firebase.firestore.collection("grades")
        .whereEqualTo("studentId", Auth.id.value)
        .whereEqualTo("ntaLevel", level.toString())
        .whereEqualTo("semester", semester.toString())
        .get()
        .addOnSuccessListener { result ->
            isLoading = false
            gradeList.clear()
            for (document in result) {
                val grade = document.toObject(Grade::class.java)
                gradeList.add(grade)
            }
            Log.d("Grades", "Loaded ${gradeList.size} grades for Level $level Semester $semester.")
        }
        .addOnFailureListener { exception ->
            isLoading = false
            Log.e("Grades", "Error loading grades: $exception")
        }
}
//suspend function
suspend fun loadGradesSuspend(level: Int, semester: Int): List<Grade> {
    val db = FirebaseFirestore.getInstance()
    return try {
        val result = db.collection("grades")
            .whereEqualTo("studentId", Auth.id.value)
            .whereEqualTo("ntaLevel", level.toString())
            .whereEqualTo("semester", semester.toString())
            .get()
            .await() // <-- suspend until Firestore returns

        val grades = result.documents.mapNotNull { it.toObject(Grade::class.java) }
        Log.d("Grades", "Loaded ${grades.size} grades for Level $level Semester $semester.")
        grades
    } catch (e: Exception) {
        Log.e("Grades", "Error loading grades: $e")
        emptyList()
    }
}

fun getPoint(ntaLevel: Int, total: Double, ueMark: Double): Double {
    return if (ntaLevel != 6) {
        when {
            ueMark < 20.0 -> 0.0
            total >= 80 -> 4.0
            total >= 65 -> 3.0
            total >= 50 -> 2.0
            total >= 40 -> 1.0
            else -> 0.0
        }
    } else {
        when {
            ueMark < 20.0 -> 0.0
            total >= 75 -> 5.0
            total >= 65 -> 4.0
            total >= 55 -> 3.0
            total >= 45 -> 2.0
            total >= 35 -> 1.0
            else -> 0.0
        }
    }
}


//functioin to get the award name
fun awardName(): String {
    val ntaLevel = studentList[0].ntaLevel.trim().toInt()
    return if (ntaLevel != 6) {
        when {
            Constants.myGPA.value >= 3.5 -> "First Class"
            Constants.myGPA.value >= 3.0 -> "Second Class"
            Constants.myGPA.value >= 2.0 -> "Pass"
            else -> "Unassigned"
        }
    } else {
        when {
            Constants.myGPA.value >= 4.4 -> "First Class"
            Constants.myGPA.value >= 3.5 -> "Upper Second Class"
            Constants.myGPA.value >= 2.7 -> "Second Class"
            Constants.myGPA.value >= 2.0 -> "Pass"
            else -> "Unassigned"
        }
    }
}



