package edu.odu.cs.teamblack.cs411.thecouponapp.ui.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.repository.EmergencyContactRepository;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.repository.IncidentLogRepository;

public class EmergencyContactsViewModel extends AndroidViewModel {
    private LiveData<List<EmergencyContact>> allEmergencyContacts;
    private EmergencyContactRepository repository;

    public EmergencyContactsViewModel(Application application) {
        super(application);
        repository = new EmergencyContactRepository(application);
        allEmergencyContacts = repository.getAllEmergencyContacts();
    }

    public LiveData<List<EmergencyContact>> getAllEmergencyContacts() {
        return allEmergencyContacts;
    }

    public void insert(EmergencyContact emergencyContact) {
        repository.insert(emergencyContact);
    }

    public void update(EmergencyContact emergencyContact) {
        repository.update(emergencyContact);
        Log.d("EmergencyContactsViewModel", "Updated emergency contact: " + emergencyContact.toString());

    }

    public void delete(EmergencyContact emergencyContact) {
        repository.delete(emergencyContact);
    }

    public EmergencyContact getPrimaryEmergencyContact() {
        if (allEmergencyContacts.getValue() == null) {
            return null;
        }
        for (EmergencyContact contact : allEmergencyContacts.getValue()) {
            if (contact.isPrimary()) {
                return contact;
            }
        }
        return null;
    }
}
