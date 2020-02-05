package com.example.lateinPro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.HashMap;

import static com.example.lateinPro.R.layout.entry_details;

public class DetailsActivity extends AppCompatActivity {
    //Deklaration der UI Elemente
    ScrollView scrollViewDetails;
    TextView theSearchWordTextView;
    LinearLayout entry_detailsLL;
    TextView dictFormTextView;
    TextView wordTypeTextView;
    TextView faelleTextView;
    TextView translationTextView;
    LinearLayout LLScrollview;
    SearchView searchBar;
    Switch darkModeSwitch;
    LinearLayout background;
    //----------------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        initSearchBar();
        initDarkModeSwitch();
        darkModeSwitch.setChecked(getSharedPreferences(EntryActivity.SHARED_PREFS, MODE_PRIVATE).getBoolean(EntryActivity.DARK_MODE_SWITCH_CHECKED, false));

        Intent intent = getIntent();
        WordObject wordObject = (WordObject) intent.getSerializableExtra(MainActivity.INTENT_NAME);



        if (wordObject != null) {
            fillDetailsList(wordObject);
        } else {
            Log.d("xxx", "onCreate: WordObject == null");
            Toast.makeText(DetailsActivity.this, "Details konnten nicht erstellt werden", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("ResourceType")
    private void fillDetailsList(WordObject wordObject) {
        Log.d("xxx", "fillDetailsList: aufgerufen...");
        Log.d("xxx", "fillDetailsList: wordObjectEntryList: " + wordObject.getEntryList().toString());
        Log.d("xxx", "fillDetailsList: entryList size: " + wordObject.getEntryList().size());
        String theSearchWord = wordObject.getEntryList().get(0).get("Suchwort").toString();
        theSearchWordTextView = findViewById(R.id.searchWord);
        scrollViewDetails  = findViewById(R.id.scrollViewDetails);
        LLScrollview = findViewById(R.id.LLScrollview);
        theSearchWordTextView.setText(theSearchWord);
        try {
            for(HashMap entryFormDict : wordObject.getEntryList()){
                LinearLayout detailsActvityLayout = (LinearLayout) findViewById(R.id.background);
                LayoutInflater inflater = getLayoutInflater();
                View inflated_entryDetails = inflater.inflate(entry_details, detailsActvityLayout, false);

                dictFormTextView = inflated_entryDetails.findViewById(R.id.dictForm);
                wordTypeTextView = inflated_entryDetails.findViewById(R.id.wordType);
                faelleTextView = inflated_entryDetails.findViewById(R.id.faelle);
                translationTextView = inflated_entryDetails.findViewById(R.id.translation);

                String woerterbuchform = entryFormDict.get("Woerterbuchform").toString();
                String type = entryFormDict.get("WordType").toString();
                String faelle = entryFormDict.get("Faelle").toString();
                String translation = entryFormDict.get("translation").toString();

                //trennt die einzelnen fälle voneinander
                StringBuilder f2 = new StringBuilder();
                for (String fall : faelle.split(",")){ f2.append(fall).append(",\n"); }
                faelle = f2.toString();
                faelle = faelle.replace("%%%", "");


                Log.d("xxx", "fillDetailsList: \nWörterbuchform: " + woerterbuchform +
                        "\ntype: " + type +
                        "\nfaelle: " + faelle +
                        "\ntranslation: " + translation);

                dictFormTextView.setText(woerterbuchform);
                dictFormTextView.setId(111);
                wordTypeTextView.setText(type);
                faelleTextView.setText(faelle);
                translationTextView.setText(translation);
                translationTextView.setMovementMethod(new ScrollingMovementMethod());

                if (!darkModeSwitch.isChecked()){
                    dictFormTextView.setTextColor(Color.BLACK);
                    wordTypeTextView.setTextColor(Color.BLACK);
                    faelleTextView.setTextColor(Color.BLACK);
                    translationTextView.setTextColor(Color.BLACK);
                }

                //färbt Word Type
                wordTypeTextView.setTextColor(Color.GRAY);
                switch (type) {
                    case "Substantiv":
                        wordTypeTextView.setTextColor(Color.parseColor("#0099ff"));
                        break;
                    case "Verb":
                        wordTypeTextView.setTextColor(Color.RED);
                        break;
                    case "Adjektiv":
                        wordTypeTextView.setTextColor(Color.parseColor("#d9ac26"));
                        break;
                }
                //inflated_entryDetails.setId(Integer.parseInt("inflatedEntryDetails"));
                LLScrollview.addView(inflated_entryDetails);
                Log.d("xxx", "fillDetailsList: Children Count" + LLScrollview.getChildCount());
            }
        } catch (Exception ex){
            Log.d("xxx", "fillDetailsList: Keine Ergebnisse /Fehler beim Füllen der Detail liste");
            TextView noResultText = new TextView(DetailsActivity.this);
            noResultText.setText("Keine Ergebnisse");
            noResultText.setTextColor(Color.WHITE);
            LLScrollview.addView(noResultText);
        }
    }


    //für die Suhleiste im Top Bar- hätte man auch anders lösen können, aber es funst
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
                Intent detailsIntent = new Intent(DetailsActivity.this, DetailsActivity.class);
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

    private void initDarkModeSwitch(){
        LLScrollview = findViewById(R.id.LLScrollview);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        background = findViewById(R.id.background);
        theSearchWordTextView = findViewById(R.id.searchWord);
        TextView topBarText = findViewById(R.id.appTitle);
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("xxx", "onCheckedChanged: Switch changed");
                SharedPreferences sharedPreferences = getSharedPreferences(EntryActivity.SHARED_PREFS, MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(EntryActivity.DARK_MODE_SWITCH_CHECKED, darkModeSwitch.isChecked());
                editor.apply();

                if (isChecked){
                    background.setBackgroundColor(Color.parseColor("#00001a"));
                    darkModeSwitch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    LLScrollview.setBackgroundColor(Color.parseColor("#00001a"));
                    theSearchWordTextView.setTextColor(Color.WHITE);
                    topBarText.setTextColor(Color.BLACK);
                    try {
                        dictFormTextView.setTextColor(Color.WHITE);
                        wordTypeTextView.setTextColor(Color.WHITE);
                        faelleTextView.setTextColor(Color.WHITE);
                        translationTextView.setTextColor(Color.WHITE);
                        Log.d("xxx", "onCheckedChanged: switched");
                    } catch (Exception e){
                        Log.d("xxx", "onCheckedChanged: Fehler: " + e);
                    }

                } else {
                    background.setBackgroundColor(Color.WHITE);
                    darkModeSwitch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    LLScrollview.setBackgroundColor(Color.WHITE);
                    theSearchWordTextView.setTextColor(Color.BLACK);
                    topBarText.setTextColor(Color.WHITE);
                    try {
                        dictFormTextView.setTextColor(Color.BLACK);
                        wordTypeTextView.setTextColor(Color.BLACK);
                        faelleTextView.setTextColor(Color.BLACK);
                        translationTextView.setTextColor(Color.BLACK);
                        Log.d("xxx", "onCheckedChanged: switched");
                    } catch (Exception e){
                        Log.d("xxx", "onCheckedChanged: Fehler: " + e);
                    }

                }
            }
        });
    }
}
