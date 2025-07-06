package com.example.nationalmodule4.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nationalmodule4.LocalRecordViewNavController

@Composable
fun ManageRecordScreen(innerPadding: PaddingValues) {
    val recordViewNavController = rememberNavController()
    val localConfig = LocalConfiguration.current

    CompositionLocalProvider(LocalRecordViewNavController provides recordViewNavController) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (localConfig.screenWidthDp.dp > 800.dp) {
                Row {
                    Box(modifier = Modifier.widthIn(max = localConfig.screenWidthDp.dp * 0.3f)) {
                        AllRecords(innerPadding)
                    }
                    VerticalDivider()
                    RecordPreviewScreen()
                }
            } else {
                NavHost(
                    recordViewNavController,
                    startDestination = ManageRecordScreenRoute.RecordList.name,
                ) {
                    composable(
                        ManageRecordScreenRoute.Preview.name,
                        enterTransition = { slideInHorizontally { it } },
                        exitTransition = { slideOutHorizontally { it } }
                    ) {
                        RecordPreviewScreen()
                    }
                    composable(ManageRecordScreenRoute.RecordList.name,
                        enterTransition = { slideInHorizontally { it } },
                        exitTransition = { slideOutHorizontally { it } }
                    ) {
                        Box(modifier = Modifier) {
                            AllRecords(innerPadding)
                        }
                    }
                }
            }
        }
    }
}

enum class ManageRecordScreenRoute {
    Preview, RecordList
}