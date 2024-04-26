package edu.odu.cs.teamblack.cs411.thecouponapp.data.repository;

import android.app.Application;

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

    public EmergencyContactRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        emergencyContactDao = db.emergencyContactDao();
        allEmergencyContacts = emergencyContactDao.getAllEmergencyContacts();
        executorService = Executors.newFixedThreadPool(4); // Or another appropriate thread pool size
    }

    public LiveData<List<EmergencyContact>> getAllEmergencyContacts() {
        return allEmergencyContacts;
    }

    public void insert(EmergencyContact emergencyContact) {
        executorService.execute(() -> {
            emergencyContactDao.insert(emergencyContact);
        });
    }

    public void delete(EmergencyContact emergencyContact) {
        executorService.execute(() -> {
            emergencyContactDao.delete(emergencyContact);
        });
    }

    public void update(EmergencyContact emergencyContact) {
        executorService.execute(() -> {
            emergencyContactDao.update(emergencyContact);
        });
    }

}
