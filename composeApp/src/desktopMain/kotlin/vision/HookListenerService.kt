package vision

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import api.AuthInterceptor
import kotlinx.coroutines.*
import lc.kra.system.keyboard.GlobalKeyboardHook


import kotlinx.coroutines.internal.synchronized
import lc.kra.system.keyboard.event.GlobalKeyAdapter
import lc.kra.system.keyboard.event.GlobalKeyEvent
import lc.kra.system.keyboard.event.GlobalKeyListener
import okhttp3.OkHttpClient
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Robot
import kotlin.concurrent.Volatile

class HookListenerService private constructor(){
    companion object {

        private var hook = GlobalKeyboardHook(true)
        private var pressed = false
        private lateinit var firstPosition: Point

        fun startService() {
            println("Turning on service")
            hook = GlobalKeyboardHook(true)
            hook.addKeyListener(keyListener)
        }

        fun endService() {
            println("Shutting down service")
            hook.shutdownHook()
        }

        private val keyListener = object:GlobalKeyAdapter(){
            override fun keyPressed(event: GlobalKeyEvent) {
                if (!pressed && event.virtualKeyCode == GlobalKeyEvent.VK_SHIFT) {
                    pressed = true
                    firstPosition = MouseInfo.getPointerInfo().location
                }

            }

            override fun keyReleased(event: GlobalKeyEvent) {
                if (event.virtualKeyCode == GlobalKeyEvent.VK_SHIFT) {
                    takeCustomScreenshot(firstPosition)
                    pressed = false
                }
            }
        }
    }

}