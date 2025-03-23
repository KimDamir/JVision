package vision

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import lc.kra.system.keyboard.GlobalKeyboardHook
import lc.kra.system.keyboard.event.GlobalKeyAdapter
import lc.kra.system.keyboard.event.GlobalKeyEvent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.MouseInfo
import java.awt.Rectangle
import java.awt.Robot
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO
import com.google.gson.Gson
import jvision.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

const val IMAGE_WIDTH = 128
const val IMAGE_HEIGHT = 32

@OptIn(ExperimentalResourceApi::class)
actual fun takeScrenshot(): ImageBitmap {
    val client = OkHttpClient()
    val robot = Robot()
    val cursorPosition = MouseInfo.getPointerInfo().location
    val rectangle = Rectangle(cursorPosition.x - IMAGE_WIDTH/2, cursorPosition.y - IMAGE_HEIGHT/2,
        IMAGE_WIDTH, IMAGE_HEIGHT)
    val image = robot.createScreenCapture(rectangle)
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(image, "bmp", outputStream)
    val byteArray = outputStream.toByteArray()
    val JSON = "application/json; charset=utf-8".toMediaType()
    val body: RequestBody = byteArray.toRequestBody(JSON)
    val request = Request.Builder()
        .url("http://127.0.0.1:9000/ocr")
        .post(body)
        .build()
    println(request.headers)
    try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Запрос к серверу не был успешен:" +
                        " ${response.code} ${response.message}")
            }
            val result = response.body!!.string()
            println(result)
        }
    } catch (e: IOException) {
        println("Ошибка подключения: $e")
    }
    val bitmap = ByteArrayInputStream(byteArray).readAllBytes().decodeToImageBitmap()
    return  bitmap
}
@Composable
actual fun listenForCall(action: ()->Unit) {
    val hook = GlobalKeyboardHook(true)

    hook.addKeyListener(object: GlobalKeyAdapter() {
        override fun keyPressed(event: GlobalKeyEvent) {
            if (event.virtualKeyCode == GlobalKeyEvent.VK_SHIFT) {
                action()
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
            argb += ((pixelData[valueIndex].toInt() and 0xff) shl 24) // alpha value
            argb += (pixelData[valueIndex + 1].toInt() and 0xff) // blue value
            argb += ((pixelData[valueIndex + 2].toInt() and 0xff) shl 8) // green value
            argb += ((pixelData[valueIndex + 3].toInt() and 0xff) shl 16) // red value
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
            argb += (pixelData[valueIndex].toInt() and 0xff) // blue value
            argb += ((pixelData[valueIndex + 1].toInt() and 0xff) shl 8) // green value
            argb += ((pixelData[valueIndex + 2].toInt() and 0xff) shl 16) // red value
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


class Response() {
    val prediction: String = ""
}

@Composable
actual fun autoButton(modifier: Modifier) {
}