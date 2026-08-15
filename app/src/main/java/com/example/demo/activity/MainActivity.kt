package com.example.demo.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.activity.camera.Camera2Activity
import com.example.demo.activity.camera.TakePictureActivity
import com.example.demo.activity.compose.ComposeActivity
import com.example.demo.activity.paging.Paging3Activity
import com.example.demo.adapter.ViewHolderImpl
import com.example.demo.databinding.ActivityMainBinding
import com.example.demo.util.AsyncTask
import com.example.demo.util.KeyboardWatcher
import com.example.demo.util.KeyboardWatcher.SoftKeyboardListener
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import timber.log.Timber
import java.util.Arrays

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var popupWindow: MainPopupWindow? = null
    private var count = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { count > 0 }
        object : CountDownTimer(count * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                count = 0
            }
        }.start()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        setSupportActionBar(binding.toolbar)

        binding.recyclerview.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        binding.recyclerview.setAdapter(ArrayRecyclerViewAdapter())

        buildInfo()
        showInternet()
        showDisplay()

        XXPermissions.with(this)
            .permission(PermissionLists.getReadMediaImagesPermission())
            .permission(PermissionLists.getReadMediaVisualUserSelectedPermission())
            .request(object : OnPermissionCallback {
                override fun onPermissionResult(
                    grantedList: List<IPermission>,
                    deniedList: List<IPermission>
                ) {
                    Timber.d(
                        "onPermissionResult: grantedList=%s, deniedList=%s",
                        grantedList,
                        deniedList
                    )
                }
            })

        KeyboardWatcher.with(this).on(object : SoftKeyboardListener {
            override fun onSoftKeyboardOpened(keyboardHeight: Int) {
                Timber.d("onSoftKeyboardOpened: keyboardHeight=%s", keyboardHeight)
            }

            override fun onSoftKeyboardClosed() {
                Timber.d("onSoftKeyboardClosed")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            if (popupWindow == null) {
                popupWindow = MainPopupWindow(this)
            }
            popupWindow!!.showAsDropDown(binding.toolbar)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun execFunc(view: View, cmd: String) {
        when (cmd) {
            "execute" -> {
                // showSnackbar(view)
                AsyncTask.launch({
                    Timber.d("doAction1")
                    Thread.sleep(1000)
                    Timber.d("doAction2")
                }) {
                    Timber.d("done")
                }
            }

            "webview" -> startActivity(Intent(this, WebActivity::class.java))
            "compose" -> startActivity(Intent(this, ComposeActivity::class.java))
            "http" -> startActivity(Intent(this, HttpActivity::class.java))
            "animation" -> startActivity(Intent(this, AnimationActivity::class.java))
            "paging" -> startActivity(Intent(this, Paging3Activity::class.java))
            "database" -> startActivity(Intent(this, DatabaseActivity::class.java))
            "camera2" -> startActivity(Intent(this, Camera2Activity::class.java))
            "takePicture" -> startActivity(Intent(this, TakePictureActivity::class.java))
        }
    }

    private fun showSnackbar(v: View) {
        Timber.d("showSnackbar")
        Snackbar.make(v, "Snackbar", Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.white))
            .setTextColor(ContextCompat.getColor(this, R.color.deep_purple_100))
            .setAction("Ok") { showToast("Snackbar Ok") }
            .setAnchorView(v)
            .show()
    }

    private fun buildInfo() {
        // Timber.d("Build.BRAND: %s", Build.BRAND)
        // Timber.d("Build.MODEL: %s", Build.MODEL)
        // Timber.d("Build.HARDWARE: %s", Build.HARDWARE)
        try {
            val fields = Build::class.java.getFields()
            for (field in fields) {
                val value = field.get(null)
                if (value != null && value.javaClass.isArray) {
                    Timber.d("Build.%s: %s", field.getName(), Arrays.toString(value as Array<*>))
                } else {
                    Timber.d("Build.%s: %s", field.getName(), value)
                }
            }
        } catch (e: IllegalAccessException) {
            Timber.e(e)
        }
    }

    fun showDialog() {
        Timber.d("openDialog")
        AlertDialog.Builder(this)
            .setTitle("AlertDialog")
            .setMessage("This is AlertDialog")
            .setCancelable(true)
            .setPositiveButton(
                "LoadingDialog",
                { dialog, which -> }
            )
            .setNegativeButton(
                "LoadingDialogFragment",
                { dialog, which -> }
            )
            .show()
    }

    fun showInternet() {
        val manager = getSystemService(ConnectivityManager::class.java) ?: return
        val network = manager.activeNetwork ?: return
        Timber.d("activeNetwork: %s", network)
        val networkCapabilities = manager.getNetworkCapabilities(network)
        Timber.d("networkCapabilities: %s", networkCapabilities)
        val linkProperties = manager.getLinkProperties(network)
        Timber.d("linkProperties: %s", linkProperties)
        if (linkProperties != null) {
            Timber.d("linkAddresses: %s", linkProperties.linkAddresses)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Timber.d("nat64Prefix: %s", linkProperties.nat64Prefix)
            }
        }
    }

    fun showDisplay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            Timber.d(windowMetrics.toString())
        } else {
            val wm = ContextCompat.getSystemService(this, WindowManager::class.java) ?: return
            val metrics = DisplayMetrics()
            val display = wm.defaultDisplay
            display.getMetrics(metrics)
            Timber.d("getMetrics: %s", metrics)
            display.getRealMetrics(metrics)
            Timber.d("getRealMetrics: %s", metrics)
        }
    }

    private inner class ArrayRecyclerViewAdapter : RecyclerView.Adapter<ViewHolderImpl>() {
        val data = arrayOf(
            "execute",
            "compose",
            "http",
            "webview",
            "paging",
            "database",
            "animation",
            "camera2",
            "takePicture"
        )

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderImpl {
            return ViewHolderImpl.create(parent, R.layout.item_textview)
        }

        override fun onBindViewHolder(holder: ViewHolderImpl, position: Int) {
            val item = data[position]
            val textView = holder.itemView as TextView
            textView.text = item
            textView.setOnClickListener { v ->
                execFunc(v, item)
            }
        }
    }
}
