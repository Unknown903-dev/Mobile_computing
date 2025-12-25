package unknown903-dev._.detection_new.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import alex_j.lab3.detection_new.utils.constant;


public class ActivityDetectionService extends Service {

    private static final String TAG = ActivityDetectionService.class.getSimpleName();

    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;

    public ActivityDetectionService() {
        // Log.d(TAG, "ActivityDetectionService()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "onStartCommand()");

        mActivityRecognitionClient = new ActivityRecognitionClient(this);

        Intent mIntentService = new Intent(this, DetectedActivityIntentService.class);

        // FLAG_UPDATE_CURRENT indicates that if the described PendingIntent already exists,
        // then keep it but replace its extra data with what is in this new Intent.
        mPendingIntent = PendingIntent.getService(
                this,
                1,
                mIntentService,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        requestActivityUpdatesHandler();

        return START_STICKY;
    }

    // Request updates and set up callbacks for success or failure
    public void requestActivityUpdatesHandler() {
        Log.d(TAG, "requestActivityUpdatesHandler()");

        if (mActivityRecognitionClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                    constant.DETECTION_INTERVAL_IN_MILLISECONDS,
                    mPendingIntent
            );

            // Listener for successful setup
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Successfully requested activity updates");
                }
            });

            // Listener for failed setup
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Requesting activity updates failed to start");
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Need to remove the request to Google Play services. Brings down the connection.
        removeActivityUpdatesHandler();
    }

    // Remove updates and set up callbacks for success or failure
    public void removeActivityUpdatesHandler() {
        if (mActivityRecognitionClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(mPendingIntent);

            // Listener for successful removal
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Removed activity updates successfully!");
                }
            });

            // Listener for failed removal
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to remove activity updates!");
                }
            });
        }
    }
}
