package com.n8yn8.abma.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.n8yn8.abma.model.entities.Sponsor;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

@Dao
public interface SponsorDao {

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_SPONSORS
            + " WHERE " + DatabaseHandler.KEY_YEAR_ID + "=:sponsorId")
    List<Sponsor> getSponsors(final int sponsorId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Sponsor sponsor);
}
