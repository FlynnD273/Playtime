package com.flynnd273.playtime.Utils

fun Long.toTimestamp(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / (60 * 60)
    val minutes = (totalSeconds % (60 * 60)) / 60
    val seconds = totalSeconds % 60
    if (hours > 0) {
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }
    return "%02d:%02d".format(minutes, seconds)
}