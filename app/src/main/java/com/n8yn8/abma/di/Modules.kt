package com.n8yn8.abma.di

import android.content.Context
import android.content.SharedPreferences
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.backendless.DbManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val applicationModule = module(override = true) {
    single {
        AppDatabase.getInstance(androidContext())
    }
    single<DbManager> {
        DbManager.getInstance()
    }
    single<SharedPreferences> {
        androidContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }
}