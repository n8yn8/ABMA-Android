package com.n8yn8.abma.view

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.Sponsor

class SponsorViewModel @ViewModelInject constructor(
        private val db: AppDatabase
) : ViewModel() {
    val sponsors: LiveData<List<Sponsor>>

    init {
        sponsors = Transformations.switchMap(db.yearDao().lastYearLive) { year ->
            db.sponsorDao().getSponsors(year.objectId)
        }
    }
}