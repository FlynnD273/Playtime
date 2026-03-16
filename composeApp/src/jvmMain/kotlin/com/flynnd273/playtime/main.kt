package com.flynnd273.playtime

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

fun main() = application {
    FileKit.init(appId = "Playtime")

    Window(
        onCloseRequest = ::exitApplication,
        title = "Playtime",
    ) {
        val dialogSettings = FileKitDialogSettings(this.window)
        App(dialogSettings)
    }
}
