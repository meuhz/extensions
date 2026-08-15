package com.example.demo.di

import com.example.demo.UIApp
import com.example.demo.database.MyDatabase
import dagger.Module
import dagger.Provides

@Module
object CommonModule {
    @Provides
    fun application(): UIApp {
        return UIApp.INSTANCE
    }

    @Provides
    fun database(): MyDatabase {
        return UIApp.database
    }
}
