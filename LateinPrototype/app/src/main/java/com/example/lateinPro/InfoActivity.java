package com.example.lateinPro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.Serializable;

public class InfoActivity extends AppCompatActivity implements Serializable {
    //UI Objekte
    public TextView tv1;
    public TextView tv2;
    public TextView tv3;
    public TextView tv4;
    public TextView tv5;
    public TextView tv6;

    public Switch darkModeSwitch;
    private ScrollView background;
    private Button emailBtn;
    android.widget.SearchView searchBar;

    //Deklaration von Variablen
    public static final String SHARED_PREFS = "sharedPreferences";
    public static final String DARK_MODE_SWITCH_CHECKED = "darkModeSwitchChecked";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("xxx", "onCreate: Info activity aufgerufen");
        setTheme(R.style.AppTheme);
        setContentView(R.layout.info_activity);
        initDarkModeSwitch();
        initInfoBtn();
        initSearchBar();

    }

    private void initDarkModeSwitch() {
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        background = findViewById(R.id.infoScrollView);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);


        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TextView topBarText = findViewById(R.id.appTitle);

            Log.d("xxx", "onCheckedChanged: Switch changed");

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DARK_MODE_SWITCH_CHECKED, darkModeSwitch.isChecked());
            editor.apply();

            if (isChecked) {
                background.setBackgroundColor(Color.parseColor("#00001a"));
                darkModeSwitch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                topBarText.setTextColor(Color.WHITE);
                tv1.setBackgroundColor(Color.parseColor("#00001a"));
                tv2.setBackgroundColor(Color.parseColor("#00001a"));
                tv3.setBackgroundColor(Color.parseColor("#00001a"));
                tv4.setBackgroundColor(Color.parseColor("#00001a"));
                tv5.setBackgroundColor(Color.parseColor("#00001a"));
                tv6.setBackgroundColor(Color.parseColor("#00001a"));

                tv1.setTextColor(Color.WHITE);
                tv2.setTextColor(Color.WHITE);
                tv3.setTextColor(Color.WHITE);
                tv4.setTextColor(Color.WHITE);
                tv5.setTextColor(Color.WHITE);
                tv6.setTextColor(Color.WHITE);

            } else {
                background.setBackgroundColor(Color.WHITE);
                darkModeSwitch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                topBarText.setTextColor(Color.BLACK);

                tv1.setBackgroundColor(Color.WHITE);
                tv2.setBackgroundColor(Color.WHITE);
                tv3.setBackgroundColor(Color.WHITE);
                tv4.setBackgroundColor(Color.WHITE);
                tv5.setBackgroundColor(Color.WHITE);
                tv6.setBackgroundColor(Color.WHITE);

                tv1.setTextColor(Color.BLACK);
                tv2.setTextColor(Color.BLACK);
                tv3.setTextColor(Color.BLACK);
                tv4.setTextColor(Color.BLACK);
                tv5.setTextColor(Color.BLACK);
                tv6.setTextColor(Color.BLACK);

            }
        });
    }
    private void initInfoBtn() {
        emailBtn = findViewById(R.id.secondUseBtn);
        emailBtn.setBackground(getDrawable(R.drawable.mail));
        emailBtn.setVisibility(View.VISIBLE);
        emailBtn.setOnClickListener(v -> {
            emailBtn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","lateinpro.app@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "LateinPro Feedback");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Liebes LateinPro Team,\n");
            startActivity(Intent.createChooser(emailIntent, "Mail wird gesendet..."));
        });
        emailBtn.setOnLongClickListener(v -> {
            Intent startDDoSIntent = new Intent(InfoActivity.this, PinActivity.class);
            startActivity(startDDoSIntent);
            return false;
        });
    }
    public void initSearchBar() {
        ApiCaller apiCaller = new ApiCaller();
        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("xxx", "onQueryTextSubmit: Query:\t" + query);
                String apiRespo = "";
                try {
                    apiRespo = apiCaller.run(query);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                WordObject wordObject = MainActivity.createWordObject(apiRespo, query);
                Intent detailsIntent = new Intent(InfoActivity.this, DetailsActivity.class);
                detailsIntent.putExtra(MainActivity.INTENT_NAME, wordObject);
                startActivity(detailsIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

}
