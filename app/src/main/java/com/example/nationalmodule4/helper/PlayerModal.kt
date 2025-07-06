package com.example.nationalmodule4.helper

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


data class PlayerState(
    var isPlaying: Boolean = false,
    var ready: Boolean = false,
    var title: String = "",
    var currentPosition: Float = 0f,
    var duration: Float = 0f,
    var wasPlaying: Boolean = false,
    var aspectRatio: Float = 16f / 9f
)

class PlayerModal : ViewModel() {
    private var _player: ExoPlayer? = null
    val player: ExoPlayer get() = _player!!
    val playerOrNull: ExoPlayer? get() = _player

    private var _recordId = MutableStateFlow("")
    val recordId: StateFlow<String> = _recordId

    fun updateRecordId(id: String) = _recordId.update { id }

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState
    private var job: Job? = null


    fun setMediaItem(path: String, context: Context) {
        try {
            _playerState.update {
                it.copy(
                    isPlaying = false,
                    ready = false,
                    title = "",
                    duration = 0f,
                    currentPosition = 0f
                )
            }
            Log.i("trigger modal", "player is init")
            _player = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.Builder().setUri(Uri.fromFile(File(path))).build())
                prepare()
                addListener(object : Listener {
                    @OptIn(UnstableApi::class)
                    override fun onEvents(player: Player, events: Player.Events) {
                        super.onEvents(player, events)

                        _playerState.update {
                            it.copy(
                                isPlaying = player.isPlaying,
                                duration = player.duration.toFloat(),
                                title = player.mediaMetadata.title.toString(),
                                wasPlaying = player.isPlaying,
                                aspectRatio = _player?.videoFormat?.let { it.width.toFloat() / it.height.toFloat() }
                                    ?: (16f / 10f)
                            )
                        }

                        job?.cancel()
                        job = viewModelScope.launch {
                            while (true) {
                                _playerState.update {
                                    it.copy(currentPosition = player.currentPosition.toFloat())
                                }
                                delay(500L)
                            }
                        }
                    }
                })
                play()
            }
            _playerState.update { it.copy(ready = true) }
        } catch (e: Exception) {
            Log.i("trigger modal", "player is not init")
        }
    }

    fun toggle() {
        _player?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun forward() = _player?.seekForward()
    fun backward() = _player?.seekBack()

    fun seekTo(time: Long) = _player?.seekTo(time)

    fun dispose() {
        job?.cancel()
        _player?.stop()
        _player?.release()
        _player = null
    }

    override fun onCleared() {
        super.onCleared()
        dispose()
    }
}
