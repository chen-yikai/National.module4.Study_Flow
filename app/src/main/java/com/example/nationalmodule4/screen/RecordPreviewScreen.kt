package com.example.nationalmodule4.screen

import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.transition.Slide
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.OptIn
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.LocalConfiguration
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import com.example.nationalmodule4.LocalPlayerModal
import com.example.nationalmodule4.LocalRecordDataModal
import com.example.nationalmodule4.LocalShareModal
import com.example.nationalmodule4.R
import com.example.nationalmodule4.helper.PlayerModal
import java.io.File

@OptIn(UnstableApi::class)
@Composable
fun RecordPreviewScreen() {
    val context = LocalContext.current
    val recordData = LocalRecordDataModal.current
    val playerModal = LocalPlayerModal.current
    val playerState by playerModal.playerState.collectAsState()

    val id by playerModal.recordId.collectAsState()
    val record by recordData.getById(id).collectAsState(null)

    var oldId by rememberSaveable { mutableStateOf("") }

    fun setUpPlayer() {
        record?.let { data ->
            oldId = data.path
            try {
                if (data.path != "") {
                    playerModal.setMediaItem(data.path, context)
                } else {
                    Log.e("RecordPreview", "No path found")
                }
            } catch (e: Exception) {
                Log.e("RecordPreview", "Error loading media: ${e.message}", e)
            }
        }
    }
    LaunchedEffect(record) {
        if (!playerState.ready) {
            setUpPlayer()
        } else {
            if (oldId != id) {
                playerModal.dispose()
                setUpPlayer()
            }
        }
    }


    if (playerState.ready && playerModal.playerOrNull != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
                Box(modifier = Modifier.aspectRatio(playerState.aspectRatio)) {
                    Card(border = CardDefaults.outlinedCardBorder()) {
                        PlayerSurface(playerModal.player)
                    }
                }
            Spacer(Modifier.height(20.dp))
            Slider(playerState.currentPosition, onValueChange = {
                playerModal.seekTo(it.toLong())
            }, valueRange = 0f..if (playerState.duration > 0f) playerState.duration else 0f)
            Spacer(Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallFloatingActionButton(onClick = {
                    playerModal.backward()
                }) {
                    Icon(
                        painter = painterResource(R.drawable.fast_rewind), contentDescription = null
                    )
                }
                FloatingActionButton(onClick = {
                    playerModal.toggle()
                }) {
                    Icon(
                        painter = painterResource(if (playerState.isPlaying) R.drawable.pause else R.drawable.play),
                        contentDescription = null
                    )
                }
                SmallFloatingActionButton(onClick = { playerModal.forward() }) {
                    Icon(
                        painter = painterResource(R.drawable.fast_forward),
                        contentDescription = null
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(R.drawable.record_file),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(70.dp)
            )
            Spacer(Modifier.height(15.dp))
            Text("No selected record", color = Color.Gray)
        }
    }
}
