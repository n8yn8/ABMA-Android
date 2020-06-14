package com.n8yn8.abma.view

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.backendless.*
import com.n8yn8.abma.model.entities.Year
import com.n8yn8.test.util.FakeData
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val sharedPreferences = mockk<SharedPreferences> {
        every { edit() } returns mockk {
            every { putBoolean(any(), any()) } returns mockk()
            every { apply() } just Runs
        }
    }
    private val yearObserver = spyk<Observer<Year>>()
    private val loadingObserver = spyk<Observer<Boolean>>()
    private val remote = mockk<DbManager>()

    private val database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        every {
            remote.getSponsors(any(), any())
        } answers {
            secondArg<DbManager.Callback<List<BSponsor>>>().onDone(FakeData.getBSponsors(), null)
        }
    }

    private fun getMainViewModel() = MainViewModel(
            ApplicationProvider.getApplicationContext(),
            database,
            remote,
            sharedPreferences
    )

    private fun setupRemoteResponse(years: List<BYear> = listOf(), events: List<BEvent> = listOf()) {
        every { remote.getYears(any(), any()) } answers {
            secondArg<DbManager.Callback<List<BYear>>>().onDone(years, null)
        }
        every { remote.getEvents(any(), any()) } answers { secondArg<DbManager.Callback<List<BEvent>>>().onDone(events, null) }
        every { remote.getPapers(any(), any()) } answers { secondArg<DbManager.Callback<List<BPaper>>>().onDone(listOf(), null) }
    }

    private fun setPushDriven(isPush: Boolean) {
        every { sharedPreferences.getBoolean(any(), any()) } returns isPush
    }

    @Test
    fun selectLatestYear() {
        setPushDriven(false)
        val expectedYear = FakeData.getYear()
        val expectedSecondYear = FakeData.getYear(2019)
        runBlocking {
            database.yearDao().insert(expectedYear)
            database.yearDao().insert(expectedSecondYear)
            database.yearDao().insert(FakeData.getYear(2018))
        }
        mainViewModel = getMainViewModel()
        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.isLoading.observeForever(loadingObserver)

        verify { yearObserver.onChanged(expectedYear) }
        verify(exactly = 0) { loadingObserver.onChanged(any()) }

        mainViewModel.selectYear(expectedSecondYear.name.toString())
        verify { yearObserver.onChanged(expectedSecondYear) }
        verify(exactly = 0) { remote.getYears(any(), any()) }
        verify(exactly = 0) { remote.getSponsors(any(), any()) }
        verify(exactly = 0) { remote.getEvents(any(), any()) }
        verify(exactly = 0) { remote.getPapers(any(), any()) }
    }

    @Test
    fun testStartEmpty_noResponse() {
        setupRemoteResponse()
        mainViewModel = getMainViewModel()
        verify { remote.getYears(any(), any()) }
        verify(exactly = 0) { remote.getSponsors(any(), any()) }
        verify(exactly = 0) { remote.getEvents(any(), any()) }
        verify(exactly = 0) { remote.getPapers(any(), any()) }

        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.isLoading.observeForever(loadingObserver)

        verify(exactly = 0) { yearObserver.onChanged(any()) }
        verify { loadingObserver.onChanged(false) }
    }

    @Test
    fun testStartEmpty_withResponse() {
        val years = listOf(FakeData.getBYear(), FakeData.getBYear(2019))
        val events = years.map {
            FakeData.getBEvent(it.name)
        }

        every { remote.getYears(any(), any()) } answers { secondArg<DbManager.Callback<List<BYear>>>().onDone(years, null) }

        every { remote.getEvents(any(), any()) } answers {
            val event = if (firstArg<String>() == "year2019") {
                events[0]
            } else {
                events[1]
            }
            secondArg<DbManager.Callback<List<BEvent>>>().onDone(listOf(event), null)
        }

        mainViewModel = getMainViewModel()
        verify { remote.getYears(any(), any()) }
        for (bYear in years) {
            verify { remote.getSponsors(eq(bYear.objectId), any()) }
            verify { remote.getEvents(eq(bYear.objectId), any()) }
        }
        for (bEvent in events) {
            verify { remote.getPapers(eq(bEvent.objectId), any()) }
        }
        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.isLoading.observeForever(loadingObserver)

        verify { yearObserver.onChanged(FakeData.getYear(id = 1)) }
        verify { loadingObserver.onChanged(false) }
    }

    @Test
    fun testStartWithData_noUpdate() {
        setPushDriven(false)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
        }

        mainViewModel = getMainViewModel()
        verify(exactly = 0) { remote.getYears(any(), any()) }
        verify(exactly = 0) { remote.getSponsors(any(), any()) }
        verify(exactly = 0) { remote.getEvents(any(), any()) }
        verify(exactly = 0) { remote.getPapers(any(), any()) }

        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.isLoading.observeForever(loadingObserver)

        verify { yearObserver.onChanged(FakeData.getYear()) }
        verify(exactly = 0) { loadingObserver.onChanged(any()) }

    }

    @Test
    fun testStartWithData_pushUpdate() {
        setPushDriven(true)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
        }

        val remoteYear = FakeData.getBYear()
        val remoteEvent = FakeData.getBEvent(remoteYear.name)
        setupRemoteResponse(listOf(remoteYear), listOf(remoteEvent))

        mainViewModel = getMainViewModel()
        verify { remote.getYears(any(), any()) }
        verify { remote.getEvents(remoteYear.objectId, any()) }
        verify { remote.getSponsors(remoteYear.objectId, any()) }
        verify { remote.getPapers(remoteEvent.objectId, any()) }
        mainViewModel.year.observeForever(yearObserver)
        mainViewModel.isLoading.observeForever(loadingObserver)

        val expected = FakeData.getYear()
        expected.id += 1 //year is replaced and id is incremented by db.
        verify { yearObserver.onChanged(expected) }

        //TODO: verify true state hit
        verify { loadingObserver.onChanged(false) }
    }

//    @Test
//    fun testSelectYear() {
//        val expectedYear = FakeData.getYear()
//        val expectedSecondYear = FakeData.getYear(2019)
//        runBlocking {
//            database.yearDao().insert(expectedYear)
//            database.yearDao().insert(expectedSecondYear)
//            database.yearDao().insert(FakeData.getYear(2018))
//        }
//        mainViewModel = MainViewModel(application)
//        mainViewModel.year.observeForever(yearObserver)
//        mainViewModel.isLoading.observeForever(loadingObserver)
//
//        verify(yearObserver).onChanged(expectedYear)
//
//        mainViewModel.selectYear(expectedSecondYear.name.toString())
//        verify(yearObserver).onChanged(expectedSecondYear)
//
//        verify(remote, never()).getYears(any(), any())
//        verify(remote, never()).getEvents(any(), any())
//        verify(remote, never()).getSponsors(any(), any())
//        verify(remote, never()).getPapers(any(), any())
//        verify(loadingObserver, never()).onChanged(anyBoolean())
//    }
//
//    @Test
//    fun testSelectYear_noSaveYears() {
//        mainViewModel = MainViewModel(application)
//        mainViewModel.year.observeForever(yearObserver)
//        mainViewModel.selectYear()
//
//        verify(yearObserver, never()).onChanged(any())
//    }

}