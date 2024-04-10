package edu.odu.cs.teamblack.cs411.thecouponapp.data.local;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.converters.DateConverter;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao.EmergencyContactDao;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao.IncidentLogDao;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao.LocalResourcesDao;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao.SettingsDao;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao.UserDao;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.User;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.LocalResource;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.Settings;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import android.content.Context;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

@Database(entities = {
        User.class,
        IncidentLog.class,
        Settings.class,
        EmergencyContact.class,
        LocalResource.class
}, version = 1, exportSchema = false)

@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract IncidentLogDao incidentLogDao();

    public abstract EmergencyContactDao emergencyContactDao();

    public abstract LocalResourcesDao localResourcesDao();

    public abstract SettingsDao settingsDao();

    public abstract UserDao userDao();

    // Define a single ExecutorService with a fixed number of threads to run database operations asynchronously on a background thread.
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Instance of the database
    private static volatile AppDatabase INSTANCE;

    // Method to get the database instance
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            // Add callback to populate the database
                            // .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
