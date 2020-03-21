package com.n8yn8.abma

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.n8yn8.abma.model.AppDatabase
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
abstract class BaseTest: KoinTest {

    protected val database: AppDatabase by inject()

    @Before
    open fun setUp() {
        StandAloneContext.startKoin(
                listOf(
                        module {
                            single {
                                Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
                                        .allowMainThreadQueries()
                                        .build()
                            }
                        }
                )
        )
    }

    @After
    open fun tearDown() {
        StandAloneContext.stopKoin()
    }
}