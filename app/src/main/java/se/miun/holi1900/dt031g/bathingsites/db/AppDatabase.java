package se.miun.holi1900.dt031g.bathingsites.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 2, entities = BathingSite.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BathingSiteDao bathingSiteDao();
    private static AppDatabase INSTANCE;
    public static AppDatabase getDbAInstance(Context context){
        if(INSTANCE==null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "bathing_sites")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    /**
     * Free resources
     */
    public static void destroyInstance(){INSTANCE = null;}
}
