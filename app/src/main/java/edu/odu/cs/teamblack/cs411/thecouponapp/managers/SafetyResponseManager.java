package edu.odu.cs.teamblack.cs411.thecouponapp.managers;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;

import edu.odu.cs.teamblack.cs411.thecouponapp.data.repository.IncidentLogRepository;
import edu.odu.cs.teamblack.cs411.thecouponapp.services.AudioClassifierService;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.local.entity.EmergencyContact;
import edu.odu.cs.teamblack.cs411.thecouponapp.data.repository.EmergencyContactRepository;

public class SafetyResponseManager {


    public enum TriggerType {
        WAKE_WORD,
        AUDIO_EVENT
    }

    private CommunicationsManager communicationsManager;
    private EmergencyContactRepository emergencyContactRepository;
    private IncidentLogsManager incidentLogsManager;
    private static SafetyResponseManager instance;
    private Context context;
    private boolean isAudioClassifierRunning = false;


    private SafetyResponseManager(Context context) {
        // Use the application context to create the repository
        this.context = context;
        IncidentLogRepository incidentLogRepository = new IncidentLogRepository((Application) context.getApplicationContext());
        String audioFileDirectory = context.getFilesDir().getAbsolutePath() + "/audio_recordings/";
        File audioFileDir = new File(audioFileDirectory);
        if (!audioFileDir.exists()) {
            Log.d("SafetyResponseManager", "Creating audio recordings directory");
            audioFileDir.mkdirs(); // Create the directory if it doesn't exist
        }
        this.communicationsManager = new CommunicationsManager(context);
        this.incidentLogsManager = new IncidentLogsManager(context, incidentLogRepository, audioFileDirectory);
        this.emergencyContactRepository = new EmergencyContactRepository((Application) context.getApplicationContext());
    }

    public static synchronized SafetyResponseManager getInstance(Context context) {
        if (instance == null) {
            instance = new SafetyResponseManager(context.getApplicationContext());
        }
        return instance;
    }

    public void handleTrigger(TriggerType triggerType, String data) {
        if (triggerType == TriggerType.WAKE_WORD) {
            Log.d("SafetyResponseManager", "Wake word detected: " + data);
            handleWakeWordTrigger(data);
        } else if (triggerType == TriggerType.AUDIO_EVENT) {
            Log.d("SafetyResponseManager", "Audio event detected");
            handleAudioEventTrigger(data);
        }
    }

    public void handleAudioEventTrigger(String action) {
        if (action.equals("COMMUNICATE")) {
            initiateCommunication();
        } else if (action.equals("START_LOGGING")) {
            incidentLogsManager.startIncidentLogging(true); // started by system
        }
    }

    private void handleWakeWordTrigger(String wakeWord) {
        switch (wakeWord) {
            case "COMMUNICATE":
                initiateCommunication();
                break;
            case "START_LOGGING":
                incidentLogsManager.startIncidentLogging(true); // started by user
                break;
            case "STOP_LOGGING":
                incidentLogsManager.stopIncidentLogging();
                break;
            case "TOGGLE_AUDIO_CLASSIFIER":
                toggleAudioClassifier();
                break;
        }
    }

    private void toggleAudioClassifier() {
        if (isAudioClassifierRunning) {
            stopAudioClassifier();
        } else {
            startAudioClassifier();
        }
    }

    private void startAudioClassifier() {
        Intent intent = new Intent(context, AudioClassifierService.class);
        context.startForegroundService(intent);
        isAudioClassifierRunning = true;
    }

    private void stopAudioClassifier() {
        Intent intent = new Intent(context, AudioClassifierService.class);
        context.stopService(intent);
        isAudioClassifierRunning = false;
    }



    private void initiateCommunication() {
        EmergencyContact primaryContact = emergencyContactRepository.getPrimaryEmergencyContact().getValue();
        if (primaryContact != null) {
            communicationsManager.sendCommunication(primaryContact);
        } else {
            Log.d("SafetyResponseManager", "No primary contact set");
            communicationsManager.makeCall("5713760038"); // dummy 911
        }

    }


    public void setAudioClassificationRunning(boolean running) {
        this.isAudioClassifierRunning = running;
    }

    public boolean isAudioClassifierRunning() {
        return this.isAudioClassifierRunning;
    }

}
