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

import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "incident_logs")
public class IncidentLog implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @TypeConverters({DateConverter.class})
    public Date incidentDate = new Date();

    public boolean createdByUser = true;

    @TypeConverters({DateConverter.class})
    public Date startTime;

    @TypeConverters({DateConverter.class})
    public Date endTime;

    public int duration = 0;

    public String transcription = "";

    public String severity = "Low";

    public String notes = "";

    private String audioFilePath;

    private String error;

    public IncidentLog() {
        this.notes = "";
        this.severity = "Low";
        this.transcription = "";
        this.duration = 0;
        this.startTime = new Date();
        this.endTime = new Date();
        this.incidentDate = new Date();
    }

    // getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(Date incidentDate) {
        this.incidentDate = incidentDate;
    }

    public boolean isCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(boolean createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        return sdf.format(getIncidentDate());
    }

    public String getFormattedDuration() {
        int totalSeconds = getDuration() / 1000;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }


    // Parcelable implementation

    public IncidentLog(Parcel in) {
        id = in.readInt();
        long tmpIncidentDate = in.readLong();
        incidentDate = tmpIncidentDate!= -1? new Date(tmpIncidentDate) : null;
        createdByUser = in.readByte()!= 0;
        long tmpStartTime = in.readLong();
        startTime = tmpStartTime!= -1? new Date(tmpStartTime) : null;
        long tmpEndTime = in.readLong();
        endTime = tmpEndTime!= -1? new Date(tmpEndTime) : null;
        duration = in.readInt();
        transcription = in.readString();
        severity = in.readString();
        notes = in.readString();
        audioFilePath = in.readString();
        error = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(incidentDate!= null? incidentDate.getTime() : -1);
        dest.writeByte((byte) (createdByUser? 1 : 0));
        dest.writeLong(startTime!= null? startTime.getTime() : -1);
        dest.writeLong(endTime!= null? endTime.getTime() : -1);
        dest.writeInt(duration);
        dest.writeString(transcription);
        dest.writeString(severity);
        dest.writeString(notes);
        dest.writeString(audioFilePath);
        dest.writeString(error);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IncidentLog> CREATOR = new Creator<IncidentLog>() {
        @Override
        public IncidentLog createFromParcel(Parcel in) {
            return new IncidentLog(in);
        }

        @Override
        public IncidentLog[] newArray(int size) {
            return new IncidentLog[size];
        }
    };
}