package com.example.demo.activity.camera

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager.AudioRecordingCallback
import android.media.AudioRecord
import android.media.AudioRecordingConfiguration
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import com.example.demo.activity.BaseActivity
import com.example.demo.databinding.ActivityAudioBinding
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Executors

class AudioActivity : BaseActivity() {
    private lateinit var binding: ActivityAudioBinding
    private var audioRecord: AudioRecord? = null
    private var bufferSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.play.setOnClickListener {
            if (audioRecord == null || audioRecord!!.recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                startRecord()
                binding.time.setBase(SystemClock.elapsedRealtime())
                binding.time.start()
            } else {
                stopRecord()
                binding.time.stop()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioRecord?.release()
    }

    private fun startRecord() {
        Timber.d("startRecord")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return
        }
        val sampleRateHz = 48000
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val channelConfig = AudioFormat.CHANNEL_IN_STEREO
        bufferSize = AudioRecord.getMinBufferSize(sampleRateHz, channelConfig, audioFormat)
        if (audioRecord == null) {
            audioRecord = AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(audioFormat)
                        .setSampleRate(sampleRateHz)
                        .setChannelMask(channelConfig)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize * 2)
                .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                audioRecord!!.registerAudioRecordingCallback(
                    Executors.newSingleThreadExecutor(),
                    object : AudioRecordingCallback() {
                        override fun onRecordingConfigChanged(configs: MutableList<AudioRecordingConfiguration>) {
                            super.onRecordingConfigChanged(configs)
                            Timber.i("AudioRecord onRecordingConfigChanged: %s", configs)
                        }
                    })
            }
        }

        val pcmFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "raw.pcm")
        if (pcmFile.exists()) {
            pcmFile.delete()
        }

        audioRecord!!.startRecording()
        Thread {
            val audioRecord = audioRecord ?: return@Thread
            try {
                FileOutputStream(pcmFile).use { stream ->
                    val buffer = ByteArray(bufferSize)
                    while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                        val readStatus = audioRecord.read(buffer, 0, bufferSize)
                        if (readStatus > 0) {
                            stream.write(buffer, 0, readStatus)
                        }
                    }
                }
            } catch (e: IOException) {
                Timber.e(e)
            }
        }.start()
    }

    private fun stopRecord() {
        Timber.d("stopRecord")
        val audioRecord = audioRecord ?: return
        if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.stop()
        }
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