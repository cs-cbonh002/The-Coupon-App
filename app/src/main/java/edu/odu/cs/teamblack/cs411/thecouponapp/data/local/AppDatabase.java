package edu.odu.cs.teamblack.cs411.thecouponapp.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.converters.DateConverter;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao.IncidentLogDao;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao.UserProfileDao;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.UserProfile;

@Database(entities = {UserProfile.class, IncidentLog.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class}) // Assuming you have a DateConverter to handle the conversion of Date fields
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserProfileDao userProfileDao();
    public abstract IncidentLogDao incidentLogDao();
}
