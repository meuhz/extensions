package com.example.demo.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface RestApi {
    @GET
    fun get(@Url url: String): Call<ResponseBody>

    @GET
    fun get(@Url url: String, @QueryMap map: MutableMap<String, String?>): Call<ResponseBody>

    @FormUrlEncoded
    @POST
    fun post(@Url url: String, @FieldMap map: MutableMap<String, String?>): Call<ResponseBody>

    @POST
    @Headers("Content-Type: application/json; charset=utf-8")
    fun postJson(@Url url: String, @Body jsonBody: Any): Call<ResponseBody>
}