package com.ali.exoplayer

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun Media3PlayerView(videoUrl: String) {
    val context = LocalContext.current
    var player by remember { mutableStateOf<ExoPlayer?>(null) }

    DisposableEffect(videoUrl) {
        val exoPlayer = ExoPlayer.Builder(context).build().also {
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
            it.setMediaItem(mediaItem)
            it.prepare()
            it.playWhenReady = true
            it.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    handleError(error)
                }
            })
        }
        player = exoPlayer

        onDispose {
            exoPlayer.release()
            player = null
        }
    }

    Column {
        Media3AndroidView(player)
        PlayerControls(player)
    }

}

@Composable
fun Media3AndroidView(player: ExoPlayer?) {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            PlayerView(context).apply {
                this.player = player
            }
        },
        update = { playerView ->
            playerView.player = player
        }
    )
}

private fun handleError(error: PlaybackException) {
    when (error.errorCode) {
        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> {
            // Handle network connection error
            println("Network connection error")
        }
        PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> {
            // Handle file not found error
            println("File not found")
        }
        PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> {
            // Handle decoder initialization error
            println("Decoder initialization error")
        }
        else -> {
            // Handle other types of errors
            println("Other error: ${error.message}")
        }
    }
}