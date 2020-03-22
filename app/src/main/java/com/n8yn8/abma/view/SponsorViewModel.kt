package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.ConvertUtil
import com.n8yn8.abma.model.backendless.BSponsor
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.Sponsor
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SponsorViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val db: AppDatabase by inject()
    private val remote: DbManager by inject()
    val sponsors: LiveData<List<Sponsor>>

    init {
        val lastYear = db.yearDao().lastYear
        sponsors = Transformations.map(db.sponsorDao().getSponsors(lastYear.objectId)) {
            if (it.isNullOrEmpty()) {
                remote.getSponsors(lastYear.objectId, object : DbManager.Callback<List<BSponsor>> {
                    override fun onDone(remoteList: List<BSponsor>?, error: String?) {
                        if (remoteList == null) return
                        db.sponsorDao().insert(ConvertUtil.convertSponsors(remoteList, lastYear.objectId))
                    }
                })
            }
            it
        }

    }
}