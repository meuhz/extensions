package com.example.demo.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object JsonUtils {

    private val gson: Gson by lazy {
        GsonBuilder().create()
    }

    @JvmStatic
    fun toJson(obj: Any?): String? {
        try {
            return gson.toJson(obj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
