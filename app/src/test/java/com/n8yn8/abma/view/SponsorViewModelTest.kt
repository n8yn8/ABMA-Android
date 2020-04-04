package com.n8yn8.abma.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.Sponsor
import com.n8yn8.test.util.FakeData
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SponsorViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val application: Application = ApplicationProvider.getApplicationContext()
    private val sponsorModelObserver = spyk<Observer<List<Sponsor>>>()

    private val database: AppDatabase by inject()

    @Before
    fun setUp() {
        StandAloneContext.startKoin(
                listOf(
                        module {
                            single {
                                Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java)
                                        .allowMainThreadQueries()
                                        .build()
                            }
                        }
                )
        )

        runBlocking {
            database.yearDao().insert(FakeData.getYear())
        }
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun responseDataExists() {
        runBlocking {
            database.sponsorDao().insert(FakeData.getSponsor())
        }
        val sponsorViewModel = SponsorViewModel(application)
        sponsorViewModel.sponsors.observeForever(sponsorModelObserver)

        verify {
            sponsorModelObserver.onChanged(any())
            sponsorModelObserver.onChanged(listOf(FakeData.getSponsor()))
        }
    }

    @Test
    fun responseDataEmpty() {
        val sponsorViewModel = SponsorViewModel(application)
        sponsorViewModel.sponsors.observeForever(sponsorModelObserver)
        verify { sponsorModelObserver.onChanged(emptyList()) }
    }
}