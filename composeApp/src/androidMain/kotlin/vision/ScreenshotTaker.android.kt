package vision

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.MainActivity
import store.UserStore
import ui.components.Button


const val IMAGE_WIDTH = 256
const val IMAGE_HEIGHT = 80

@RequiresApi(Build.VERSION_CODES.O)
actual fun takeScreenshot() {
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
actual fun listenForCall(action: () -> Unit) {
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
actual fun autoButton(modifier: Modifier) {
    val context = LocalContext.current
    val activity = context.getActivityOrNull() as MainActivity
    val open = remember { mutableStateOf(false) }
    Button("Auto", onClick = {
        if (open.value) {
            context.stopService(Intent(context, ScreenshotService::class.java))
            println("Service stopped")
        } else {
            print("${activity.resultCode} ${activity.data}")
            val intent = Intent(context, ScreenshotService::class.java)
            intent.putExtra("RESULT_CODE", activity.resultCode)
            intent.putExtra("DATA", activity.data)
            CoroutineScope(Dispatchers.IO).launch {
                UserStore(context).getAccessToken.collect{value -> intent.putExtra("TOKEN", value)}
            }
            context.startForegroundService(intent)
            println("Service started")
        }
        open.value = !open.value
    },
        modifier = modifier)
}



fun processImage(image:Image, x:Float, y:Float): Bitmap {
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
    var intX = x.toInt() - IMAGE_WIDTH/2
    val intY = y.toInt() - IMAGE_HEIGHT/2 - 17
    if (intX + IMAGE_WIDTH > bitmap.width) {
        intX = bitmap.width - IMAGE_WIDTH
    } else if (intX < 0) intX = 0


    val screenshot = Bitmap.createBitmap(bitmap, intX, intY, IMAGE_WIDTH,
        IMAGE_HEIGHT, null, false)
    image.close()
    return screenshot
}

fun Context.getActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}
