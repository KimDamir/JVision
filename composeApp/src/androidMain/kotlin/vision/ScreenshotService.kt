package vision

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Resources
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import api.sendScreenshot
import const.viewmodel.JVisionViewModel
import dataclasses.Word
import dataclasses.wordToParcelable
import jvision.composeapp.generated.resources.Res
import jvision.composeapp.generated.resources.icon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import store.UserStore
import ui.components.Popup
import ui.theme.JvisionTheme


// you need extends LifecycleService and implement SavedStateRegistryOwner.
class ScreenshotService : LifecycleService(), SavedStateRegistryOwner {

    // create a SavedStateRegistryController to get SavedStateRegistry object.
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private lateinit var contentView: View
    private lateinit var windowManager: WindowManager
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var windowMetrics: WindowMetrics
    private lateinit var imageReader: ImageReader
    private lateinit var mediaProjection: MediaProjection
    private lateinit var handler: Handler
    private lateinit var screenshotView: View
    private lateinit var wordList: List<Word>
    private lateinit var vm: JVisionViewModel
    private var isLoaded: Boolean = false
    private var errorCount = 0
    private lateinit var bounds: Rect

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate() {
        super.onCreate()
        object : Thread() {
            override fun run() {
                Looper.prepare()
                handler = Handler()
                Looper.loop()
            }
        }.start()

        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)

        contentView = buttonView()
        windowManager = getSystemService<WindowManager>()!!
        layoutInflater = getSystemService<LayoutInflater>()!!
        windowMetrics = windowManager.maximumWindowMetrics
        bounds = windowMetrics.bounds
        mediaProjectionManager = getSystemService(MediaProjectionManager::class.java) as MediaProjectionManager
    }

    private inner class MediaProjectionStopCallback : MediaProjection.Callback() {
        override fun onStop() {
            handler.post {
                if (this@ScreenshotService::virtualDisplay.isInitialized) virtualDisplay.release()
                if (this@ScreenshotService::imageReader.isInitialized) imageReader.setOnImageAvailableListener(null, null)
                mediaProjection.unregisterCallback(this@MediaProjectionStopCallback)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        windowManager.addView(contentView, getWindowButtonParams())
        isLoaded = false
        val notification = NotificationUtils.getNotification(this)
        startForeground(notification.first, notification.second, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        val token = intent!!.getStringExtra("TOKEN")
        CoroutineScope(Dispatchers.IO).launch {
            if (token != null) {
                UserStore(this@ScreenshotService).saveToken(token)
            }
        }

        val resultCode = intent.getIntExtra("RESULT_CODE", RESULT_CANCELED)
        val data = intent.getParcelableExtra<Intent>("DATA")
        if (data != null) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
            mediaProjection.registerCallback(MediaProjectionStopCallback(), handler)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(contentView)
        mediaProjection.stop()
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    @OptIn(ExperimentalComposeUiApi::class)
    @RequiresApi(Build.VERSION_CODES.R)
    private fun screenshotView(): View {
        var x: Float
        var y: Float
        return ComposeView(this).apply {
            setViewTreeSavedStateRegistryOwner(this@ScreenshotService)
            setContent {
                JvisionTheme {
                    Surface(modifier = Modifier.background(Color.Transparent).fillMaxSize().pointerInteropFilter { motionEvent ->
                        x = motionEvent.x
                        y = motionEvent.y

                        captureScreenshot(x, y)
                        post {
                            screenshotView.apply {
                                setContent {
                                    JvisionTheme {
                                        Canvas(
                                            modifier = Modifier.background(Color.Transparent).fillMaxSize()
                                        ) {
                                            drawRect(color = Color.Red,
                                                size = Size(IMAGE_WIDTH.toFloat(), IMAGE_HEIGHT.toFloat()),
                                                topLeft = Offset(x= (x- IMAGE_WIDTH/2), y=(y- IMAGE_HEIGHT/2)),
                                                style = Stroke(width = 3.dp.toPx())
                                            )
                                        }
                                    }
                                }
                            }
                            waitForLoaded(x, y)
                        }
                        false
                    }, color = Color.Transparent) {

                    }
                }
            }
            setViewTreeLifecycleOwner(this@ScreenshotService)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun buttonView(): View {
        return ComposeView(this).apply {
            setViewTreeSavedStateRegistryOwner(this@ScreenshotService)
            setViewTreeLifecycleOwner(this@ScreenshotService)
            setContent {
                JvisionTheme {
                    Button(onClick = {
                        if (errorCount == 0) {
                            screenshotView = screenshotView()
                            post {
                                windowManager.addView(screenshotView, getWindowScreenshotParams())
                            }
                        }
                        Modifier.background(Color.Transparent)
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent, contentColor = Color.Transparent))
                    {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(painter = painterResource(Res.drawable.icon), "JVision button",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.size(150.dp))
                        }
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun getWindowButtonParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            250,  // Width
            250,  // Height
            bounds.width()/2-75,  // X position
            -bounds.height()/2+150,  // Y position
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSPARENT
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getWindowScreenshotParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            bounds.width(),  // Width
            bounds.height(),  // Height
            0,  // X position
            0,  // Y position
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSPARENT
        )
    }

    private fun popupView(wordList: List<Word>): View {
        return ComposeView(this).apply {
            setViewTreeSavedStateRegistryOwner(this@ScreenshotService)
            setViewTreeLifecycleOwner(this@ScreenshotService)
            setContent {
                JvisionTheme {
                    Popup(wordList)
                }
            }
            setOnTouchListener { _, _ ->
                post {
                    windowManager.removeView(screenshotView)
                    windowManager.removeView(this)
                }
                performClick()
                true
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getWindowPopupParams(x:Float, y:Float): WindowManager.LayoutParams {
        val actualY = if (y + bounds.width()/2 > bounds.height()) y - 2*bounds.width()/3  else y
        val layoutParams = WindowManager.LayoutParams(
            bounds.width(),  // Width
            bounds.width()/2,  // Height
            0,  // X position
            actualY.toInt(),  // Y position
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSPARENT
        )
        layoutParams.gravity=Gravity.TOP
        return layoutParams
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun captureScreenshot(x:Float, y:Float) {
        imageReader = ImageReader.newInstance(
            windowMetrics.bounds.width(),
            windowMetrics.bounds.height(),
            PixelFormat.RGBA_8888,
            1
        )
        virtualDisplay = mediaProjection.createVirtualDisplay(
            "Screen",
            windowMetrics.bounds.width(),
            windowMetrics.bounds.height(),
            Resources.getSystem().displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            handler
        )
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            image?.let {
                reader.setOnImageAvailableListener(null, null)
                wordList = sendScreenshot(processImage(image, x, y), this)
                isLoaded = true
                reader.close()
                virtualDisplay.release()
            }
        }, handler)
    }
    @RequiresApi(Build.VERSION_CODES.R)
    fun waitForLoaded(x: Float, y: Float) {
        if(isLoaded) {
            isLoaded = false
            errorCount = 0
            windowManager.addView(popupView(wordList), getWindowPopupParams(x, y))
            val intent = Intent("WordList")
            intent.putParcelableArrayListExtra("WordList", wordToParcelable(wordList))
            this.sendBroadcast(intent)
        } else {
            errorCount ++
            if (errorCount > 35) {
                errorCount = 0
                isLoaded = false
                Toast.makeText(this,
                    "Не удалось получить ответ от сервера.",
                    Toast.LENGTH_LONG
                ).show()
                windowManager.removeView(screenshotView)
                return
            }
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    waitForLoaded(x, y)
                },
            150)
        }
    }
}



