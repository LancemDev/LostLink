package com.example.firebaseauthdemoapp.pages

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebaseauthdemoapp.AuthState
import com.example.firebaseauthdemoapp.AuthViewModel
import com.example.firebaseauthdemoapp.R
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
        // Add the drawable image at the top of the signup form
        Image(
            painter = painterResource(id = R.drawable.lostlink),  // Correct usage of painterResource
            contentDescription = "Signup Image",
            modifier = Modifier
                .size(120.dp) // Adjust size as needed
                .padding(bottom = 32.dp) // Add padding if required
        )

        Text(
            text = "Create Account",
            fontSize = 32.sp,
            color = AppTheme.Primary, // Using AppTheme primary color for title
            style = TextStyle(fontSize = 32.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") }, // No need to set color here
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.Primary,
                unfocusedBorderColor = AppTheme.Primary,
                focusedLabelColor = AppTheme.Primary, // Set focused label color
                unfocusedLabelColor = AppTheme.TextGray // Set unfocused label color
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") }, // Using primary color for label
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp), // Added padding to avoid full width
            colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppTheme.Primary,
                unfocusedBorderColor = AppTheme.Primary,
                focusedLabelColor = AppTheme.Primary, // Set focused label color
                unfocusedLabelColor = AppTheme.TextGray // Set unfocused label color
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

