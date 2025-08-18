package com.example.bcbt

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

data class PublishSetting(
    var coursework: String = "",
    var courseworkNtaLevel:String = "",
    var courseworkSemester: String = "",
    var results:String = "",
    var resultsNtaLevel:String = "",
    var resultsSemester:String = "",
)
val publish = mutableStateListOf<PublishSetting>()
fun loadPublish() {
    Firebase.firestore.collection("publish")
        .document("settings")
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val data = document.data
                if (data != null) {
                    val publishSetting = PublishSetting(
                        coursework = data["coursework"] as? String ?: "",
                        courseworkNtaLevel = data["courseworkNtaLevel"] as? String ?: "",
                        courseworkSemester = data["courseworkSemester"] as? String ?: "",
                        results = data["results"] as? String ?: "",
                        resultsNtaLevel = data["resultsNtaLevel"] as? String ?: "",
                        resultsSemester = data["resultsSemester"] as? String ?: "",
                    )
                    publish.clear()
                    publish.add(publishSetting)
                    Log.d("Noreb",publish.toString())
                }
            }
        }
        .addOnFailureListener {
            //handle failure here
            print(it.message)
        }
}
