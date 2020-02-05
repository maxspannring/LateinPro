package com.example.lateinPro;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

import java.io.IOException;
import java.io.Serializable;

import static android.widget.Toast.makeText;

public class EntryActivity extends AppCompatActivity implements Serializable {

    //Initialisierung aller Variablen
    static boolean internetStatus;
    public static final String SHARED_PREFS = "sharedPreferences";
    public static final String DARK_MODE_SWITCH_CHECKED = "darkModeSwitchChecked";
    private FirebaseAnalytics mFirebaseAnalytics;

    static boolean lockTranslation = false;
    PreviousRequestManager previousRequestManager;

    //UI Elemente
    ImageButton cameraBtn;
    android.widget.SearchView searchBar;
    Switch darkModeSwitch;
    LinearLayout background;
    TextView textView;

    Button infoBtn;
    //---------------

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        String method = "started App johou";
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, method);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
        this.getWindow().setStatusBarColor(Color.parseColor("#ffffbb33"));
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        setContentView(R.layout.starter_activity);
        initCameraBtn();
        initSearchBar();
        initDarkModeSwitch();
        initInfoBtn();
        //initOneSignal();
        darkModeSwitch.setChecked(getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getBoolean(DARK_MODE_SWITCH_CHECKED, false));
        Log.d("xxx", "onCreate: InternetConnection: " + internetStatus);
    }

    private void initCameraBtn() {
        ApiCaller apiCaller = new ApiCaller();
        internetStatus = apiCaller.testConnection();
        cameraBtn = findViewById(R.id.cameraButton);
        textView = findViewById(R.id.textView);
        cameraBtn.setOnClickListener(v -> {
            if(internetStatus){
                Intent intent  = new Intent(EntryActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(EntryActivity.this, "Keine Internetverbindung ):", Toast.LENGTH_LONG).show();
                cameraBtn.setImageResource(R.drawable.nointernet);
            }
        });
        textView.setOnClickListener(v -> {
            if(internetStatus){
                Intent intent  = new Intent(EntryActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(EntryActivity.this, "Keine Internetverbindung ):", Toast.LENGTH_LONG).show();
                cameraBtn.setImageResource(R.drawable.nointernet);
                try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
                Intent intent  = new Intent(EntryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initSearchBar(){
        ApiCaller apiCaller = new ApiCaller();
        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(internetStatus){
                    Log.d("xxx", "onQueryTextSubmit: Query:\t" + query);
                    String apiRespo = "";
                    try {
                        apiRespo  = apiCaller.run(query);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    WordObject wordObject = MainActivity.createWordObject(apiRespo, query);
                    Intent detailsIntent = new Intent(EntryActivity.this, DetailsActivity.class);
                    detailsIntent.putExtra(MainActivity.INTENT_NAME, wordObject);
                    startActivity(detailsIntent);
                } else {
                    Toast.makeText(EntryActivity.this, "Keine Internetverbindung ):", Toast.LENGTH_LONG).show();
                }
                return false;
            }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

    }

    private void initDarkModeSwitch(){
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        background = findViewById(R.id.background);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TextView topBarText = findViewById(R.id.appTitle);

            Log.d("xxx", "onCheckedChanged: Switch changed");

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DARK_MODE_SWITCH_CHECKED, darkModeSwitch.isChecked());
            editor.apply();

            if (isChecked){
                background.setBackgroundColor(Color.parseColor("#00001a"));
                darkModeSwitch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                topBarText.setTextColor(Color.WHITE);
                if(internetStatus){
                    cameraBtn.setImageResource(R.drawable.whitecamera);
                    cameraBtn.setBackgroundColor(Color.parseColor("#00001a"));
                }
            } else {
                background.setBackgroundColor(Color.WHITE);
                darkModeSwitch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                topBarText.setTextColor(Color.BLACK);
                if(internetStatus){
                    cameraBtn.setImageResource(R.drawable.camerasolid);
                    cameraBtn.setBackgroundColor(Color.WHITE);
                }
            }
        });

        darkModeSwitch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //oast.makeText(EntryActivity.this, "Übersetzung gesperrt!", Toast.LENGTH_LONG).show();
                //Toast.makeText(EntryActivity.this, "Joaaa, ds feature is weg wegen Bug " + ("\\tU+1F60E\\tU+1F60E"), Toast.LENGTH_LONG).show();
                //Toast.makeText(EntryActivity.this, "Smileys = " + ("\ud83d\ude01"),Toast.LENGTH_SHORT).show();
                runOnUiThread(() -> {
                    Toast.makeText(EntryActivity.this, "ToDo: DDoS auf lateinon.de", Toast.LENGTH_LONG).show();                    Handler h = new Handler();
                    h.postDelayed(() -> {
                        Toast.makeText(EntryActivity.this, "und btw der code ist drei mal sechs", Toast.LENGTH_SHORT).show();
                    }, 2500);
                });
                return false;
            }
        });
    }

    private void initOneSignal(){
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    private void initInfoBtn() {
        infoBtn = findViewById(R.id.secondUseBtn);
        infoBtn.setVisibility(View.VISIBLE);
        infoBtn.setOnClickListener(v -> {
            infoBtn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            Intent intent  = new Intent(EntryActivity.this, InfoActivity.class);
            startActivity(intent);
        });
    }
}
