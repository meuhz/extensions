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
                onError(HttpException("网络错误", e))
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
            onFailure(call, HttpException("请求失败", e))
            return
        }
        val genericInterfaces = javaClass.getGenericInterfaces()[0]
        val typeArgument = (genericInterfaces as ParameterizedType).actualTypeArguments[0]
        val responseType = ParameterizedTypeImpl(ResponseData::class.java, typeArgument)
        val gson = Gson()
        val responseData: ResponseData<T>
        try {
            responseData = gson.fromJson(bodyString, responseType)
        } catch (e: Exception) {
            onFailure(call, HttpException("数据错误", e))
            return
        }
        if (responseData.code != "0") {
            val code = try {
                responseData.code?.toInt()
            } catch (_: Exception) {
                0
            }
            onFailure(call, HttpException(code ?: 0, responseData.message ?: "数据错误"))
            return
        }
        if (responseData.data == null) {
            onFailure(call, HttpException("数据错误"))
            return
        }
        try {
            onSuccess(responseData.data!!)
        } catch (_: Exception) {
        }
    }

    fun onSuccess(t: T)

    fun onError(e: HttpException)
}