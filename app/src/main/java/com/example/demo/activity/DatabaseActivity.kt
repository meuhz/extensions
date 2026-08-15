package com.example.demo.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.demo.UIApp
import com.example.demo.database.UserInfo
import com.example.demo.databinding.ActivityDatabaseBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import java.util.stream.Collectors

class DatabaseActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val userDao = UIApp.INSTANCE.getDatabase().userDao()

        binding.insert.setOnClickListener {
            lifecycleScope.launch {
                val u = UserInfo()
                u.username = UUID.randomUUID().toString()
                    .replace("-".toRegex(), "")
                    .substring(0, 16)
                u.password = "123456"
                u.nickname = "user-" + (userDao.count() + 1)
                Timber.d("insert %s", Gson().toJson(u))
                userDao.insert(u)
            }
        }

        binding.query.setOnClickListener {
            lifecycleScope.launch {
                val list = userDao.getAllUser()
                Timber.d("getAllUser %s", list)
                val gson = Gson()
                val json = list.stream()
                    .map { src -> gson.toJson(src) }
                    .collect(Collectors.joining("\n"))
                binding.textShow.text = json
            }
        }
        binding.clear.setOnClickListener {
            lifecycleScope.launch {
                Timber.d("clear")
                userDao.deleteAll()
            }
        }
    }
}