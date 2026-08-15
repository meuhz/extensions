package com.example.demo.data

import com.example.demo.UIApp
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException

object HttpApi {
    private const val baseUrl = "https://localhost/"
    private val httpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        if (UIApp.INSTANCE.isDebuggable) {
            builder.addInterceptor(HttpLoggingInterceptor { message ->
                Timber.d(message)
            }.setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        httpClient = builder.build()
    }

    private fun mainUrl(url: String): HttpUrl {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url.toHttpUrl()
        }
        if (url.startsWith("/")) {
            return (baseUrl + url.substring(1)).toHttpUrl()
        }
        return (baseUrl + url).toHttpUrl()
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

    @Throws(IOException::class)
    suspend fun getString(url: String): String = withContext(Dispatchers.IO) {
        val httpUrl = mainUrl(url)
        val request = Request.Builder().url(httpUrl).build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code} ${response.message}")
            }
            response.body.string()
        }
    }

    @Throws(IOException::class)
    suspend fun <T> postJson(url: String, json: String, type: Class<T>): T? =
        withContext(Dispatchers.IO) {
            val httpUrl = mainUrl(url)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val request = Request.Builder().url(httpUrl).post(requestBody).build()
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Http ${response.code} ${response.message}")
                }
                val bodyString = response.body.string()
                val gson = Gson()
                val responseData: ResponseData<T>
                try {
                    responseData = gson.fromJson(
                        bodyString,
                        ParameterizedTypeImpl(ResponseData::class.java, type)
                    )
                } catch (e: Exception) {
                    throw IOException(e)
                }
                if (responseData.code != "0") {
                    throw IOException("Response ${responseData.code} ${responseData.message}")
                }
                responseData.data
            }
        }

}