package alex_j.lab2a.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private Sensor magneticSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gravitySensor != null) {
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (magneticSensor != null) {
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final DecimalFormat df = new DecimalFormat("0.00");

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            float xaccel = event.values[0];
            float yaccel = event.values[1];
            float zaccel = event.values[2];

            EditText gravValue_x = findViewById(R.id.gravValue_x);
            gravValue_x.setText(df.format(xaccel)+ "m/s\u00B2");

            EditText gravValue_y = findViewById(R.id.gravValue_y);
            gravValue_y.setText(df.format(yaccel)+ "m/s\u00B2");

            EditText gravValue_z = findViewById(R.id.gravValue_z);
            gravValue_z.setText(df.format(zaccel)+ "m/s\u00B2");

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float xgrav = event.values[0];
            float ygrav = event.values[1];
            float zgrav = event.values[2];

            EditText magValue_x = findViewById(R.id.magValue_x);
            magValue_x.setText(df.format(xgrav)+ " \u00B5T");

            EditText magValue_y = findViewById(R.id.magValue_y);
            magValue_y.setText(df.format(ygrav)+ " \u00B5T");

            EditText magValue_z = findViewById(R.id.magValue_z);
            magValue_z.setText(df.format(zgrav)+ " \u00B5T");
        }
    }
    //calc error
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // no-op for this lab
    }
}
