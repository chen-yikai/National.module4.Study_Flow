package com.example.nationalmodule4

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nationalmodule4.helper.PlayerModal
import com.example.nationalmodule4.room.Record
import com.example.nationalmodule4.room.RecordDataModal
import com.example.nationalmodule4.room.getDataBase
import com.example.nationalmodule4.screen.ManageRecordScreen
import com.example.nationalmodule4.helper.ScreenRecordingService
import com.example.nationalmodule4.helper.timeFormatter
import com.example.nationalmodule4.widget.EditRecordDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NavItem(
    val route: String, val label: String, val icon: Int
)

@Composable
fun Nav(context: Context) {
    val navController = rememberNavController()

    val db = getDataBase(context)
    val recordDao = db.recordDao()

    val recordDataModal = remember { RecordDataModal(recordDao) }
    val playerModal = remember { PlayerModal() }

    var currentTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }
    val scope = rememberCoroutineScope()

    val navItems: List<NavItem> =
        listOf(NavItem(Screen.Home.name, "Record", R.drawable.record_file))
    val currentNavItem = navItems[0].route

    val recordState by ScreenRecordingService.recordState.collectAsState()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val intent = Intent(context, ScreenRecordingService::class.java).apply {
                        putExtra("resultCode", result.resultCode)
                        putExtra("data", data)
                    }
                    context.startForegroundService(intent)
                }
            }
        }
    val mediaProjectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager


    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalRecordDataModal provides recordDataModal,
        LocalPlayerModal provides playerModal
    ) {
        var showEnterFileNameDialog by remember { mutableStateOf(false) }
        if (showEnterFileNameDialog) EditRecordDialog(recordState.currentId) {
            showEnterFileNameDialog = false
        }
        Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (recordState.isRecording) {
                        showEnterFileNameDialog = true
                        scope.launch {
                            recordDataModal.add(
                                Record(
                                    recordState.currentId,
                                    LocalDateTime.now()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                    recordState.path,
                                    currentTime - recordState.startRecording,
                                    System.currentTimeMillis()
                                )
                            )
                            context.stopService(Intent(context, ScreenRecordingService::class.java))
                        }
                    } else {
                        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
                        launcher.launch(captureIntent)
                    }
                },
                containerColor = if (recordState.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(R.drawable.record), contentDescription = null)
                    Spacer(Modifier.width(5.dp))
                    Text(if (recordState.isRecording) timeFormatter(currentTime - recordState.startRecording) else "Start Recording")
                }
            }
        }) { innerPadding ->
            Row(modifier = Modifier) {
                NavigationRail {
                    navItems.forEach {
                        NavigationRailItem(selected = currentNavItem == it.route,
                            onClick = { },
                            icon = {
                                Icon(
                                    painter = painterResource(it.icon), contentDescription = null
                                )
                            },
                            label = { Text(it.label) })
                    }
                }
                NavHost(navController, startDestination = Screen.Home.name) {
                    composable(Screen.Home.name) { ManageRecordScreen(innerPadding) }
                }
            }
        }
    }
}

enum class Screen {
    Home
}
