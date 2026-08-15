package com.example.demo.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.demo.R

class MultiFragmentActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        val fragmentClass = intent.getStringExtra("fragment")
        if (fragmentClass != null) {
            val manager = supportFragmentManager
            val fragment = manager.getFragmentFactory().instantiate(classLoader, fragmentClass)
            val arguments = intent.getBundleExtra("arguments")
            if (arguments != null) {
                fragment.setArguments(arguments)
            }
            manager.beginTransaction()
                .add(R.id.fragment_container_view, fragment)
                .commit()
        }
    }

    companion object {
        fun start(context: Context, fragment: Class<out Fragment>, arguments: Bundle? = null) {
            val intent = Intent(context, MultiFragmentActivity::class.java)
            intent.putExtra("fragment", fragment.getName())
            if (arguments != null) {
                intent.putExtra("arguments", arguments)
            }
            context.startActivity(intent)
        }
    }
}
