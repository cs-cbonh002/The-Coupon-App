package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.dao;

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
    void insertIncidentLog(IncidentLog incidentLog);

    @Update
    void updateIncidentLog(IncidentLog incidentLog);

    @Delete
    void deleteIncidentLog(IncidentLog incidentLog);

    @Query("SELECT * FROM incident_logs")
    List<IncidentLog> getAllIncidentLogs();

    @Query("SELECT * FROM incident_logs WHERE uuid = :uuid")
    IncidentLog getIncidentLogByUuid(String uuid);

    // Additional queries as needed
}
