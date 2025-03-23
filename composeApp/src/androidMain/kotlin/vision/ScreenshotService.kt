package vision

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Resources
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.*
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dictionary.Word
import ui.components.Popup


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
    private var isLoaded: Boolean = false
    private var errorCount = 0

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

        // init your SavedStateRegistryController
        savedStateRegistryController.performAttach() // you can ignore this line, because performRestore method will auto call performAttach() first.
        savedStateRegistryController.performRestore(null)

        // configure your ComposeView
        contentView = buttonView()
        windowManager = getSystemService<WindowManager>()!!
        layoutInflater = getSystemService<LayoutInflater>()!!
        windowMetrics = windowManager.maximumWindowMetrics
        mediaProjectionManager = getSystemService(MediaProjectionManager::class.java) as MediaProjectionManager
    }

    private inner class MediaProjectionStopCallback : MediaProjection.Callback() {
        override fun onStop() {
            handler.post {
                if (virtualDisplay != null) virtualDisplay.release()
                if (imageReader != null) imageReader.setOnImageAvailableListener(null, null)
                mediaProjection.unregisterCallback(this@MediaProjectionStopCallback)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        windowManager.addView(contentView, getWindowButtonParams())
        val notification = NotificationUtils.getNotification(this)
        startForeground(notification.first, notification.second, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        val resultCode = intent!!.getIntExtra("RESULT_CODE", RESULT_CANCELED)
        val data = intent.getParcelableExtra<Intent>("DATA")
        print("$resultCode $data Printed Here")
        if (data != null) {
//            Handler(Looper.getMainLooper()).postDelayed({
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
                mediaProjection.registerCallback(MediaProjectionStopCallback(), handler)

//            }, 1000)

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
        var x = 0F
        var y = 0F
        return ComposeView(this).apply {
            setViewTreeSavedStateRegistryOwner(this@ScreenshotService)
            setContent {
                val xView by remember{mutableStateOf(x)}
                val yView by remember{mutableStateOf(y)}

                Surface(modifier = Modifier.background(Color.Transparent).fillMaxSize().pointerInteropFilter { motionEvent ->
                    x = motionEvent.x
                    y = motionEvent.y
                    println("Clicked! $x  $y")
                    captureScreenshot(x, y)
                    post {
                        windowManager.removeView(this@apply)
                        waitForLoaded(x, y)

                    }
                    false
                }, color = Color.Transparent) {
                    Text("X: $xView     Y: $yView") }
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
                Button(onClick = {
                    screenshotView = screenshotView()
                    post {
                        windowManager.addView(screenshotView, getWindowScreenshotParams())
                    }

                }) {
                    Text("JV")
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun getWindowButtonParams(): WindowManager.LayoutParams {
        val windowMetrics = windowManager.maximumWindowMetrics
        val bounds = windowMetrics.bounds
        return WindowManager.LayoutParams(
            150,  // Width
            150,  // Height
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
        val windowMetrics = windowManager.maximumWindowMetrics
        val bounds = windowMetrics.bounds
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
                Popup(wordList)
            }
            setOnTouchListener { _, _ ->
                post {
                    windowManager.removeView(this)
                }
                performClick()
                true
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getWindowPopupParams(x:Float, y:Float): WindowManager.LayoutParams {
        val windowMetrics = windowManager.maximumWindowMetrics
        val bounds = windowMetrics.bounds
        val layoutParams = WindowManager.LayoutParams(
            bounds.width(),  // Width
            bounds.width()/2,  // Height
            0,  // X position
            y.toInt(),  // Y position
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
                wordList = sendScreenshot(processImage(image))
                isLoaded = true
                println("Deleting imageListener")
                imageReader.close()
                virtualDisplay.release()
                imageReader.setOnImageAvailableListener(null, null)
            }
        }, handler)
    }
    @RequiresApi(Build.VERSION_CODES.R)
    fun waitForLoaded(x: Float, y: Float) {
        if(isLoaded) {
            windowManager.addView(popupView(wordList), getWindowPopupParams(x, y))
        } else {
            errorCount ++
            if (errorCount > 3) {
                errorCount = 0
                return
            }
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    waitForLoaded(x, y)
                },
            100)
        }
    }
}



