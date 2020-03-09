package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.Year
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MainViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val db: AppDatabase by inject()

    private val _year = MutableLiveData<Year>()
    val year: LiveData<Year>
        get() = _year

    fun selectYear(name: String? = null) {
        val latestYear = if (name == null) {
            db.yearDao().lastYear
        } else {
            db.yearDao().getYearByName(name)
        }
        _year.postValue(latestYear)
    }
}