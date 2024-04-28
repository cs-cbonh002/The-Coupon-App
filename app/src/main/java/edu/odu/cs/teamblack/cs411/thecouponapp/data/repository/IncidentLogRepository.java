package edu.odu.cs.teamblack.cs411.thecouponapp.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.AppDatabase;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao.IncidentLogDao;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.api.ApiService;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.remote.api.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IncidentLogRepository {
    private IncidentLogDao incidentLogDao;
    private LiveData<List<IncidentLog>> allIncidentLogs;
    private ExecutorService executorService;
    private Context appContext; // Add this line


    // Constructor that gets a handle to the database and initializes the member variables.
    public IncidentLogRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        incidentLogDao = db.incidentLogDao();
        allIncidentLogs = incidentLogDao.getAllIncidentLogs();
        executorService = Executors.newFixedThreadPool(4); // Or another appropriate thread pool size
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<IncidentLog>> getAllIncidentLogs() {
        return allIncidentLogs;
    }

    // You must call this on a non-UI thread or your app will crash.
    // Room ensures that you're not doing any long-running operations on the main thread,
    // blocking the UI.
    public void insert(IncidentLog incidentLog) {
        executorService.execute(() -> {
            incidentLogDao.insert(incidentLog);
        });
    }

    // Add methods for delete, and update similarly:
    public void delete(IncidentLog incidentLog) {
        executorService.execute(() -> {
            incidentLogDao.delete(incidentLog);
        });
    }

    public void update(IncidentLog incidentLog) {
        executorService.execute(() -> {
            incidentLogDao.update(incidentLog);
        });
    }

    public void sendIncidentLogToServer(IncidentLog incidentLog) {
        ApiService apiService = RetrofitClientInstance.getRetrofitInstance(appContext).create(ApiService.class); // pass appropriate context
        Call<IncidentLog> call = apiService.createIncidentLog(incidentLog);
        call.enqueue(new Callback<IncidentLog>() {
            @Override
            public void onResponse(Call<IncidentLog> call, Response<IncidentLog> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                } else {
                    // Handle error response
                }
            }

            @Override
            public void onFailure(Call<IncidentLog> call, Throwable t) {
                // Handle failure in network request
            }
        });
    }

}
