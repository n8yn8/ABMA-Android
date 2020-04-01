package com.n8yn8.abma.view

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.n8yn8.abma.Utils
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.ConvertUtil
import com.n8yn8.abma.model.MyDateTypeAdapter
import com.n8yn8.abma.model.backendless.*
import com.n8yn8.abma.model.entities.Year
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val db: AppDatabase by inject()
    private val remote: DbManager by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private val _year = MutableLiveData<Year>()
    val year: LiveData<Year>
        get() = _year

    private val _yearNames = MutableLiveData<List<String>>()
    val yearNames: LiveData<List<String>>
        get() = _yearNames

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        viewModelScope.launch {
            val savedYears = db.yearDao().years()
            if (savedYears.isEmpty()) {
                loadBackendless()
            } else {
                if (sharedPreferences.getBoolean("PushReceived", false)) {
                    loadBackendless()
                } else {
                    selectYear()
                }
            }
        }
    }

    fun selectYear(name: String? = null) {
        viewModelScope.launch {
            val latestYear = if (name == null) {
                db.yearDao().lastYear()
            } else {
                db.yearDao().getYearByName(name)
            }
            if (latestYear != null) {
                _year.postValue(latestYear)
            }
        }
    }

    fun loadBackendless() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("PushReceived", false)
        editor.apply()

        _isLoading.postValue(true)
        remote.getYears(getApplication()) { years, error ->
            if (error != null) {
                Toast.makeText(getApplication(), "Error: $error", Toast.LENGTH_LONG).show()
            }
            viewModelScope.launch {
                for (bYear in years) {
                    db.yearDao().insert(ConvertUtil.convert(bYear))
                    Utils.saveSurveys(db, bYear)
                    saveMaps(bYear)
                    remote.getEvents(bYear.objectId) { bEvents, _ ->
                        viewModelScope.launch {
                            saveEvents(bYear.objectId, bEvents)
                        }
                    }
                    remote.getSponsors(bYear.objectId, DbManager.Callback<List<BSponsor>> { remoteList, _ ->
                        if (remoteList == null) return@Callback
                        viewModelScope.launch { db.sponsorDao().insert(ConvertUtil.convertSponsors(remoteList, bYear.objectId)) }
                    })
                }
                selectYear()
            }

            _isLoading.postValue(false)
        }
    }

    private suspend fun saveMaps(year: BYear) {
        val mapsString = year.maps
        val gson = GsonBuilder()
                .registerTypeAdapter(Date::class.java, MyDateTypeAdapter())
                .create()
        val maps = gson.fromJson<List<BMap>>(mapsString, object : TypeToken<List<BMap?>?>() {}.type)
        db.mapDao().insert(ConvertUtil.convertMaps(maps, year.objectId))
    }

    private suspend fun saveEvents(yearId: String, bEvents: List<BEvent>) {
        val localEvents = db.eventDao().getEvents(yearId)
        for (localEvent in localEvents) {
            var found = false
            for (remoteEvent in bEvents) {
                if (remoteEvent.objectId == localEvent.objectId) {
                    found = true
                    break
                }
            }
            if (!found) {
                db.eventDao().delete(localEvent)
            }
        }

        db.eventDao().insert(ConvertUtil.convertEvents(bEvents, yearId))
        for (event in bEvents) {
            if (event.papersCount != 0) {
                remote.getPapers(event.objectId) { bPapers, _ ->
                    viewModelScope.launch { db.paperDao().insert(ConvertUtil.convertPapers(bPapers, event.objectId)) }
                }
            }
        }
    }

    fun requestYearNames() {
        viewModelScope.launch {
            val namesInt = db.yearDao().allYearNames()
            val names = namesInt.map {
                it.toString()
            }
            _yearNames.postValue(names)
        }
    }
}