package com.n8yn8.abma.view

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.n8yn8.abma.Utils
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.backendless.BYear
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.Year
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MainViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val db: AppDatabase by inject()
    private val remote: DbManager by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private val _year = MutableLiveData<Year>()
    val year: LiveData<Year>
        get() = _year

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        val savedYears = db.yearDao().years
        if (savedYears.size == 0) {
            loadBackendless(false)
        } else {
            if (sharedPreferences.getBoolean("PushReceived", false)) {
                loadBackendless(true)
            } else {
                selectYear()
            }
        }
    }

    fun selectYear(name: String? = null) {
        val latestYear = if (name == null) {
            db.yearDao().lastYear
        } else {
            db.yearDao().getYearByName(name)
        }
        _year.postValue(latestYear)
    }

    fun loadBackendless(isUpdate: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("PushReceived", false)
        editor.apply()

        _isLoading.postValue(true)
        remote.getYears(getApplication(), object : DbManager.Callback<List<BYear?>> {
            override fun onDone(years: List<BYear?>, error: String?) {
                if (error != null) {
                    Toast.makeText(getApplication(), "Error: $error", Toast.LENGTH_LONG).show()
                }
                Utils.saveYears(db, years)
                selectYear()
                _isLoading.postValue(false)
            }
        })
    }
}