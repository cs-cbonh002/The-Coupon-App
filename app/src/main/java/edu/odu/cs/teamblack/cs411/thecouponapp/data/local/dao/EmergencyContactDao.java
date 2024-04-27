package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;

@Dao
public interface EmergencyContactDao {

    /**
     * Inserts a new emergency contact into the database.
     * @param emergencyContact The emergency contact to insert.
     * @return The row ID of the inserted contact.
     */
    @Insert
    long insert(EmergencyContact emergencyContact);

    /**
     * Updates an existing emergency contact in the database.
     * @param emergencyContact The emergency contact to update.
     */
    @Update
    void update(EmergencyContact emergencyContact);

    /**
     * Deletes an emergency contact from the database.
     * @param emergencyContact The emergency contact to delete.
     */
    @Delete
    void delete(EmergencyContact emergencyContact);

    /**
     * Retrieves all emergency contacts.
     * @return A LiveData list of all emergency contacts.
     */
    @Query("SELECT * FROM emergency_contacts ORDER BY isPrimary DESC, id ASC")
    LiveData<List<EmergencyContact>> getAllEmergencyContacts();

    /**
     * Retrieves the currently set primary contact.
     * @return LiveData containing the primary emergency contact.
     */
    @Query("SELECT * FROM emergency_contacts WHERE isPrimary = 1 LIMIT 1")
    LiveData<EmergencyContact> getPrimaryContact();

    /**
     * Sets a unique primary contact, ensuring only one primary contact exists.
     * @param newPrimaryId The ID of the contact to set as primary.
     */
    @Transaction
    default void setUniquePrimaryContact(long newPrimaryId) {
        unsetAllPrimaryContacts(); // Ensure no other contacts are primary
        setContactPrimary(newPrimaryId); // Set the new primary contact
    }

    /**
     * Unsets all contacts from being primary.
     */
    @Query("UPDATE emergency_contacts SET isPrimary = 0")
    void unsetAllPrimaryContacts();

    /**
     * Sets a specific contact as the primary.
     * @param contactId The ID of the contact to set as primary.
     */
    @Query("UPDATE emergency_contacts SET isPrimary = 1 WHERE id = :contactId")
    void setContactPrimary(long contactId);

}

