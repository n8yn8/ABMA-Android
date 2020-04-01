package com.n8yn8.abma.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.n8yn8.abma.model.entities.Sponsor

@Dao
interface SponsorDao {
    @Query("SELECT * FROM sponsors WHERE year_id = :yearId")
    fun getSponsors(yearId: String): LiveData<List<Sponsor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg sponsor: Sponsor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sponsors: List<Sponsor>)
}