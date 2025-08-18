package com.example.bcbt

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

data class BottomNav (
    val label:String,
    val icon: Int,
)
val navItems = listOf(
    BottomNav("Home", R.drawable.home),
    BottomNav("Coursework", R.drawable.course),
    BottomNav("Results",R.drawable.results),
    BottomNav("Settings",R.drawable.setting_setting_svgrepo_com)
)