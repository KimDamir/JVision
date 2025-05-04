package vision

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import api.sendScreenshot
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lc.kra.system.keyboard.GlobalKeyboardHook
import lc.kra.system.keyboard.event.GlobalKeyAdapter
import lc.kra.system.keyboard.event.GlobalKeyEvent
import org.example.project.changeWordList
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ui.components.Button
import vision.HookListenerService.Companion.endService
import vision.HookListenerService.Companion.startService
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Rectangle
import java.awt.Robot
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt

const val IMAGE_WIDTH = 128
const val IMAGE_HEIGHT = 32

@OptIn(ExperimentalResourceApi::class)
actual fun takeScreenshot() {
    val robot = Robot()
    val cursorPosition = MouseInfo.getPointerInfo().location
    val rectangle = Rectangle(cursorPosition.x - IMAGE_WIDTH/2, cursorPosition.y - IMAGE_HEIGHT/2,
        IMAGE_WIDTH, IMAGE_HEIGHT)
    val image = robot.createScreenCapture(rectangle)
    println("Changing word list")
    runBlocking{
        launch {
            changeWordList(sendScreenshot(image))
        }
    }
}
fun takeCustomScreenshot(firstPosition: Point) {
    val robot = Robot()
    val lastPosition = MouseInfo.getPointerInfo().location
    val width = lastPosition.x - firstPosition.x
    val height = lastPosition.y - firstPosition.y
//    if (width < IMAGE_WIDTH && height < IMAGE_HEIGHT) {
//        takeScreenshot()
//        return
//    }
    val rectangle = Rectangle(firstPosition.x, firstPosition.y,
        width, height)
    val image = robot.createScreenCapture(rectangle)
    println("Changing word list")
    runBlocking{
        launch {
            changeWordList(sendScreenshot(image))
        }
    }
}
@Composable
actual fun listenForCall(action: ()->Unit) {
    val hook = GlobalKeyboardHook(true)
    hook.addKeyListener(object: GlobalKeyAdapter() {
        override fun keyPressed(event: GlobalKeyEvent) {
            if (event.virtualKeyCode == GlobalKeyEvent.VK_SHIFT) {
                action()
            }
            if (event.virtualKeyCode == GlobalKeyEvent.VK_ESCAPE) {
                hook.shutdownHook()
            }
        }

        override fun keyReleased(event: GlobalKeyEvent) {

        }
    })
}

fun get2DPixelArrayFast(image: BufferedImage): Array<IntArray> {
    val pixelData = (image.raster.dataBuffer as DataBufferInt).data
    val width = image.width
    val height = image.height
    val hasAlphaChannel = image.alphaRaster != null

    val result = Array(height) { IntArray(width) }
    if (hasAlphaChannel) {
        val numberOfValues = 4
        var valueIndex = 0
        var row = 0
        var col = 0
        while (valueIndex + numberOfValues - 1 < pixelData.size) {
            var argb = 0
            argb += ((pixelData[valueIndex] and 0xff) shl 24) // alpha value
            argb += (pixelData[valueIndex + 1] and 0xff) // blue value
            argb += ((pixelData[valueIndex + 2] and 0xff) shl 8) // green value
            argb += ((pixelData[valueIndex + 3] and 0xff) shl 16) // red value
            result[row][col] = argb

            col++
            if (col == width) {
                col = 0
                row++
            }
            valueIndex += numberOfValues
        }
    } else {
        val numberOfValues = 3
        var valueIndex = 0
        var row = 0
        var col = 0
        while (valueIndex + numberOfValues - 1 < pixelData.size) {
            var argb = 0
            argb += -16777216 // 255 alpha value (fully opaque)
            argb += (pixelData[valueIndex] and 0xff) // blue value
            argb += ((pixelData[valueIndex + 1] and 0xff) shl 8) // green value
            argb += ((pixelData[valueIndex + 2] and 0xff) shl 16) // red value
            result[row][col] = argb

            col++
            if (col == width) {
                col = 0
                row++
            }
            valueIndex += numberOfValues
        }
    }

    return result
}


@Composable
actual fun autoButton(modifier: Modifier) {
    var open by remember { mutableStateOf(false) }

    Button("Auto", onClick = {
        if (open) {
            endService()
        } else {
            startService()
        }
        open = !open
    },
        modifier = modifier)
}