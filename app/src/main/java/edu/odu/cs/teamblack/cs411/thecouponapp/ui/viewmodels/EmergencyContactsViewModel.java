package edu.odu.cs.teamblack.cs411.thecouponapp.ui.viewmodels;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.repository.EmergencyContactRepository;

public class EmergencyContactsViewModel extends AndroidViewModel {
    private LiveData<List<EmergencyContact>> allEmergencyContacts;
    private EmergencyContactRepository repository;
    private Handler handler = new Handler();

    public EmergencyContactsViewModel(Application application) {
        super(application);
        repository = new EmergencyContactRepository(application);
        allEmergencyContacts = repository.getAllEmergencyContacts();
    }

    // Method to set a new primary contact
    public void setUniquePrimaryContact(long contactId) {
        Log.d("ViewModel", "Setting unique primary contact: " + contactId);
        repository.setUniquePrimaryContact(contactId);
    }

    // Method to insert a new emergency contact
    // Inside EmergencyContactsViewModel class
    public void insert(EmergencyContact emergencyContact, Runnable afterSave) {
        repository.insert(emergencyContact, afterSave);
    }

    public void update(EmergencyContact emergencyContact, Runnable afterSave) {
        repository.update(emergencyContact, afterSave);
    }


    // Method to delete an emergency contact
    public void delete(EmergencyContact emergencyContact) {
        repository.delete(emergencyContact);
    }

    // Getter for all emergency contacts LiveData
    public LiveData<List<EmergencyContact>> getAllEmergencyContacts() {
        return allEmergencyContacts;
    }

}
