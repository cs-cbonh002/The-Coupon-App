//https://www.youtube.com/watch?v=aL528F-AqsE
package edu.odu.cs.teamblack.cs411.thecouponapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;
import org.tensorflow.lite.task.core.BaseOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import edu.odu.cs.teamblack.cs411.thecouponapp.R;

public class SafetyMonitoringFragment extends Fragment {

    final String TAG = "Audio Classifier Fragment";
    // TensorFlow Lite
    BaseOptions baseOptionsBuilder;
    private AudioClassifier audioClassifier;
    private TensorAudio tensorAudio;
    private AudioRecord audioRecord;
    private TimerTask timerTask;

    //permissions
    int permissionsCount = 0;
    ArrayList<String> permissionsList;
    final String[] permissionsStr = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA
    };
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    //UI
    private ToggleButton toggleButton;
    private TextView channelTextView;
    private TextView outText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //receiver = new WakeWordsFragment.ServiceBroadcastReceiver();

        initPermission();

        //ask permissions
        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);

        baseOptionsBuilder = BaseOptions.builder().setNumThreads(1).useGpu().build();

        try {
            //tensorflow
            final String model = "audioClassifier.tflite";
            audioClassifier = AudioClassifier.createFromFile(requireContext(), model);
        } catch (IOException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        tensorAudio = audioClassifier.createInputTensorAudio();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_classifier, container, false);
        toggleButton = view.findViewById(R.id.audioClassifierToggleButton);
        channelTextView = view.findViewById(R.id.channelsTextView);
        outText = view.findViewById(R.id.audioClassifierOutText);

        toggleButton.setOnClickListener(v ->  {
            if (toggleButton.isChecked()) {
                if (hasPermission(getContext(),permissionsStr)) {
                    startService();
                } else {
                    initPermission();
                }
            } else {
                stopService();
            }
        });
        return view;
    }

    /////////// TensorFlow
    public void startService() {
        TensorAudio.TensorAudioFormat format = audioClassifier.getRequiredTensorAudioFormat();
        String channels = "Number of channels: " + format.getChannels() + "\n" +
                "Sample rate: " + format.getSampleRate();

        audioRecord = audioClassifier.createAudioRecord();
        audioRecord.startRecording();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                int numOfSamples = tensorAudio.load(audioRecord);
                List<Classifications> output = audioClassifier.classify(tensorAudio);

                List<Category> finalOutput = new ArrayList<>(numOfSamples);
                for (Classifications classifications : output) {
                    for (Category category : classifications.getCategories()) {
                        if (category.getScore() > 0.2f) {
                            finalOutput.add(category);
                        }
                    }
                }

                StringBuilder outStr = new StringBuilder();
                for (Category c :
                        finalOutput) {
                    outStr.append(c.getLabel()).append(": ")
                            .append(c.getScore()).append("\n");
                }
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,outStr.toString());
                        channelTextView.setText(channels);
                        outText.setText(outStr.toString());
                    }
                });
            }
        };
        new Timer().scheduleAtFixedRate(timerTask, 1, 500);
    }

    public void stopService() {
        audioRecord.stop();
        timerTask.cancel();
    }


    ///////////////////////////// Permissions
    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            requestPermissionLauncher.launch(newPermissionStr);
        } else {
        /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
        which will lead them to app details page to enable permissions from there. */
            showPermissionDialog();
        }
    }

    private void initPermission() {
        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        ArrayList<Boolean> list = new ArrayList<>(result.values());
                        permissionsList = new ArrayList<>();
                        permissionsCount = 0;
                        for (int i = 0; i < list.size(); i++) {
                            if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                permissionsList.add(permissionsStr[i]);
                            } else if (!hasPermission(getActivity(), permissionsStr[i])) {
                                permissionsCount++;
                            }
                        }
                        if (!permissionsList.isEmpty()) {
                            //Some permissions are denied and can be asked again.
                            askForPermissions(permissionsList);
                        } else if (permissionsCount > 0) {
                            //Show alert dialog
                            showPermissionDialog();
                        }  //All permissions granted. Do your stuff 🤞
                    }
                });
    }

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean hasPermission(Context context, String[] permissionStr) {
        for (String s : permissionStr) {
            if (ContextCompat.checkSelfPermission(context, s) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
    AlertDialog alertDialog;
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Permission required")
                .setMessage("Some permissions are need to be allowed to use this app without any problems.")
                .setPositiveButton("Settings", (dialog, which) -> dialog.dismiss());
        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }
}