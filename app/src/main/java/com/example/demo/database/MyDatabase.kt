package com.example.demo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(
    version = MyDatabase.DB_VERSION,
    exportSchema = false,
    entities = [UserInfo::class, TrackLog::class]
)
abstract class MyDatabase : RoomDatabase() {
    abstract fun userDao(): UserInfoDao

    abstract fun trackLogDao(): TrackLogDao

    companion object {
        const val DB_NAME: String = "test"
        const val DB_VERSION: Int = 6

        fun create(appContext: Context): MyDatabase {
            return databaseBuilder(appContext, MyDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }
}
