package api

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import store.UserStore
import java.io.IOException


class AuthInterceptor(context: Context) : Interceptor {
    private val store = UserStore(context)
    private val authToken = store.getAccessToken
    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        var token: String = ""
        runBlocking {
            token = authToken.firstOrNull() ?: ""
        }
        println(token)
        val original: Request = chain.request()
        val builder: Request.Builder = original.newBuilder()
            .header("Authorization", "Bearer $token")
        val newRequest: Request = builder.build()
        return chain.proceed(newRequest)
    }
}