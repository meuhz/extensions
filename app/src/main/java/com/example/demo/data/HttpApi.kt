package com.example.demo.data

import com.example.demo.UIApp
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException

object HttpApi {
    val JSON_TYPE = "application/json; charset=utf-8".toMediaType()
    val httpClient: OkHttpClient
    var baseUrl = "https://localhost/"

    init {
        val builder = OkHttpClient.Builder()
        if (UIApp.isDebuggable) {
            builder.addInterceptor(HttpLoggingInterceptor { message ->
                Timber.d(message)
            }.setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        httpClient = builder.build()
    }

    private fun mainUrl(url: String): String {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url
        }
        if (url.startsWith("/")) {
            return baseUrl + url.substring(1)
        }
        return baseUrl + url
    }

    fun cancel(tag: Any) {
        val dispatcher = httpClient.dispatcher
        for (call in dispatcher.queuedCalls()) {
            if (call.request().tag() == tag) {
                call.cancel()
            }
        }
        for (call in dispatcher.runningCalls()) {
            if (call.request().tag() == tag) {
                call.cancel()
            }
        }
    }

    suspend fun subscribe(url: String, data: Map<String, Any>): String? {
        val json = Gson().toJson(data)
        val httpResponse: HttpResponse<String> = postJson(mainUrl(url), json)
        return httpResponse.data
    }

    @Throws(IOException::class)
    suspend fun <T> postJson(url: String, json: String): HttpResponse<T> {
        val requestBody = json.toRequestBody(JSON_TYPE)
        val request = Request.Builder().url(url).post(requestBody).build()
        val httpResponse: HttpResponse<T> = execute(request)
        if (httpResponse.code.all { it != '0' }) {
            throw HttpException(httpResponse.code, httpResponse.message)
        }
        return httpResponse
    }

    @Throws(IOException::class)
    suspend inline fun <reified T> execute(request: Request): T = withContext(Dispatchers.IO) {
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw HttpException(response.code, response.message)
            }
            val bodyString = response.body.string()
            val gson = Gson()
            try {
                gson.fromJson(bodyString, T::class.java)
            } catch (e: Exception) {
                throw HttpException(e)
            }
        }
    }
}
