package com.n8yn8.abma.di

import com.n8yn8.abma.model.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val applicationModule = module(override = true) {
    single {
        AppDatabase.getInstance(androidContext())
    }
}