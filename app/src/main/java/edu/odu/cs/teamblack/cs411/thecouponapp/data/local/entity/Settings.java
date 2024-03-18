package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class Settings {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public boolean videoEnabled;
    public boolean audioEnabled;
    public boolean gpsSpoofingEnabled;
    public String theme;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVideoEnabled() {
        return videoEnabled;
    }

    public void setVideoEnabled(boolean videoEnabled) {
        this.videoEnabled = videoEnabled;
    }

    public boolean isAudioEnabled() {
        return audioEnabled;
    }

    public void setAudioEnabled(boolean audioEnabled) {
        this.audioEnabled = audioEnabled;
    }

    public boolean isGpsSpoofingEnabled() {
        return gpsSpoofingEnabled;
    }

    public void setGpsSpoofingEnabled(boolean gpsSpoofingEnabled) {
        this.gpsSpoofingEnabled = gpsSpoofingEnabled;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
