package se.miun.holi1900.dt031g.bathingsites.db;

import static se.miun.holi1900.dt031g.bathingsites.utils.Helper.BATHING_SITE_TABLE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BathingSiteDao {

    @Query("SELECT * FROM " + BATHING_SITE_TABLE + " ORDER BY site_name ASC")
    LiveData<List<BathingSite>> getAllBathingSites();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     void insertBathingSite(BathingSite... bathingSite);

    @Query("SELECT * FROM " + BATHING_SITE_TABLE + " WHERE latitude = :lat AND longitude = :lon LIMIT 1")
    BathingSite findByCoordinates(final double lat, final double lon);

    @Query("SELECT COUNT(*) FROM " + BATHING_SITE_TABLE)
    LiveData<Integer> getRowCount();

    @Query("SELECT * FROM " + BATHING_SITE_TABLE + " WHERE site_name LIKE :bathingSiteName")
    BathingSite getBathingSiteByName(String bathingSiteName);
    @Delete
    int deleteBathingSite(BathingSite... bathingSites);


}
