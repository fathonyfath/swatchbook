package dev.fathony.swatchbook

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Swatchbook") {
        App(FormFactor.Desktop)
    }
}
