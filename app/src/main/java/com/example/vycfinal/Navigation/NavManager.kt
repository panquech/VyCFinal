package com.example.vycfinal.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vycfinal.ViewModel.EsteganografiaViewModel
import com.example.vycfinal.ViewModel.LoginViewModel
import com.example.vycfinal.Views.Lockdown.HideMenu
import com.example.vycfinal.Views.Lockdown.MainMenu
import com.example.vycfinal.Views.Lockdown.RevealMenu
import com.example.vycfinal.Views.User.BlankView
import com.example.vycfinal.Views.User.LoginView
import com.example.vycfinal.Views.User.RegisterView


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager(loginVM: LoginViewModel, esteganografiaVM: EsteganografiaViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Blank"){
        composable("Blank"){
            BlankView(navController)
        }
        composable("Login"){
            LoginView(navController, loginVM)
        }
        composable("Register"){
            RegisterView(navController, loginVM)
        }
        composable("MainMenu"){
            MainMenu(navController, esteganografiaVM)
        }
        composable("HideMenu"){
            HideMenu(navController, esteganografiaVM)
        }
        composable("RevealMenu"){
            RevealMenu(navController, esteganografiaVM)
        }
    }
}
