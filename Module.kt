package com.example.bcbt

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

data class Module(
    var name: String = "",
    var courses: List<String> = emptyList(),
    var credit: String = "",
    var level: String = "",
    var semester: String = "",
    var code: String = ""
)

val moduleList = mutableStateListOf<Module>()
// the function to loadModules
fun loadModules() {
    val db = Firebase.firestore

    // ✅ Ensure student data is loaded before accessing studentList[0]
    if (studentList.isEmpty()) {
        println("Student list is empty. Cannot load modules.")
        return
    }

    db.collection("modules")
        .whereEqualTo("level", Constants.ntaLevel.intValue.toString())
        .whereEqualTo("semester", Constants.mySemester.intValue.toString())
        .get()
        .addOnSuccessListener { result ->
            moduleList.clear() // ✅ Clear before loop
            for (document in result) {
                val module = document.toObject(Module::class.java)
                moduleList.add(module)
            }
            Auth.noModules.value = moduleList.size.toString()
            Auth.totalCredits.value = moduleList.sumOf { it.credit.toDouble() }
            Log.d("Azir",moduleList.toString())
        }
        .addOnFailureListener { exception ->
            println("Error loading modules: $exception")
        }
}

@Composable
fun ModuleList(modules: List<Module>, studentProgram: String) {
    val filteredModules = modules.filter { it.courses.contains(studentProgram) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filteredModules) { module ->
            ModuleCard(module)
        }
    }
}

@Composable
fun ModuleCard(module: Module) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = module.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GradeMateColors.Primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Code: ${module.code}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Text(
                text = "Credits: ${module.credit}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
