package com.example.nationalmodule4

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.example.nationalmodule4.service.ScreenRecordingService
import com.example.nationalmodule4.ui.theme.Nationalmodule4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Nationalmodule4Theme {
                val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
                val audioPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

                LaunchedEffect(Unit) {
                    if(!audioPermission) permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }

                Nav(this)
            }
        }
    }
}