package com.example.demo.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.demo.data.HttpApi
import com.example.demo.data.RestApi
import com.example.demo.databinding.ActivityHttpBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class HttpActivity : BaseActivity() {
    private lateinit var binding: ActivityHttpBinding
    private lateinit var restApi: RestApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHttpBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.input.setText("https://mdn.github.io/learning-area/javascript/apis/fetching-data/can-store/products.json")
        binding.request.setOnClickListener {
            val text = binding.input.getText().toString()
            if (!text.isBlank()) {
                request(text)
            }
        }
        binding.retrofit.setOnClickListener {
            val text = binding.input.getText().toString()
            if (!text.isBlank()) {
                restApi.get(text).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        try {
                            val s = response.body()?.string()
                            binding.text.text = s
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Timber.e(t)
                    }
                })

                lifecycleScope.launch {
                    try {
                        val s = restApi.getString(text)
                        binding.text.text = s
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }
        binding.cancel.setOnClickListener { }

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(client)
            .build()
        restApi = retrofit.create(RestApi::class.java)
    }

    private fun request(url: String) {
        lifecycleScope.launch {
            try {
                binding.text.text = ""
            } catch (e: Exception) {
                binding.text.text = e.message
                Timber.e(e)
            }
        }
    }
}
