package com.example.demo.database

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TrackLog {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    var tag: String
    var message: String
    var timestamp: Long
    var priority: Int

    constructor(priority: Int, tag: String, message: String) {
        this.priority = priority
        this.tag = tag
        this.message = message
        this.timestamp = System.currentTimeMillis()
    }

    companion object {
        fun log(priority: Int, tag: String, message: String): TrackLog {
            return TrackLog(priority, tag, message)
        }

        fun debug(tag: String, message: String): TrackLog {
            return TrackLog(Log.DEBUG, tag, message)
        }

        fun error(tag: String, message: String): TrackLog {
            return TrackLog(Log.ERROR, tag, message)
        }
    }

    override fun toString(): String {
        return "TrackLog(id=$id, priority=$priority, tag='$tag', message='$message', timestamp=$timestamp)"
    }
}
