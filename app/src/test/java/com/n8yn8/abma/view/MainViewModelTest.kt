package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.Observer
import androidx.room.Room
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.Year
import com.n8yn8.test.util.FakeData
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MainViewModelTest : KoinTest {

    @Mock
    lateinit var application: Application
    @Mock
    lateinit var yearObserver: Observer<Year>

    private val database: AppDatabase by inject()
    private lateinit var mainViewModel: MainViewModel

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
                        }
                )
        )
        mainViewModel = MainViewModel(application)
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun selectLatestYear() {
        val expectedYear = FakeData.getYear()
        runBlocking {
            database.yearDao().insert(expectedYear)
            database.yearDao().insert(FakeData.getYear(2019))
            database.yearDao().insert(FakeData.getYear(2018))
        }

        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.selectLatestYear()

        Mockito.verify(yearObserver).onChanged(expectedYear)
    }
}