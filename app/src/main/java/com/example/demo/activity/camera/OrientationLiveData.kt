package com.example.demo.activity.camera

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.view.OrientationEventListener
import android.view.Surface
import androidx.lifecycle.LiveData

/**
 * Calculates closest 90-degree orientation to compensate for the device
 * rotation relative to sensor orientation, i.e., allows user to see camera
 * frames with the expected orientation.
 */
class OrientationLiveData(context: Context, characteristics: CameraCharacteristics) :
    LiveData<Int?>() {
    private val listener: OrientationEventListener =
        object : OrientationEventListener(context.applicationContext) {
            override fun onOrientationChanged(orientation: Int) {
                var rotation = 0
                if (orientation <= 45) {
                    rotation = 0
                } else if (orientation <= 135) {
                    rotation = Surface.ROTATION_90
                } else if (orientation <= 225) {
                    rotation = Surface.ROTATION_180
                } else if (orientation <= 315) {
                    rotation = Surface.ROTATION_270
                }
                val relative: Int = computeRelativeRotation(characteristics, rotation)
                if (getValue() == null || relative != getValue()) {
                    postValue(relative)
                }
            }
        }

    override fun onActive() {
        super.onActive()
        listener.enable()
    }

    override fun onInactive() {
        super.onInactive()
        listener.disable()
    }

    companion object {
        /**
         * Computes rotation required to transform from the camera sensor orientation to the
         * device's current orientation in degrees.
         * 
         * @param characteristics the [CameraCharacteristics] to query for the sensor orientation.
         * @param surfaceRotation the current device orientation as a Surface constant
         * @return the relative rotation from the camera sensor to the current device orientation.
         */
        private fun computeRelativeRotation(
            characteristics: CameraCharacteristics,
            surfaceRotation: Int
        ): Int {
            val sensorOrientationDegrees: Int =
                characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!

            var deviceOrientationDegrees = 0
            when (surfaceRotation) {
                Surface.ROTATION_90 -> deviceOrientationDegrees = 90
                Surface.ROTATION_180 -> deviceOrientationDegrees = 180
                Surface.ROTATION_270 -> deviceOrientationDegrees = 270
            }

            // Reverse device orientation for front-facing cameras
            val sign = if (
                characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
            ) 1 else -1

            // Calculate desired JPEG orientation relative to camera orientation to make
            // the image upright relative to the device orientation
            return (sensorOrientationDegrees - (deviceOrientationDegrees * sign) + 360) % 360
        }
    }
}
