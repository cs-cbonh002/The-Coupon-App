package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;

@Dao
public interface EmergencyContactDao {

    @Insert
    void insert(EmergencyContact emergencyContact);

    @Update
    void update(EmergencyContact emergencyContact);

    @Delete
    void delete(EmergencyContact emergencyContact);

    @Query("SELECT * FROM emergency_contacts")
    LiveData<List<EmergencyContact>> getAllEmergencyContacts();

    @Query("SELECT * FROM emergency_contacts WHERE id = :uuid")
    EmergencyContact getEmergencyContactByUuid(String uuid);
}
