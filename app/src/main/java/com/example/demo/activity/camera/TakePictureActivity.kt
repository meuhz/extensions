package com.example.demo.activity.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.demo.R
import com.example.demo.activity.BaseActivity
import com.example.demo.databinding.ActivityTakePictureBinding
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class TakePictureActivity : BaseActivity() {
    private lateinit var binding: ActivityTakePictureBinding
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }
        binding = ActivityTakePictureBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.takePicture.setImageResource(R.drawable.ic_camera)
        binding.takePicture.setOnClickListener {
            if (imageCapture != null) {
                val datetime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val file = File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "JPEG_$datetime.jpg"
                )
                val outputFileOptions = OutputFileOptions.Builder(file)

                imageCapture!!.takePicture(
                    outputFileOptions.build(), Executors.newSingleThreadExecutor(),
                    object : OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: OutputFileResults) {
                            Timber.d("onImageSaved: %s", outputFileResults.savedUri)
                            showToast("onImageSaved: " + outputFileResults.savedUri)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Timber.d("onError: %s", exception.message)
                        }
                    })
            }
        }

        bindCamera()
    }

    private fun bindCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture!!.addListener({
            bindPreview(cameraProviderFuture!!.get())
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        preview.surfaceProvider = binding.previewView.getSurfaceProvider()

        imageCapture = ImageCapture.Builder().build()

        cameraProvider.bindToLifecycle(
            this, CameraSelector.DEFAULT_BACK_CAMERA,
            preview, imageCapture
        )
    }

    private fun useCameraController() {
        val cameraController = LifecycleCameraController(this)
        cameraController.setCameraSelector(CameraSelector.DEFAULT_FRONT_CAMERA)
        cameraController.bindToLifecycle(this)
        binding.previewView.setController(cameraController)
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height) * 1f / min(width, height)
        if (abs(previewRatio - AspectRatio.RATIO_4_3) <= abs(previewRatio - AspectRatio.RATIO_16_9)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            val granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            Timber.d("%s: %s", permissions[0], if (granted) "GRANTED" else "DENIED")
        }
    }
}