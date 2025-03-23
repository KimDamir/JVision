package vision

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.Image
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.KeyEventDispatcher.Component
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import androidx.test.core.app.takeScreenshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dictionary.Word
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.project.MainActivity
import ui.components.Button
import java.io.ByteArrayOutputStream
import java.io.IOException


const val IMAGE_WIDTH = 128
const val IMAGE_HEIGHT = 32

@RequiresApi(Build.VERSION_CODES.O)
actual fun takeScrenshot(): ImageBitmap {
    val bitmap = takeScreenshot()
    sendScreenshot(bitmap)
    return  bitmap.asImageBitmap()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
actual fun listenForCall(action: () -> Unit) {
}

fun viewToBitmap(view: View, x:Float=0f, y:Float=0f): Bitmap {
    var screenshot = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
    if (x != 0f || y != 0f) {
        val intX = x.toInt()
        val intY = y.toInt()
        screenshot = Bitmap.createBitmap(screenshot, intX-IMAGE_WIDTH/2, intY-IMAGE_HEIGHT/2, IMAGE_WIDTH,
            IMAGE_HEIGHT, null, false)
    }
    val canvas = android.graphics.Canvas(screenshot)
    val bg = view.background
    if (bg != null)
        bg.draw(canvas)
    else
        canvas.drawColor(android.graphics.Color.WHITE)
    view.draw(canvas)
    return screenshot
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
actual fun autoButton(modifier: Modifier) {
    val context = LocalContext.current
    val activity = context.getActivityOrNull() as MainActivity
    val open = remember { mutableStateOf(false) }
    Button("Auto", onClick = {
        open.value = !open.value
        if (open.value) {
            context.stopService(Intent(context, ScreenshotService::class.java))
            println("Service stopped")
        } else {
            print("${activity.resultCode} ${activity.data}")
            val intent = Intent(context, ScreenshotService::class.java)
            intent.putExtra("RESULT_CODE", activity.resultCode)
            intent.putExtra("DATA", activity.data)
            context.startForegroundService(intent)
            println("Service started")
        }
    },
        modifier = modifier)
}

fun sendScreenshot(screenshot:Bitmap): List<Word> {
    val client = OkHttpClient()
    val outputStream = ByteArrayOutputStream()
    screenshot.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    val json = "application/json; charset=utf-8".toMediaType()
    val body: RequestBody = byteArray.toRequestBody(json)
    val request = Request.Builder()
        .url("http://192.168.31.188:9000/ocr")
        .post(body)
        .build()
    try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Запрос к серверу не был успешен:" +
                        " ${response.code} ${response.message}")
            }
            val result = response.body!!.string()
            println(result)
            val gson = Gson()
            val wordListType = object : TypeToken<List<Word>>() {}.type
            val wordList = gson.fromJson<List<Word>>(result, wordListType)
            return wordList
        }
    } catch (e: IOException) {
        println("Ошибка подключения: $e")
    }
    return listOf(Word("","",""))
}

fun processImage(image:Image): Bitmap {
    val planes = image.planes
    val buffer = planes[0].buffer
    val pixelStride = planes[0].pixelStride
    val rowStride = planes[0].rowStride
    val rowPadding = rowStride - pixelStride * image.width

    val bitmap = Bitmap.createBitmap(
        image.width + rowPadding / pixelStride,
        image.height,
        Bitmap.Config.ARGB_8888).apply {
            copyPixelsFromBuffer(buffer)
    }
    image.close()
    return bitmap
}

fun Context.getActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}
