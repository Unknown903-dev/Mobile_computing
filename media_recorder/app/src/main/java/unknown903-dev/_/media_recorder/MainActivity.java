package alex_j.lab3.media_recorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.camera.view.PreviewView;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private PreviewView previewView;
    private Button captureButton;
    private EditText editText;
    private boolean isRecording = false;
    private VideoCapture<Recorder> videoCapture;
    private Recording currentRecording;

    private static final String TAG = "Recorder";
    private static final int REQUEST_CODE_PERMISSIONS = 200;

    private final String[] REQUIRED_PERMISSIONS = new String[] {
            //gets permissions from user
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.preview_view);
        captureButton = findViewById(R.id.button_capture);
        editText = findViewById(R.id.video_name);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            );
        }

        captureButton.setOnClickListener(this::onCaptureClick);
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreviewAndVideoCapture(cameraProvider);
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreviewAndVideoCapture(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        Recorder recorder = new Recorder.Builder().build();
        videoCapture = VideoCapture.withOutput(recorder);

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(
                    (LifecycleOwner) this,
                    cameraSelector,
                    preview,
                    videoCapture
            );
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    // record stop button
    public void onCaptureClick(View view) {
        if (isRecording) {
            currentRecording.stop();
            currentRecording = null;
            setCaptureButtonText("START");
            isRecording = false;
        } else {
            startRecording();
            setCaptureButtonText("STOP");
            isRecording = true;
        }
    }

    private void startRecording() {
        String videoName = editText.getText().toString();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.US)
                .format(new Date());
        String fileName = videoName + "_" + timeStamp + ".mp4";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(
                    MediaStore.Video.Media.RELATIVE_PATH,
                    "Movies/Lab3_Recordings"
            );
        }

        MediaStoreOutputOptions mediaStoreOutputOptions =
                new MediaStoreOutputOptions.Builder(
                        getContentResolver(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                        .setContentValues(contentValues)
                        .build();

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission check failed");
            return;
        } else {
            Log.d(TAG, "permission check passed");
        }

        currentRecording = videoCapture.getOutput()
                .prepareRecording(this, mediaStoreOutputOptions)
                .withAudioEnabled()
                .start(ContextCompat.getMainExecutor(this), videoRecordEvent -> {
                    if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                        if (!((VideoRecordEvent.Finalize) videoRecordEvent).hasError()) {
                            Log.d(TAG, "Video saved successfully");
                        } else {
                            Log.e(TAG, "Video saving failed: " +
                                    ((VideoRecordEvent.Finalize) videoRecordEvent).getError());
                        }
                    }
                });
    }

    private void setCaptureButtonText(String title) {
        captureButton.setText(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentRecording != null) {
            currentRecording.stop();
            currentRecording = null;
        }
    }
}
