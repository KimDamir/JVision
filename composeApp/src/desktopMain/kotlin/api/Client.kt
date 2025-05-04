package api

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import okhttp3.OkHttpClient
import kotlin.concurrent.Volatile

class Client private constructor(val client: OkHttpClient){
    companion object {

        @Volatile
        private lateinit var instance: Client

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(): Client {
            synchronized(this) {
                if(!Companion::instance.isInitialized) {
                    instance = Client(OkHttpClient().newBuilder().addInterceptor(AuthInterceptor()).build())
                }
                return instance
            }

        }
    }

}