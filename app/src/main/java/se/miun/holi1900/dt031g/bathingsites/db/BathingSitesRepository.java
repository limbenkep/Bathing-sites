package se.miun.holi1900.dt031g.bathingsites.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class BathingSitesRepository {
    private static final String TAG = "DatabaseInterface";
    private final BathingSiteDao bathingSiteDao;

    public BathingSitesRepository(Context context) {
        AppDatabase db = AppDatabase.getDbAInstance(context);
        bathingSiteDao = db.bathingSiteDao();
    }

    /**
     * Gets all bathing sites in stored in the database
     * @return list of all bathing sites
     */
    public LiveData<List<BathingSite>> getAllBathingSites() {
        return bathingSiteDao.getAllBathingSites();
    }

    /**
     * Gets a bathing site by name
     * @param name name of bathing sites to get
     * @return bathing if found else null
     */
    public BathingSite getBathingSite(String name) {
        return bathingSiteDao.getBathingSiteByName(name);
    }

    /**
     * Deletes bathing sites from database
     * @param bathingSites bathing sites to delete
     * @return number of bathing sites deleted
     */
    public int deleteBathingSite(BathingSite... bathingSites) {
        return bathingSiteDao.deleteBathingSite(bathingSites);
    }

    /**
     *
     * @return number of bathing sites that are in the database
     */
    public LiveData<Integer> numberOfBathingSites() {
        return bathingSiteDao.getRowCount();
    }

    /**
     * Saves the passed BathingSite to database
     * @param bathingSite bathing site to be saved
     */
    public void insertNewBathingSite(BathingSite bathingSite) {
        try {
            InsertBathingSiteAsyncTask insert = new InsertBathingSiteAsyncTask(bathingSiteDao);
            insert.execute(bathingSite);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Checks if a bathing site with same longitude and latitude as the
     * passed bathing site is found in the database
     *
     * @param newBathingSite the new site to be added to the database
     * @return true if bathing site with same latitude and longitude is found in the database
     */
    public boolean BathingSiteFound(BathingSite newBathingSite) {
        BathingSite bathingSite = bathingSiteDao.findByCoordinates(newBathingSite.latitude, newBathingSite.longitude);
       return bathingSite !=null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        AppDatabase.destroyInstance();
    }

    /**
     * Async task to add bathing site to database
     */
    private static class InsertBathingSiteAsyncTask extends AsyncTask<BathingSite, Integer, Long> {
        BathingSiteDao bathingSiteDao;

        public InsertBathingSiteAsyncTask(BathingSiteDao bDao) {
            bathingSiteDao = bDao;
        }

        @Override
        protected Long doInBackground(final BathingSite... bathingSites) {
            bathingSiteDao.insertBathingSite(bathingSites[0]);
            return null;
        }
    }

    /**
     * Async Task to delete bathing sites from database.
     */
    private static class DeleteBathingSiteAsyncTask extends AsyncTask<BathingSite, Integer, Integer> {
        BathingSiteDao bathingSiteDao;

        public DeleteBathingSiteAsyncTask(BathingSiteDao bDao) {
            bathingSiteDao = bDao;
        }

        @Override
        protected Integer doInBackground(final BathingSite... bathingSites) {
            return bathingSiteDao.deleteBathingSite(bathingSites);
        }
    }
}
