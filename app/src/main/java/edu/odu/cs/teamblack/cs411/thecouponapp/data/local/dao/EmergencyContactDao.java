package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;

@Dao
public interface EmergencyContactDao {

    @Insert
    void insertEmergencyContact(EmergencyContact emergencyContact);

    @Update
    void updateEmergencyContact(EmergencyContact emergencyContact);

    @Delete
    void deleteEmergencyContact(EmergencyContact emergencyContact);

    @Query("SELECT * FROM emergency_contacts")
    EmergencyContact[] getAllEmergencyContacts();

    @Query("SELECT * FROM emergency_contacts WHERE id = :uuid")
    EmergencyContact getEmergencyContactByUuid(String uuid);

    // Additional queries as needed
}
