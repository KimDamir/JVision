import lc.kra.system.keyboard.GlobalKeyboardHook
import lc.kra.system.keyboard.event.GlobalKeyAdapter
import lc.kra.system.keyboard.event.GlobalKeyEvent

object GlobalKeyboardExample {
    private var run = true

    @JvmStatic
    fun main(args: Array<String>) {
        // Might throw a UnsatisfiedLinkError if the native library fails to load or a RuntimeException if hooking fails 
        val keyboardHook = GlobalKeyboardHook(true) // Use false here to switch to hook instead of raw input

        println("Global keyboard hook successfully started, press [escape] key to shutdown. Connected keyboards:")

        for ((key, value) in GlobalKeyboardHook.listKeyboards()) {
            System.out.format("%d: %s\n", key, value)
        }

        keyboardHook.addKeyListener(object : GlobalKeyAdapter() {
            override fun keyPressed(event: GlobalKeyEvent) {
                println(event)
                if (event.virtualKeyCode == GlobalKeyEvent.VK_ESCAPE) {
                    run = false
                }
            }

            override fun keyReleased(event: GlobalKeyEvent) {
                println(event)
            }
        })

        try {
            while (run) {
                Thread.sleep(128)
            }
        } catch (e: InterruptedException) {
            //Do nothing
        } finally {
            keyboardHook.shutdownHook()
        }
    }
}