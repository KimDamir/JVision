package api

import android.content.Context
import android.graphics.Bitmap
import api.Client.Companion.getInstance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dataclasses.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import store.UserStore
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.CountDownLatch
import const.URL
import ui.components.Options

fun sendScreenshot(screenshot: Bitmap, context: Context): List<Word> {
    val client = getInstance(context).client
    val outputStream = ByteArrayOutputStream()
    screenshot.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    val json = "application/json; charset=utf-8".toMediaType()
    val body: RequestBody = byteArray.toRequestBody(json)
    val request = Request.Builder()
        .url("$URL/ocr")
        .post(body)
        .build()
    try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Запрос к серверу не был успешен:" +
                        " ${response.code} ${response.message}")
            }
            val result = response.body!!.string()
            val gson = Gson()
            val wordListType = object : TypeToken<List<Word>>() {}.type
            val wordList = gson.fromJson<List<Word>>(result, wordListType)
            return wordList
        }
    } catch (e: IOException) {
        println("Ошибка подключения: $e")
    }
    return listOf(Word(listOf(""), listOf(""), listOf("")))
}
fun checkToken(context: Context): Boolean {
    val client = getInstance(context).client
    var isSuccess = false
    val request = Request.Builder()
        .url("$URL/token")
        .get()
        .build()
    val countDown = CountDownLatch(1)
    try {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                countDown.countDown()
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    isSuccess =  response.code == 200
                    countDown.countDown()
                }
            }
        })
    } catch (e: IOException) {
        println("Ошибка подключения: $e")
    }
    countDown.await()
    return isSuccess
}

fun login(email: String, password:String, context: Context): Boolean {
    val client = getInstance(context).client
    val request = Request.Builder()
        .url("$URL/user/$email")
        .get()
        .headers(Headers.Builder().addUnsafeNonAscii("Password", password).build())
        .build()
    var isAuthSuccessful = false
    val countDown = CountDownLatch(1)
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            countDown.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    println("Запрос к серверу не был успешен:" +
                            " ${response.code} ${response.body!!.string()}")
                    countDown.countDown()
                } else {
                    val result = response.body!!.string()
                    val authResponse = Gson().fromJson(result, AuthorizationResponse::class.java)
                    if (authResponse.token != "") {
                        CoroutineScope(Dispatchers.IO).launch {
                            UserStore(context).saveToken(authResponse.token)
                        }
                        isAuthSuccessful = true
                    }
                    countDown.countDown()
                }
            }
        }
    })
    countDown.await()
    return isAuthSuccessful
}

fun register(user: User, context: Context): Boolean {
    val client = getInstance(context).client
    var isRegistrationSuccessful = false
    val countDown = CountDownLatch(1)
    val gson = Gson()
    val userData = gson.toJson(user)
    val json = "application/json; charset=utf-8".toMediaType()
    val body: RequestBody = userData.toRequestBody(json)
    val request = Request.Builder()
        .url("$URL/user")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            countDown.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    println(
                        "Запрос к серверу не был успешен:" +
                                " ${response.code} ${response.message}"
                    )
                    countDown.countDown()
                } else {
                    val result = response.body!!.string()
                    val regResponse = Gson().fromJson(result, RegistrationResponse::class.java)
                    if (regResponse.message != "") {
                        isRegistrationSuccessful = true
                    }
                    countDown.countDown()
                }

            }
        }
    })
    countDown.await()
    return isRegistrationSuccessful
}

fun getHistory(context: Context, filter:String = Options.DAY.text): List<Query> {
    val client = getInstance(context).client
    var history: List<Query> = listOf()
    val countDown = CountDownLatch(1)
    val request = Request.Builder()
        .url("$URL/history")
        .get()
        .headers(Headers.Builder().addUnsafeNonAscii("Filter", filter).build())
        .build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            countDown.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    println(
                        "Запрос к серверу не был успешен:" +
                                " ${response.code} ${response.body!!.string()}"
                    )
                    countDown.countDown()
                } else {
                    val result = response.body!!.string()
                    val gson = Gson()
                    val queryListType = object : TypeToken<List<Query>>() {}.type
                    history = gson.fromJson(result, queryListType)
                    countDown.countDown()
                }
            }
        }
    })
    countDown.await()
    return history
}

fun sendQuery(context: Context, query_text:String): List<Word> {
    val client = getInstance(context).client
    var wordList: List<Word> = listOf(Word(listOf(""), listOf(""), listOf("")))
    val countDown = CountDownLatch(1)
    val request = Request.Builder()
        .url("$URL/query/$query_text")
        .get()
        .build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            countDown.countDown()
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    println(
                        "Запрос к серверу не был успешен:" +
                                " ${response.code} ${response.body!!.string()}"
                    )
                    countDown.countDown()
                } else {
                    val result = response.body!!.string()
                    val gson = Gson()
                    val queryListType = object : TypeToken<List<Word>>() {}.type
                    wordList = gson.fromJson(result, queryListType)
                    countDown.countDown()
                }
            }
        }
    })
    countDown.await()
    return wordList
}


