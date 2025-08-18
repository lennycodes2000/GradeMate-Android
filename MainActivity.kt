package com.example.bcbt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.WorkManager
import com.example.bcbt.ui.theme.BCBTTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase once
       FirebaseApp.initializeApp(this)


        // Set Compose content
        setContent {
            BCBTTheme {
                My_Host()
            }
        }
    }
}



