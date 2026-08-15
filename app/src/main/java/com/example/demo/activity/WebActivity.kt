package com.example.demo.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import androidx.webkit.WebViewAssetLoader.InternalStoragePathHandler
import androidx.webkit.WebViewAssetLoader.ResourcesPathHandler
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewCompat
import com.example.demo.databinding.ActivityWebviewBinding
import timber.log.Timber
import java.io.File

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(applicationContext)
        if (webViewPackageInfo != null) {
            Timber.d("WebView: %s", webViewPackageInfo.packageName)
            Timber.d("WebView: %s", webViewPackageInfo.versionName)
        }

        val webView = binding.webview
        initWebView(webView)
        webView.setWebViewClient(CustomWebViewClient())
        webView.setWebChromeClient(CustomWebChromeClient())

        var url = intent.getStringExtra("url")
        if (url == null) {
            url = intent.dataString
        }
        if (url != null) {
            webView.loadUrl(url)
        }
        webView.loadUrl("file:///android_asset/test.html")
        // webView.loadUrl("https://www.baidu.com");
    }

    override fun onResume() {
        super.onResume()
        binding.webview.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.webview.onPause()
    }

    override fun onDestroy() {
        val webView = binding.webview
        val parent = webView.parent
        if (parent is ViewGroup) {
            parent.removeView(webView)
        }

        webView.stopLoading()
        webView.clearHistory()
        webView.removeAllViews()
        webView.destroy()

        super.onDestroy()
    }

    override fun onBackPressed() {
        val webView = binding.webview
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun initWebView(webview: WebView) {
        val settings = webview.getSettings()
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        // Setting this off for security. Off by default for SDK versions >= 16.
        settings.allowFileAccessFromFileURLs = false
        // Off by default, deprecated for SDK versions >= 30.
        settings.allowUniversalAccessFromFileURLs = false
        // Keeping these off is less critical but still a good idea, especially if your app is not
        // using file:// or content:// URLs.
        settings.allowFileAccess = false
        settings.allowContentAccess = false
    }

    private inner class CustomWebViewClient : WebViewClientCompat() {
        private val assetLoader: WebViewAssetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", AssetsPathHandler(application))
            .addPathHandler("/res/", ResourcesPathHandler(application))
            .addPathHandler(
                "/file/", InternalStoragePathHandler(
                    application, File(cacheDir, "webview")
                )
            )
            .setDomain("localhost")
            .setHttpAllowed(true)
            .build()

        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            return assetLoader.shouldInterceptRequest(request.url)
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            if (url.contains("baidu.com")) {
                return true
            } else if (url.startsWith("file://")) {
                return true
            } else {
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
    }

    private inner class CustomWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            Timber.d("onProgressChanged: %s", newProgress)
            binding.progressBar.progress = newProgress
            if (newProgress == 100) {
                binding.progressBar.visibility = View.GONE
            }
            if (newProgress == 0) {
                binding.progressBar.visibility = View.VISIBLE
            }
        }

        override fun onReceivedTitle(view: WebView, title: String?) {
            super.onReceivedTitle(view, title)
            Timber.d("onReceivedTitle: %s", title)
            if (title != null) {
                binding.toolbar.setTitle(title)
            }
        }
    }
}
