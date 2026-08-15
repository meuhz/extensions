package com.example.demo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<UserInfo>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(user: UserInfo)

    @Delete
    suspend fun delete(user: UserInfo)

    @Query("DELETE FROM USERINFO WHERE id=:id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM USERINFO")
    suspend fun deleteAll()

    @Query("SELECT * FROM USERINFO WHERE id=:id")
    suspend fun getUser(id: Int): UserInfo?

    @Query("SELECT * FROM USERINFO")
    suspend fun getAllUser(): List<UserInfo>

    @Query("SELECT count() FROM USERINFO")
    suspend fun count(): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'USERINFO'")
    suspend fun resetPrimaryKey()
}
