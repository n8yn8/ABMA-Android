package com.n8yn8.abma.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.backendless.BSponsor
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.Sponsor
import com.n8yn8.test.util.FakeData
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
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SponsorViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var application: Application

    @Mock
    lateinit var remote: DbManager

    @Mock
    lateinit var sponsorModelObserver: Observer<List<Sponsor>>

    private val database: AppDatabase by inject()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        StandAloneContext.startKoin(
                listOf(
                        module {
                            single {
                                Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java)
                                        .allowMainThreadQueries()
                                        .build()
                            }
                            single { remote }
                        }
                )
        )

        runBlocking {
            database.yearDao().insert(FakeData.getYear())
        }

        doAnswer {
            val callback = it.arguments[1] as DbManager.Callback<List<BSponsor>>
            callback.onDone(FakeData.getBSponsors(), null)
            null
        }.`when`(remote).getSponsors(any(), any())
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

        verify(remote, never()).getSponsors(any(), any())
        verify(sponsorModelObserver).onChanged(anyList())
        verify(sponsorModelObserver).onChanged(listOf(FakeData.getSponsor()))
    }

    @Test
    fun responseDataEmpty() {
        val sponsorViewModel = SponsorViewModel(application)
        sponsorViewModel.sponsors.observeForever(sponsorModelObserver)

        verify(remote).getSponsors(any(), any())
        verify(sponsorModelObserver, times(2)).onChanged(anyList())

    }
}