package com.example.demo.util

import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.Arrays

/**
 * @see android.os.FileUtils
 */
object FileUtils {
    /**
     * 删除目录中的所有文件
     */
    fun deleteContents(dir: File): Boolean {
        val files = dir.listFiles() ?: return true
        var success = true
        for (file in files) {
            if (file.isDirectory()) {
                success = success and deleteContents(file)
            }
            if (!file.delete()) {
                success = false
            }
        }
        return success
    }

    fun digestMD5(file: File): ByteArray? {
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            DigestInputStream(FileInputStream(file), messageDigest).use { stream ->
                val buffer = ByteArray(1024 * 8)
                while (stream.read(buffer) != -1) {
                }
            }
            return messageDigest.digest()
        } catch (_: Exception) {
            return null
        }
    }

    /**
     * @param minCount Always keep at least this many files.
     * @param minAgeMs Always keep files younger than this age, in milliseconds.
     * @return if any files were deleted.
     */
    @JvmStatic
    fun deleteOlderFiles(dir: File, minCount: Int = 0, minAgeMs: Long = 0): Boolean {
        require(!(minCount < 0 || minAgeMs < 0)) { "Constraints must be positive or 0" }

        val files = dir.listFiles() ?: return false

        // Sort with newest files first
        Arrays.sort(files, Comparator { lhs: File, rhs: File ->
            (rhs.lastModified() - lhs.lastModified()).toInt()
        })

        // Keep at least minCount files
        var deleted = false
        for (i in minCount..<files.size) {
            val file = files[i]

            // Keep files newer than minAgeMs
            if (minAgeMs == 0L || System.currentTimeMillis() - file.lastModified() > minAgeMs) {
                if (file.delete()) {
                    deleted = true
                }
            }
        }
        return deleted
    }
}
