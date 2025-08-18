package com.example.bcbt

import Login
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes{
    const val splash = "Splash"
    const val home = "Home"
    const val gradeSplash = "GradeSplash"
    const val gradeHome = "GradeHome"
    const val login = "Login"
}
@Composable
fun My_Host(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.splash ){
        composable(Routes.splash){
            Splash(navController)
        }
        composable(Routes.home){
            Home(navController)
        }
        composable(Routes.gradeSplash){
            GradeSplash(navController)
        }
        composable(Routes.gradeHome){
            GradeHome(navController)
        }
        composable(Routes.login){
            Login(navController)
        }
    }
}