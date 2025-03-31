package api

import android.content.Context
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import okhttp3.OkHttpClient
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