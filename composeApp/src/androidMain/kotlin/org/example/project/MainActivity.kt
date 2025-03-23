package org.example.project

import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE: Int = 100
    private lateinit var mediaProjectionManager: MediaProjectionManager
    var resultCode = 0
    lateinit var data: Intent

    private val startMediaProjection = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            resultCode = result.resultCode
            data = result.data!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaProjectionManager = getSystemService(android.media.projection.MediaProjectionManager::class.java)
        startMediaProjection.launch(mediaProjectionManager.createScreenCaptureIntent())

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}