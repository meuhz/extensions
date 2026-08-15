package com.example.demo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trackLog: TrackLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trackLogs: List<TrackLog>)

    @Delete
    fun delete(trackLog: TrackLog)

    @Query("select * from TrackLog")
    fun queryAll(): List<TrackLog>
}
