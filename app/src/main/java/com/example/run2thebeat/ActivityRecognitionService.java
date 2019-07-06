package com.example.run2thebeat;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import androidx.annotation.Nullable;

public class ActivityRecognitionService extends IntentService {

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivity(result.getProbableActivities());
        }
    }

    private boolean handleDetectedActivity(List<DetectedActivity> probableActivities) {
        for (DetectedActivity activity : probableActivities) {
            switch (activity.getType()) {
                case DetectedActivity.STILL:
                    return true;
                case DetectedActivity.ON_FOOT:
                    Toast.makeText(this, "on foot", Toast.LENGTH_SHORT).show();
                case DetectedActivity.RUNNING:
                    Toast.makeText(this, "running", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
        return false;
    }
}
