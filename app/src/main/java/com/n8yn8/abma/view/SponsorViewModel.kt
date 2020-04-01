package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.Sponsor
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SponsorViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val db: AppDatabase by inject()
    val sponsors: LiveData<List<Sponsor>>

    init {
        sponsors = Transformations.switchMap(db.yearDao().lastYearLive) { year ->
            db.sponsorDao().getSponsors(year.objectId)
        }
    }
}