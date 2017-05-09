package com.sample.hrv;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by isaali93 on 09/05/2017.
 */

public class ActivityRecognizedService extends IntentService {

    public static boolean stillActivity = false;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    public void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    //Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        stillActivity = true;
                    }
                }
                case DetectedActivity.ON_BICYCLE: {
                    //Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        stillActivity = false;
                    }
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    //Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        stillActivity = false;
                    }
                    break;
                }
                case DetectedActivity.RUNNING: {
                    //Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        stillActivity = false;
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    //Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        stillActivity = true;
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    //Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        stillActivity = false;
                    }
                    break;
                }
                case DetectedActivity.WALKING: {
                    //Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        stillActivity = false;
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    //Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    if( activity.getConfidence() >= 75 ) {
                        stillActivity = false;
                    }
                    break;
                }
            }
        }
    }

    public static boolean getActivityStatus(){
        //Log.e( "Acitivity Recognition", "Is user still: " + stillActivity);
        return stillActivity;
    }

}
