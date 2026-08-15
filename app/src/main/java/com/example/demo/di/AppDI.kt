package com.example.demo.di

import com.example.demo.UIApp
import com.example.demo.database.MyDatabase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CommonModule::class])
interface AppDI {
    fun app(): UIApp

    fun database(): MyDatabase
}