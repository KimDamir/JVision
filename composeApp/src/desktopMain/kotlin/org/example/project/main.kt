package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import api.checkToken
import const.viewmodel.JVisionViewModel

fun main() = application {
    val vm = JVisionViewModel()
    Window(
        onCloseRequest = ::exitApplication,
        title = "JVision",
    ) {
        App(checkToken(), vm)
    }
}