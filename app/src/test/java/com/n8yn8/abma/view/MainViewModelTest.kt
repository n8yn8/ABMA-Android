package com.n8yn8.abma.view

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.backendless.BSponsor
import com.n8yn8.abma.model.backendless.BYear
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.Year
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
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MainViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var application: Application

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var yearObserver: Observer<Year>

    @Mock
    lateinit var loadingObserver: Observer<Boolean>

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

        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        `when`(context.getSharedPreferences(anyString(), anyInt()).edit()).thenReturn(sharedPrefsEditor)
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
        mainViewModel.isLoading.observeForever(loadingObserver)

        verify(yearObserver).onChanged(expectedYear)
        verify(loadingObserver, never()).onChanged(ArgumentMatchers.anyBoolean())

        mainViewModel.selectYear(expectedSecondYear.name.toString())
        verify(yearObserver).onChanged(expectedSecondYear)
        verify(remote, never()).getYears(any(), any())
        verify(remote, never()).getEvents(any(), any())
        verify(remote, never()).getSponsors(any(), any())
    }

    @Test
    fun testStartEmpty_noResponse() {
        mainViewModel = MainViewModel(application)
        verify(remote).getYears(any(), any())
        verify(remote, never()).getEvents(any(), any())
        verify(remote, never()).getSponsors(any(), any())

        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.isLoading.observeForever(loadingObserver)

        verify(yearObserver, never()).onChanged(any())
        verify(loadingObserver).onChanged(true)
    }

    @Test
    fun testStartEmpty_withResponse() {
        val years = listOf(FakeData.getBYear(), FakeData.getBYear(2019))

        doAnswer {
            //TODO: figure out how to test
//            verify(loadingObserver).onChanged(true)
            val callback = it.arguments[1] as DbManager.Callback<List<BYear>>
            callback.onDone(years, null)
            null
        }.`when`(remote).getYears(any(), any())

        mainViewModel = MainViewModel(application)
        verify(remote).getYears(any(), any())
        for (bYear in years) {
            verify(remote).getEvents(ArgumentMatchers.eq(bYear.objectId), any())
            verify(remote).getSponsors(ArgumentMatchers.eq(bYear.objectId), any())
        }
        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.isLoading.observeForever(loadingObserver)

        verify(yearObserver).onChanged(FakeData.getYear(id = 1))
        verify(loadingObserver).onChanged(false)
    }

    @Test
    fun testStartWithData_noUpdate() {
        `when`(sharedPreferences.getBoolean(anyString(), anyBoolean())).thenReturn(false)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
        }

        mainViewModel = MainViewModel(application)
        verify(remote, never()).getYears(any(), any())
        verify(remote, never()).getEvents(any(), any())
        verify(remote, never()).getSponsors(any(), any())

        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.isLoading.observeForever(loadingObserver)

        verify(yearObserver).onChanged(FakeData.getYear())
        verify(loadingObserver, never()).onChanged(ArgumentMatchers.anyBoolean())

    }

    @Test
    fun testStartWithData_pushUpdate() {
        `when`(sharedPreferences.getBoolean(anyString(), anyBoolean())).thenReturn(true)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
        }

        val remoteYear = FakeData.getBYear()
        doAnswer {

            val callback = it.arguments[1] as DbManager.Callback<List<BYear>>
            callback.onDone(listOf(remoteYear), null)
            null
        }.`when`(remote).getYears(any(), any())

        mainViewModel = MainViewModel(application)
        verify(remote).getYears(any(), any())
        verify(remote).getEvents(ArgumentMatchers.eq(remoteYear.objectId), any())
        verify(remote).getSponsors(ArgumentMatchers.eq(remoteYear.objectId), any())
        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.isLoading.observeForever(loadingObserver)

        val expected = FakeData.getYear()
        expected.id += 1 //year is replaced and id is incremented by db.
        verify(yearObserver).onChanged(expected)

        //TODO: verify true state hit
        verify(loadingObserver).onChanged(false)
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
        mainViewModel.isLoading.observeForever(loadingObserver)

        verify(yearObserver).onChanged(expectedYear)

        mainViewModel.selectYear(expectedSecondYear.name.toString())
        verify(yearObserver).onChanged(expectedSecondYear)

        verify(remote, never()).getYears(any(), any())
        verify(remote, never()).getEvents(any(), any())
        verify(remote, never()).getSponsors(any(), any())
        verify(loadingObserver, never()).onChanged(ArgumentMatchers.anyBoolean())
    }

    @Test
    fun testSelectYear_noSaveYears() {
        mainViewModel = MainViewModel(application)
        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.selectYear()

        verify(yearObserver, never()).onChanged(any())
    }

}