package com.flynnd273.playtime.UiComponents

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.flynnd273.playtime.*
import com.flynnd273.playtime.Utils.toTimestamp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NowPlayingBanner(viewModel: SharedViewModel, nc: NavHostController) {
    val nowPlaying = viewModel.nowPlaying
    val playerState by viewModel.nowPlaying.playerState.collectAsState()
    val currPlaying by viewModel.nowPlaying.currPlaying.collectAsState()
    val shuffleMode by nowPlaying.shuffleMode.collectAsState()
    val repeatMode by nowPlaying.repeatMode.collectAsState()
    if (currPlaying == null) {
        return
    }

    Card(modifier = Modifier.height(Sizes.Image).padding(Padding.SmallPadding).fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.weight(1f).padding(Padding.SmallPadding)) {
                AsyncImage(
                    model = currPlaying!!.artPath,
                    contentDescription = currPlaying!!.name,
                    modifier = Modifier.aspectRatio(1f).height(Sizes.SmallImage).clip(CardDefaults.shape)
                )
                Spacer(modifier = Modifier.width(Padding.MediumPadding))
                Column {
                    Text(currPlaying!!.name)
                    Text(currPlaying!!.artistName, fontSize = FontSizes.Small)
                }
                Spacer(modifier = Modifier.padding(Padding.SmallPadding))
                Box(modifier = Modifier.weight(1f, true), contentAlignment = Alignment.Center) {
                    Row {
                        val extraSmallModifier = Modifier.size(IconButtonDefaults.extraSmallIconSize)
                        val smallModifier = Modifier.size(IconButtonDefaults.smallIconSize)
                        val extraLargeModifier = Modifier.size(IconButtonDefaults.extraLargeIconSize)
                        IconButton(onClick = { nowPlaying.nextShuffleMode() }) {
                            when (shuffleMode) {
                                ShuffleMode.None ->
                                    Icon(
                                        Icons.Default.Shuffle,
                                        "No shuffle",
                                        modifier = extraSmallModifier
                                    )

                                ShuffleMode.Normal ->
                                    Icon(
                                        Icons.Default.ShuffleOn,
                                        "Shuffle on",
                                        modifier = extraSmallModifier
                                    )
                            }
                        }
                        IconButton(onClick = { nowPlaying.skipPrev() }) {
                            Icon(
                                Icons.Default.SkipPrevious, "Skip previous",
                                modifier = smallModifier
                            )
                        }
                        IconButton(
                            onClick = { nowPlaying.togglePlaying() },
                        ) {
                            if (playerState.isPlaying) {
                                Icon(
                                    Icons.Default.Pause,
                                    "Pause",
                                    modifier = extraLargeModifier
                                )
                            } else {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    "Play",
                                    modifier = extraLargeModifier
                                )
                            }
                        }
                        IconButton(onClick = { nowPlaying.skipNext() }) {
                            Icon(
                                Icons.Default.SkipNext, "Skip next",
                                modifier = smallModifier
                            )
                        }
                        IconButton(onClick = { nowPlaying.nextRepeatMode() }) {
                            when (repeatMode) {
                                RepeatMode.None ->
                                    Icon(
                                        Icons.Default.Repeat,
                                        "No repeat",
                                        modifier = extraSmallModifier
                                    )

                                RepeatMode.All ->
                                    Icon(
                                        Icons.Default.RepeatOn,
                                        "Repeat all",
                                        modifier = extraSmallModifier
                                    )

                                RepeatMode.One ->
                                    Icon(
                                        Icons.Default.RepeatOne,
                                        "Repeat one",
                                        modifier = extraSmallModifier
                                    )
                            }
                        }
                    }
                }
            }
            Column(modifier = Modifier.weight(1f).padding(Padding.SmallPadding)) {
                Slider(
                    value = playerState.currentPositionMs / playerState.durationMs.toFloat().coerceAtLeast(1f),
                    onValueChange = { nowPlaying.seekTo((playerState.durationMs * it).toLong()) },
                    modifier = Modifier.weight(1.5f)
                )
                Row {
                    Text(playerState.currentPositionMs.toTimestamp(), fontSize = FontSizes.Small)
                    Spacer(modifier = Modifier.weight(1f, true))
                    Text(playerState.durationMs.toTimestamp(), fontSize = FontSizes.Small)
                }
            }
        }
    }
}