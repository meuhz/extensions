package com.example.demo.activity.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureFailure
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.Observer
import com.example.demo.activity.BaseActivity
import com.example.demo.databinding.ActivityCamera2Binding
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class Camera2Activity : BaseActivity(), SurfaceHolder.Callback,
    ImageReader.OnImageAvailableListener, SurfaceTextureListener {
    private lateinit var binding: ActivityCamera2Binding
    private var mCamera: CameraDevice? = null
    private var mCameraManager: CameraManager? = null
    private var mImageReader: ImageReader? = null
    private var mCharacteristics: CameraCharacteristics? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private var mHandlerThread: HandlerThread? = null
    private var FACING_BACK = "0"
    private var FACING_FRONT = "1"
    private val mFacing: String = FACING_FRONT
    private var mHandler: Handler? = null
    private var relativeOrientation: OrientationLiveData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }
        binding = ActivityCamera2Binding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        mCameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        mHandlerThread = HandlerThread("Camera")
        mHandlerThread!!.start()
        mHandler = Handler(mHandlerThread!!.getLooper())

        binding.takePicture.setOnClickListener { v ->
            if (mCameraCaptureSession != null) {
                v.setEnabled(false)
                takePicture()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.surfaceView.holder.addCallback(this)
        } else {
            binding.surfaceView.setVisibility(View.GONE)
            binding.textureView.visibility = View.VISIBLE
            binding.textureView.surfaceTextureListener = this
        }

        showCameraInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeCamera()
        mHandlerThread!!.quit()
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

    private fun showCameraInfo() {
        try {
            val ids = mCameraManager!!.cameraIdList
            for (id in ids) {
                Timber.d("-------------------------------------------------")
                Timber.d("Camera.Id %s", id)
                val characteristics = mCameraManager!!.getCameraCharacteristics(id)

                val lensFacing: Any? = characteristics.get(CameraCharacteristics.LENS_FACING)
                Timber.d("CameraCharacteristics.LENS_FACING %s", lensFacing)
                if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    FACING_FRONT = id
                } else if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    FACING_BACK = id
                }
                val supportedHardwareLevel: Any? =
                    characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
                Timber.d(
                    "CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL %s",
                    supportedHardwareLevel
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val version: Any? =
                        characteristics.get(CameraCharacteristics.INFO_VERSION)
                    Timber.d("CameraCharacteristics.INFO_VERSION %s", version)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val range: Any? =
                        characteristics.get(CameraCharacteristics.CONTROL_ZOOM_RATIO_RANGE)
                    Timber.d("CameraCharacteristics.CONTROL_ZOOM_RATIO_RANGE %s", range)
                }
            }
            Timber.d("-------------------------------------------------")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 打开相机
     */
    private fun openCamera() {
        Timber.i("openCamera")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            return
        }
        try {
            mCameraManager!!.openCamera(mFacing, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    Timber.i("onOpened: %s", camera)
                    initCamera2(camera)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    Timber.e("onDisconnected: %s", camera)
                    closeCamera()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    closeCamera()
                    val s = when (error) {
                        ERROR_CAMERA_DEVICE -> "Fatal (device)"
                        ERROR_CAMERA_DISABLED -> "Device policy"
                        ERROR_CAMERA_IN_USE -> "Camera in use"
                        ERROR_CAMERA_SERVICE -> "Fatal (service)"
                        ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                        else -> "Unknown"
                    }
                    Timber.e("onError: " + camera + " error=" + s)
                }
            }, mHandler)
        } catch (e: CameraAccessException) {
            Timber.e(e)
        }
    }

    private fun initCamera2(camera: CameraDevice) {
        Timber.i("initCamera2 %s", camera)
        mCamera = camera
        try {
            mCharacteristics = mCameraManager!!.getCameraCharacteristics(camera.id)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        // Used to rotate the output media to match device orientation
        runOnUiThread(Runnable {
            if (relativeOrientation != null) {
                relativeOrientation!!.removeObservers(this)
            }
            relativeOrientation = OrientationLiveData(this, mCharacteristics!!)
            relativeOrientation!!.observe(this, object : Observer<Int?> {
                override fun onChanged(value: Int?) {
                    Timber.i("relativeOrientation onChanged: %s", value)
                }
            })
        })
        val map = mCharacteristics!!.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        Timber.i("OutputFormats: %s", map!!.outputFormats.contentToString())
        val sizes = map.getOutputSizes(ImageFormat.JPEG)
        Timber.i("OutputSizes: %s", sizes.contentToString())
        val pictureSize = CameraUtil.getPictureSize(sizes, 4000 * 3000)
        Timber.i("PictureSize: %s", pictureSize)
        mImageReader = ImageReader.newInstance(
            pictureSize.width,
            pictureSize.height,
            ImageFormat.JPEG,
            2
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val surface = binding.surfaceView.holder.surface
                val outputConfiguration = OutputConfiguration(surface)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    outputConfiguration.setMirrorMode(OutputConfiguration.MIRROR_MODE_NONE)
                }
                val captureOutputConfiguration = OutputConfiguration(mImageReader!!.surface)

                val sessionConfiguration = SessionConfiguration(
                    SessionConfiguration.SESSION_REGULAR,
                    listOf(outputConfiguration, captureOutputConfiguration),
                    Executors.newSingleThreadExecutor(),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            Timber.i("onConfigured: session=%s", session)
                            // This will keep sending the capture request as frequently as possible until the
                            // session is torn down or session.stopRepeating() is called
                            mCameraCaptureSession = session
                            try {
                                val captureRequest =
                                    camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                                captureRequest.addTarget(surface)
                                mCameraCaptureSession!!.setRepeatingRequest(
                                    captureRequest.build(),
                                    null,
                                    mHandler
                                )
                            } catch (e: CameraAccessException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            Timber.e("onConfigureFailed " + session)
                        }
                    })
                camera.createCaptureSession(sessionConfiguration)
            } else {
                val matrix = Matrix()
                matrix.setScale(-1f, 1f)
                matrix.postTranslate(binding.textureView.width.toFloat(), 0f)
                binding.textureView.setTransform(matrix)

                val surfaceTexture = binding.textureView.surfaceTexture
                surfaceTexture!!.setDefaultBufferSize(
                    pictureSize.width,
                    pictureSize.height
                )
                val surface = Surface(surfaceTexture)

                val targets = listOf(surface, mImageReader!!.surface)
                camera.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        Timber.i("onConfigured: session=%s", session)
                        // This will keep sending the capture request as frequently as possible until the
                        // session is torn down or session.stopRepeating() is called
                        mCameraCaptureSession = session
                        try {
                            val captureRequest =
                                camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                            captureRequest.addTarget(surface)
                            mCameraCaptureSession!!.setRepeatingRequest(
                                captureRequest.build(),
                                null,
                                mHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Timber.e("onConfigureFailed %s", session)
                    }
                }, mHandler)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() {
        Timber.d("closeCamera %s", mCamera)
        if (mCamera != null) {
            mCamera!!.close()
            mCamera = null
        }
    }

    private fun takePicture() {
        try {
            val captureBuilder =
                mCamera!!.createCaptureRequest(CameraDevice.TEMPLATE_ZERO_SHUTTER_LAG)
            captureBuilder.addTarget(mImageReader!!.surface)

            captureBuilder.set(
                CaptureRequest.JPEG_ORIENTATION,
                relativeOrientation!!.getValue()
            )

            val sensorOrientation: Any? =
                mCharacteristics!!.get(CameraCharacteristics.SENSOR_ORIENTATION)
            Timber.i("SensorOrientation: %s", sensorOrientation)

            // Timber.Timber.i("DisplayRotation " + getRotationDegree(getDisplay().getRotation()));
            mImageReader!!.setOnImageAvailableListener(this, mHandler)
            mCameraCaptureSession!!.capture(
                captureBuilder.build(),
                object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureStarted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        timestamp: Long,
                        frameNumber: Long
                    ) {
                        super.onCaptureStarted(session, request, timestamp, frameNumber)
                        Timber.i(
                            "onCaptureStarted: timestamp=%s, frameNumber=%s",
                            timestamp,
                            frameNumber
                        )
                    }

                    override fun onCaptureFailed(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        failure: CaptureFailure
                    ) {
                        super.onCaptureFailed(session, request, failure)
                        Timber.e("onCaptureFailed: failure=%s", failure.reason)
                    }

                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult
                    ) {
                        super.onCaptureCompleted(session, request, result)
                        Timber.i("onCaptureCompleted: result=%s", result)

                        // mImageReader.setOnImageAvailableListener(null, null);
                        runOnUiThread(Runnable {
                            binding.takePicture.setEnabled(true)
                        })
                    }
                },
                mHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun onImageAvailable(reader: ImageReader) {
        Timber.i("onImageAvailable: %s", reader)
        try {
            val image = reader.acquireLatestImage()
            Timber.i("Width: %s", image.width)
            Timber.i("Height: %s", image.height)
            Timber.i("Format: %s", image.format)
            val datetime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "IMG_$datetime.jpg"
            )
            val output: OutputStream = FileOutputStream(file)
            Timber.i("file: %s", file.absolutePath)
            val buffer = image.planes[0].buffer
            Timber.i("capacity: %s", buffer.capacity())
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)
            image.close()

            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)

            // Matrix matrix = new Matrix();
            // matrix.postRotate(relativeOrientation.getValue());
            // bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
            Timber.i("result: %s", file.length())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val exif = ExifInterface(file)
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, "270")
                exif.saveAttributes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Timber.i("surfaceCreated: holder=%s", holder)
        openCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Timber.d(
            "surfaceChanged: holder=%s, format=%d, width=%d, height=%d",
            holder,
            format,
            width,
            height
        )
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Timber.d("surfaceDestroyed: holder=%s", holder)
        closeCamera()
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        Timber.d(
            "onSurfaceTextureAvailable: surface=%s, width=%d, height=%d",
            surface,
            width,
            height
        )
        openCamera()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        Timber.d(
            "onSurfaceTextureSizeChanged: surface=%s, width=%d, height=%d",
            surface,
            width,
            height
        )
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        Timber.d("onSurfaceTextureDestroyed: surface=%s", surface)
        closeCamera()
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }
}