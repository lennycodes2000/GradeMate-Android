package com.example.bcbt

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

    object GradeMateColors {
        val Primary = Color(0xFF1976D2)    // Blue 500
        val Secondary = Color(0xFF66BB6A)  // Green 400
        val Background = Color(0xFFF5F5F5) // Gray 100
        val Surface = Color(0xFFFFFFFF)    // White
        val TextPrimary = Color(0xFF212121) // Gray 900
        val Accent = Color(0xFFFFCA28)     // Amber 400
        val Error = Color(0xFFE53935)      // Red 600
        val backGradient = Brush.verticalGradient(listOf(Color(0xFF81d4fa), Color(0xFFe1f5fe)))
        val back1 = Color(0xFF81d4fa)
        val Warning = Color(0xFFF9A825)
        val gpaGradient = Brush.verticalGradient(
            listOf(
                Color(0xFFE3F2FD),  // Light Blue 50 (soft background)
                Color(0xFFBBDEFB),  // Blue 100 (gentle transition)
                Primary,            // Blue 500 (brand color)
                Secondary,          // Green 400 (success/academic growth)
                Accent             // Amber 400 (highlight/achievement)
            )
        )
    }
