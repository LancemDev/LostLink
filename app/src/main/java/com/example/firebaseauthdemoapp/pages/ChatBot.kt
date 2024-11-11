package com.example.firebaseauthdemoapp.pages

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.firebaseauthdemoapp.VoiceToTextParser

@Composable
fun ChatBot(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val voiceToTextParser = remember { VoiceToTextParser(context) }

    var canRecord by remember { mutableStateOf(false) }

    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            canRecord = isGranted
        }
    )

    LaunchedEffect(Unit) {
        recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    val state by voiceToTextParser.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (state.isSpeaking) {
                    voiceToTextParser.stopListening()
                } else {
                    voiceToTextParser.startListening()
                }
            }) {
                AnimatedContent(targetState = state.isSpeaking) { isSpeaking ->
                    if (isSpeaking) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                    } else {
                        Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(targetState = state.isSpeaking) { isSpeaking ->
                if (isSpeaking) {
                    Text(text = "Listening...")
                } else {
                    Text(text = state.spokenText.ifEmpty { "Click on mic to start speaking" })
                }
            }
        }
    }
}
