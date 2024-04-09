package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import java.util.Date;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.converters.DateConverter;

@Entity(tableName = "incident_logs")
public class IncidentLog {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @TypeConverters({DateConverter.class})
    public Date incidentDate;
    public String description;
    public boolean createdByUser;
    public Date timestamp;
    public int duration; // Duration in seconds
    public String transcription;

    public String severity;
    public String notes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(boolean createdByUser) {
        this.createdByUser = createdByUser;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(this.timestamp);
    }

    // Method to get formatted duration in HH:mm:ss format
    public String getFormattedDuration() {
        long hours = TimeUnit.SECONDS.toHours(this.duration);
        long minutes = TimeUnit.SECONDS.toMinutes(this.duration) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.SECONDS.toSeconds(this.duration) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }
}
