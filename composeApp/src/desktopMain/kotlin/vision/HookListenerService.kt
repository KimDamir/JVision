package vision


import const.viewmodel.JVisionViewModel
import lc.kra.system.keyboard.GlobalKeyboardHook
import lc.kra.system.keyboard.event.GlobalKeyAdapter
import lc.kra.system.keyboard.event.GlobalKeyEvent
import java.awt.MouseInfo
import java.awt.Point

class HookListenerService private constructor(){
    companion object {

        private var hook = GlobalKeyboardHook(true)
        private var pressed = false
        private lateinit var firstPosition: Point
        private lateinit var vm: JVisionViewModel

        fun startService(vm:JVisionViewModel = JVisionViewModel()) {
            this.vm = vm
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
                    takeCustomScreenshot(firstPosition, vm)
                    pressed = false
                }
            }
        }
    }

}