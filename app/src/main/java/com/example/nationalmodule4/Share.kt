package com.example.nationalmodule4

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.nationalmodule4.helper.PlayerModal
import com.example.nationalmodule4.helper.ShareModal
import com.example.nationalmodule4.room.RecordDataModal

val LocalNavController = compositionLocalOf<NavHostController> { error("LocalNavController") }
val LocalRecordDataModal = compositionLocalOf<RecordDataModal> { error("LocalRecordModal") }
val LocalRecordViewNavController =
    compositionLocalOf<NavHostController> { error("LocalRecordViewNavController") }
val LocalShareModal = compositionLocalOf<ShareModal> { error("LocalShareModal") }
val LocalPlayerModal = compositionLocalOf<PlayerModal> { error("LocalPlayerModal") }

val maxWidth: Dp = 800.dp