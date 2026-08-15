package com.example.demo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trackLog: TrackLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trackLogs: List<TrackLog>)

    @Delete
    suspend fun delete(trackLog: TrackLog)

    @Query("select * from TrackLog")
    suspend fun queryAll(): List<TrackLog>
}
