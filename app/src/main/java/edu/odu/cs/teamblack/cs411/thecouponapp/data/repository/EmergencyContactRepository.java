package edu.odu.cs.teamblack.cs411.thecouponapp.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.AppDatabase;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao.EmergencyContactDao;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;

public class EmergencyContactRepository {
    private EmergencyContactDao emergencyContactDao;
    private LiveData<List<EmergencyContact>> allEmergencyContacts;
    private ExecutorService executorService;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public EmergencyContactRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        emergencyContactDao = db.emergencyContactDao();
        allEmergencyContacts = emergencyContactDao.getAllEmergencyContacts();
        executorService = Executors.newFixedThreadPool(4); // Adjust the thread pool size as needed
    }

    public void setUniquePrimaryContact(long contactId) {
        executorService.execute(() -> {
            emergencyContactDao.unsetAllPrimaryContacts();
            emergencyContactDao.setContactPrimary(contactId);
        });
    }

    public LiveData<List<EmergencyContact>> getAllEmergencyContacts() {
        return allEmergencyContacts;
    }

    public LiveData<EmergencyContact> getPrimaryEmergencyContact() {
        return emergencyContactDao.getPrimaryEmergencyContact();
    }

    public void insert(EmergencyContact emergencyContact, Runnable afterSave) {
        executorService.execute(() -> {
            long id = emergencyContactDao.insert(emergencyContact); // This returns the new row ID
            if (id != -1) { // Check if the insertion was successful
                emergencyContact.setId(id);
                mainThreadHandler.post(afterSave); // Ensure afterSave runs on the main thread
            }
        });
    }

    public void update(EmergencyContact emergencyContact, Runnable afterSave) {
        executorService.execute(() -> {
            emergencyContactDao.update(emergencyContact);
            mainThreadHandler.post(afterSave); // Ensure afterSave runs on the main thread
        });
    }

    public void delete(EmergencyContact emergencyContact) {
        executorService.execute(() -> {
            emergencyContactDao.delete(emergencyContact);
        });
    }
}
