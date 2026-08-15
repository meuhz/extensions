package com.example.demo.activity.camera

import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface

object CameraUtil {

    @JvmStatic
    fun isFront(cameraManager: CameraManager, cameraDevice: CameraDevice): Boolean {
        try {
            val c = cameraManager.getCameraCharacteristics(cameraDevice.getId())
            return CameraCharacteristics.LENS_FACING_FRONT == c.get(CameraCharacteristics.LENS_FACING)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return false
    }

    @JvmStatic
    fun isFront(c: CameraCharacteristics): Boolean {
        return CameraCharacteristics.LENS_FACING_FRONT == c.get(CameraCharacteristics.LENS_FACING)
    }

    @JvmStatic
    fun isBack(cameraManager: CameraManager, cameraDevice: CameraDevice): Boolean {
        try {
            val c = cameraManager.getCameraCharacteristics(cameraDevice.id)
            return CameraCharacteristics.LENS_FACING_BACK == c.get(CameraCharacteristics.LENS_FACING)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return false
    }

    @JvmStatic
    fun isBack(c: CameraCharacteristics): Boolean {
        return CameraCharacteristics.LENS_FACING_BACK == c.get(CameraCharacteristics.LENS_FACING)
    }

    @JvmStatic
    fun getJpegOrientation(c: CameraCharacteristics, deviceOrientation: Int): Int {
        var deviceOrientation = deviceOrientation
        if (deviceOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) return 0
        val sensorOrientation: Int = c.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90

        // Reverse device orientation for front-facing cameras
        val facingFront =
            c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
        if (facingFront) deviceOrientation = -deviceOrientation

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        val jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360

        return jpegOrientation
    }

    /**
     * 获取 4:3 图像尺寸
     */
    @JvmStatic
    fun getPictureSize(previewSizes: Array<Size>, maxSize: Int): Size {
        var retSize = previewSizes[0]
        var curSize = 0
        for (size in previewSizes) {
            val width = size.width
            val height = size.height

            val max = width * height
            if (max > maxSize) continue

            val gcd = gcd(width, height)
            val w = width / gcd
            val h = height / gcd
            if (w == 4 && h == 3) {
                if (max > curSize) {
                    curSize = max
                    retSize = size
                }
            }
        }
        return retSize
    }

    /**
     * 计算最大公约数
     */
    private fun gcd(a: Int, b: Int): Int {
        if (b == 0) return a
        return gcd(b, a % b)
    }

    @JvmStatic
    fun getRotationDegree(rotation: Int): Int {
        return when (rotation) {
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_0 -> 0
            else -> 0
        }
    }
}
