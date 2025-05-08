package org.example.project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import api.checkToken
import const.viewmodel.JVisionViewModel
import dataclasses.ParcelableWord
import dataclasses.fromParcelable


class MainActivity : AppCompatActivity() {
    private lateinit var mediaProjectionManager: MediaProjectionManager
    var resultCode = 0
    val vm = JVisionViewModel()
    lateinit var data: Intent
    private val mMessageReceiver = object: BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context?, intent: Intent?) {
            val newWordList = intent?.getParcelableArrayListExtra<ParcelableWord>("WordList")
            if (newWordList != null) {
                vm.changeWordList(fromParcelable(newWordList))
            }
        }
    }

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
        ContextCompat.registerReceiver(
            this,
            mMessageReceiver,
            IntentFilter("WordList"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        setContent {
            AppWrapper(vm)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(true)
}

@Composable
fun AppWrapper(viewModel: JVisionViewModel) {
    val context = LocalContext.current
    val isAuthorized = checkToken(context)
    println(isAuthorized)
    App(isAuthorized, viewModel)
}