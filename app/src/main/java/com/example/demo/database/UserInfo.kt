package com.example.demo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UserInfo {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var username: String? = null
    var password: String? = null
    var nickname: String? = null
    var phone: String? = null
    var email: String? = null

    override fun toString(): String {
        return "UserInfo(id=$id, username=$username, password=$password, nickname=$nickname, phone=$phone, email=$email)"
    }
}
