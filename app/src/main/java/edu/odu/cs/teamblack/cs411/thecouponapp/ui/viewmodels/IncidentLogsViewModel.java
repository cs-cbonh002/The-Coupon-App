package edu.odu.cs.teamblack.cs411.thecouponapp.ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.repository.IncidentLogRepository;

public class IncidentLogsViewModel extends AndroidViewModel {
    private LiveData<List<IncidentLog>> allIncidentLogs;
    private IncidentLogRepository repository;

    // Adjust the constructor to receive the Application context
    public IncidentLogsViewModel(Application application) {
        super(application);
        repository = new IncidentLogRepository(application);
        allIncidentLogs = repository.getAllIncidentLogs();
    }

    public LiveData<List<IncidentLog>> getAllIncidentLogs() {
        return allIncidentLogs;
    }

    // Methods for database operations
    public void insert(IncidentLog incidentLog) {
        repository.insert(incidentLog);
    }

    public void update(IncidentLog incidentLog) {
        repository.update(incidentLog);
    }

    public void delete(IncidentLog incidentLog) {
        repository.delete(incidentLog);
    }

}
