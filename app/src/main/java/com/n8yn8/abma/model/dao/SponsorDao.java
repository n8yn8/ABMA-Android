package com.n8yn8.abma.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.n8yn8.abma.model.entities.Sponsor;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

@Dao
public interface SponsorDao {

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_SPONSORS
            + " WHERE " + DatabaseHandler.KEY_YEAR_ID + "=:yearId")
    LiveData<List<Sponsor>> getSponsors(final String yearId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Sponsor... sponsor);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<Sponsor> sponsors);
}
