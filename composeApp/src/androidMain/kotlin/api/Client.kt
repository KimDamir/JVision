package api

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dataclasses.Query
import dataclasses.User
import dataclasses.Word
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import store.UserStore
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.concurrent.Volatile

class Client private constructor(val client: OkHttpClient){
    companion object {

        @Volatile
        private lateinit var instance: Client

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(context: Context): Client {
            synchronized(this) {
                if(!Companion::instance.isInitialized) {
                    instance = Client(OkHttpClient().newBuilder().addInterceptor(AuthInterceptor(context)).build())
                }
                return instance
            }

        }
    }

}