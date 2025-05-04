package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import api.checkToken

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "JVision",
    ) {
        App(true)
    }
}