package com.example.firebaseauthdemoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.firebaseauthdemoapp.pages.HomePage
import com.example.firebaseauthdemoapp.pages.NewHomePage
import com.example.firebaseauthdemoapp.pages.NotificationsPage
import com.example.firebaseauthdemoapp.pages.ProfilePage
import com.example.firebaseauthdemoapp.pages.SettingsPage
import com.example.firebaseauthdemoapp.ui.theme.FirebaseAuthDemoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel : AuthViewModel by viewModels()
        setContent {
            FirebaseAuthDemoAppTheme {
                MyNavigationPage(modifier = Modifier, authViewModel = authViewModel)
            }
        }
    }
}


//@Composable
//fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int){
//    val navController = rememberNavController()
//    when(selectedIndex){
//        0-> MyNavigationPage(modifier = Modifier.fillMaxSize(), authViewModel = AuthViewModel())
//        1-> SettingsPage()
//        2-> NotificationsPage()
//        3-> ProfilePage()
//    }
//}

