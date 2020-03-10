package com.n8yn8.abma.view

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.Observer
import androidx.room.Room
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.backendless.BYear
import com.n8yn8.abma.model.backendless.DbManager
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
    lateinit var context: Context
    @Mock
    lateinit var yearObserver: Observer<Year>
    @Mock
    lateinit var remote: DbManager
    @Mock
    lateinit var sharedPreferences: SharedPreferences
    @Mock
    lateinit var sharedPrefsEditor: SharedPreferences.Editor

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
                            single { remote }
                            single { sharedPreferences }
                        }
                )
        )

        Mockito.`when`(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences)
        Mockito.`when`(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt()).edit()).thenReturn(sharedPrefsEditor)
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun selectLatestYear() {
        val expectedYear = FakeData.getYear()
        val expectedSecondYear = FakeData.getYear(2019)
        runBlocking {
            database.yearDao().insert(expectedYear)
            database.yearDao().insert(expectedSecondYear)
            database.yearDao().insert(FakeData.getYear(2018))
        }
        mainViewModel = MainViewModel(application)
        mainViewModel.year.observeForever(yearObserver)

        Mockito.verify(yearObserver).onChanged(expectedYear)

        mainViewModel.selectYear(expectedSecondYear.name.toString())
        Mockito.verify(yearObserver).onChanged(expectedSecondYear)
    }

    @Test
    fun testStartEmpty_noResponse() {
        mainViewModel = MainViewModel(application)
        Mockito.verify(remote).getYears(Mockito.any(), Mockito.any())
        mainViewModel.year.observeForever(yearObserver)
        Mockito.verify(yearObserver, Mockito.never()).onChanged(Mockito.any())
    }

    @Test
    fun testStartEmpty_withResponse() {
        val years = listOf(FakeData.getBYear(), FakeData.getBYear(2019))

        Mockito.doAnswer {

            val callback = it.arguments[1] as DbManager.Callback<List<BYear>>
            callback.onDone(years, null)
            null
        }.`when`(remote).getYears(Mockito.any(), Mockito.any())

        mainViewModel = MainViewModel(application)
        Mockito.verify(remote).getYears(Mockito.any(), Mockito.any())
        mainViewModel.year.observeForever(yearObserver)
        Mockito.verify(yearObserver).onChanged(FakeData.getYear(id = 1))
    }

    @Test
    fun testStartWithData_noUpdate() {
        Mockito.`when`(sharedPreferences.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
        }

        mainViewModel = MainViewModel(application)
        Mockito.verify(remote, Mockito.never()).getYears(Mockito.any(), Mockito.any())

        mainViewModel.year.observeForever(yearObserver)
        Mockito.verify(yearObserver).onChanged(FakeData.getYear())

    }

    @Test
    fun testStartWithData_pushUpdate() {
        Mockito.`when`(sharedPreferences.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(true)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
        }

        val remoteYear = FakeData.getBYear()
        Mockito.doAnswer {

            val callback = it.arguments[1] as DbManager.Callback<List<BYear>>
            callback.onDone(listOf(remoteYear), null)
            null
        }.`when`(remote).getYears(Mockito.any(), Mockito.any())

        mainViewModel = MainViewModel(application)
        Mockito.verify(remote).getYears(Mockito.any(), Mockito.any())
        mainViewModel.year.observeForever(yearObserver)

        val expected = FakeData.getYear()
        expected.id += 1 //year is replaced and id is incremented by db.
        Mockito.verify(yearObserver).onChanged(expected)
    }

    @Test
    fun testSelectYear() {
        val expectedYear = FakeData.getYear()
        val expectedSecondYear = FakeData.getYear(2019)
        runBlocking {
            database.yearDao().insert(expectedYear)
            database.yearDao().insert(expectedSecondYear)
            database.yearDao().insert(FakeData.getYear(2018))
        }
        mainViewModel = MainViewModel(application)
        mainViewModel.year.observeForever(yearObserver)

        Mockito.verify(yearObserver).onChanged(expectedYear)

        mainViewModel.selectYear(expectedSecondYear.name.toString())
        Mockito.verify(yearObserver).onChanged(expectedSecondYear)

        Mockito.verify(remote, Mockito.never()).getYears(Mockito.any(), Mockito.any())
    }

}