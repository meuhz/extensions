package com.example.demo.data

import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.ParameterizedType

interface HttpCallback<T> : Callback {
    override fun onFailure(call: Call, e: IOException) {
        try {
            if (e is HttpException) {
                onError(e)
            } else if ("Canceled" == e.message) {
                onError(HttpException("取消请求", e))
            } else {
                onError(HttpException("请求失败", e))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResponse(call: Call, response: Response) {
        if (!response.isSuccessful) {
            onFailure(call, HttpException(response.code, "请求失败"))
            return
        }
        val bodyString: String
        try {
            bodyString = response.body.string()
        } catch (e: IOException) {
            onFailure(call, HttpException("网络错误", e))
            return
        }
        val genericInterface = javaClass.genericInterfaces[0]
        val actualTypeArgument = (genericInterface as ParameterizedType).actualTypeArguments[0]
        val responseType = ParameterizedTypeImpl(HttpResponse::class.java, actualTypeArgument)
        val gson = Gson()
        val httpResponse: HttpResponse<T>
        try {
            httpResponse = gson.fromJson(bodyString, responseType)
        } catch (e: Exception) {
            onFailure(call, HttpException("数据错误", e))
            return
        }
        if (httpResponse.code.all { it != '0' }) {
            onFailure(call, HttpException(httpResponse.code, httpResponse.message))
            return
        }
        try {
            onSuccess(httpResponse)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onSuccess(t: HttpResponse<T>)

    fun onError(e: HttpException)
}
