package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.Settings;

@Dao
public interface SettingsDao {

    @Insert
    void insertSetting(Settings setting);

    @Update
    void updateSetting(Settings setting);

    @Delete
    void deleteSetting(Settings setting);

    @Query("SELECT * FROM settings")
    Settings[] getAllSettings();

    @Query("SELECT * FROM settings WHERE id = :uuid")
    Settings getSettingByUuid(String uuid);

}
