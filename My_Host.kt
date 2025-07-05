package com.example.bcbt

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes{
    val splash = "Splash"
    val home = "Home"
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
    }
}