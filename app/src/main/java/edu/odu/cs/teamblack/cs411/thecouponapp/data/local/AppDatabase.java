package edu.odu.cs.teamblack.cs411.thecouponapp.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

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

    // Additional DAOs as needed

}
