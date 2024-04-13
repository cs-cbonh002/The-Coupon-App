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
    public int id; // No need for a default value since it's auto-generated

    @TypeConverters({DateConverter.class})
    public Date incidentDate = new Date(); // Default to the current system date and time

    public boolean createdByUser = true; // Default to true, assuming the user creates the log

    public Date timestamp = new Date(); // Default to the current system date and time

    public int duration = 0; // Default to 0 seconds

    public String transcription = ""; // Default to an empty string

    public String severity = "Low"; // Default to "Low" or any other default value you see fit

    public String notes = ""; // Default to an empty string


    public IncidentLog() {
        this.notes = "";
        this.severity = "Low";
        this.transcription = "";
        this.duration = 0;
        this.timestamp = new Date();
        this.incidentDate = new Date();
    }

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


    public Date getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(Date incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(this.timestamp);
    }

    public String getFormattedDuration() {
        long hours = TimeUnit.SECONDS.toHours(this.duration);
        long minutes = TimeUnit.SECONDS.toMinutes(this.duration) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.SECONDS.toSeconds(this.duration) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public IncidentLog(Parcel in) {
        id = in.readInt();
        long tmpIncidentDate = in.readLong();
        incidentDate = tmpIncidentDate != -1 ? new Date(tmpIncidentDate) : null;
        createdByUser = in.readByte() != 0;
        timestamp = new Date(in.readLong());
        duration = in.readInt();
        transcription = in.readString();
        severity = in.readString();
        notes = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(incidentDate != null ? incidentDate.getTime() : -1);
        dest.writeByte((byte) (createdByUser ? 1 : 0));
        dest.writeLong(timestamp.getTime());
        dest.writeInt(duration);
        dest.writeString(transcription);
        dest.writeString(severity);
        dest.writeString(notes);
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
