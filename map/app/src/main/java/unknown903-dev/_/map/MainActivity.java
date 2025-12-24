package unknown903-dev.map;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Launch the map automatically
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);

        // Optional: close MainActivity so back button doesn’t return here
        finish();
    }
}
