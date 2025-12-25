package unknown903-dev._.voice_commands;

import java.util.ArrayList;
import java.util.Calendar;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.speech.RecognizerIntent;

import android.widget.CalendarView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    CalendarView calendarView;
    private static final int REQUEST_PHONE_CALL = 1;
    protected static final int RESULT_SPEECH = 1;

    private ImageButton btnSpeak;
    private TextView txtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtText = (TextView) findViewById(R.id.txtText);

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        //speak recognition starts
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"en-US");

                try{
                    startActivityForResult(intent, RESULT_SPEECH);
                    txtText.setText("");
                } catch (ActivityNotFoundException a){
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Oops! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        }

        calendarView = findViewById(R.id.calendarView);
        if(calendarView != null){
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth){
                    //Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc
                    String msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year;
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        displaySpeechRecognizer();
    }
    private static final int SPEECH_REQUEST_CODE = 0;

    //Create intent that can start Speech Recognizer Activity
    private void displaySpeechRecognizer(){

        try{
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }
        catch(ActivityNotFoundException e)
        {

            String appPackageName = "com.google.android.googlequicksearchbox";
            try{
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

        }
    }

    //    @RequiresApi(api = Build.VERSION_CODES.0)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RESULT_SPEECH:{
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtText.setText(text.get(0));
                    if (text.get(0).equals("today")){

                        long unixTime = System.currentTimeMillis();
                        calendarView.setDate(unixTime);
                    }

                    if (text.get(0).equals("tomorrow")){

                        long unixTime = System.currentTimeMillis();
                        calendarView.setDate(unixTime+86400000);
                    }
                    if (text.get(0).equals("day after tomorrow")){

                        long unixTime = System.currentTimeMillis();
                        calendarView.setDate(unixTime+172800000);
                    }
                    if (text.get(0).equals("call emergency")){
                        StringBuilder sb=new StringBuilder();
                        for (int i=1;i<text.size();i++){
                            sb.append(text.get(i));
                        }

                        String phoneNumber = getString(R.string.emergency_number);
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                        startActivity(callIntent);


                        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
                    }

                    if (text.get(0).equals("take note")){

                        StringBuilder sb=new StringBuilder();
                        for (int i=1;i<text.size();i++){
                            sb.append(text.get(i));
                        }

                        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }
}
