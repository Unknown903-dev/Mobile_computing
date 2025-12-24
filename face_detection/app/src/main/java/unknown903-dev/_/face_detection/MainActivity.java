package Unknown903-dev._.face_detection;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    Canvas canvas;
    Bitmap mutableBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Face detector options (high accuracy)
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        // Load bitmap from assets
        Bitmap bm = getBitmapFromAssets("faces.png");

        iv = findViewById(R.id.imageview);
        iv.setImageBitmap(bm);

        // Create mutable bitmap to draw on
        mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBitmap);

        // Convert bitmap to InputImage
        InputImage image = InputImage.fromBitmap(bm, 0);

        Log.d("TAG", "before recognition");

        // Create detector
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

        // Run detection
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        Log.d("TAG", "on success recognition succeed");

                                        for (Face face : faces) {
                                            Rect bounds = face.getBoundingBox();

                                            Paint paint = new Paint();
                                            paint.setAntiAlias(true);
                                            paint.setColor(Color.RED);
                                            paint.setStyle(Paint.Style.STROKE);
                                            paint.setStrokeWidth(8);

                                            canvas.drawRect(bounds, paint);

                                            iv.setImageBitmap(mutableBitmap);
                                        }

                                        Log.d("TAG", "recognition succeed");
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "recognition failed");
                                        Toast.makeText(
                                                getApplicationContext(),
                                                e.getMessage(),
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });
    }

    // Helper method to load image from assets
    private Bitmap getBitmapFromAssets(String fileName) {
        AssetManager am = getAssets();
        InputStream is = null;

        try {
            is = am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
}
