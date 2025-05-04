package api

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import preferences.UserPreferences
import java.io.IOException


class AuthInterceptor() : Interceptor {
    private val token = UserPreferences().getToken()
    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        val original: Request = chain.request()
        val builder: Request.Builder = original.newBuilder()
            .header("Authorization", "Bearer $token")
        val newRequest: Request = builder.build()
        return chain.proceed(newRequest)
    }
}