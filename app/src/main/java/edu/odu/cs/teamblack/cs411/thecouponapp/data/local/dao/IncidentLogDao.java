package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.IncidentLog;

@Dao
public interface IncidentLogDao {
    @Insert
    void insert(IncidentLog incidentLog);

    @Update
    void update(IncidentLog incidentLog);

    @Delete
    void delete(IncidentLog incidentLog);

    @Query("SELECT * FROM incident_logs")
    LiveData<List<IncidentLog>> getAllIncidentLogs();

    @Query("SELECT * FROM incident_logs WHERE id = :uuid")
    IncidentLog getIncidentLogByUuid(String uuid);

}
