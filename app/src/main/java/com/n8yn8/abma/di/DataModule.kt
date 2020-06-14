package com.n8yn8.abma.di

import android.content.Context
import android.content.SharedPreferences
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.backendless.DbManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDbManager(): DbManager {
        return DbManager.getInstance()
    }

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }
}