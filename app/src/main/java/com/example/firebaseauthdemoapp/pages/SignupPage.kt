package com.example.firebaseauthdemoapp.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebaseauthdemoapp.AuthState
import com.example.firebaseauthdemoapp.AuthViewModel
import com.example.firebaseauthdemoapp.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("homepage")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.Background), // Using AppTheme background color
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Signup Page",
            fontSize = 32.sp,
            color = AppTheme.Primary, // Using AppTheme primary color for title
            style = TextStyle(fontSize = 32.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email", color = AppTheme.Primary) }, // Using primary color for label
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp), // Added padding to avoid full width
            colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.Primary,
                unfocusedBorderColor = AppTheme.TextGray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password", color = AppTheme.Primary) }, // Using primary color for label
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp), // Added padding to avoid full width
            colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.Primary,
                unfocusedBorderColor = AppTheme.TextGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.signup(email, password) },
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.Primary, // Using AppTheme primary color for button
                contentColor = Color.White
            ),
            enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Create Account")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("login") },
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "Already have an account? Login here",
                color = AppTheme.Primary // Using AppTheme primary color for text
            )
        }
    }
}

