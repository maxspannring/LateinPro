package com.example.lateinPro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


public class DDoSActivity extends AppCompatActivity {
    //deklarisierung
    private Button startDDosBtn;
    private TextView ddosInfoView;
    private TextView targetURLTExtView;
    private String targetURL;
    String asciiBadge =
    "\n\n\n   ,   /\\   ,\n" +
            "  / '-'  '-' \\\n" +
            " |   POLICE   |\n" +
            " \\    .--.    /\n" +
            "  |  ( 19 )  |\n" +
            "  \\   '--'   /\n" +
            "   '--.  .--'\n" +
            "jgs    \\/";

    String asciiComputer = "\n\n       ___________\n" +
            "      |.---------.|\n" +
            "      ||SYSTEM   ||\n" +
            "      ||DOWN ):  ||\n" +
            "      ||         ||\n" +
            "      |'---------'|\n" +
            "       `)__ ____('\n" +
            "       [=== -- o ]--.\n" +
            "     __'---------'__ \\\n" +
            "jgs [::::::::::: :::] )\n" +
            "     `\"\"'\"\"\"\"\"'\"\"\"\"`/T\\\n" +
            "                    \\_/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ddos_activity);
        TextView title = findViewById(R.id.appTitle);
        title.setTextColor(Color.BLACK);
        title.setText("DDoS Launcher");
        initSearchBar();
        initDDos();
    }

    private void initDDos() {
        startDDosBtn = findViewById(R.id.ddos_startBtn);
        ddosInfoView = findViewById(R.id.ddos_info);
        startDDosBtn.setOnClickListener(v -> new Thread(this::logDDos).start());
        targetURLTExtView = findViewById(R.id.ddos_url);
        Switch dmSwitch = findViewById(R.id.darkModeSwitch);
        dmSwitch.setVisibility(View.INVISIBLE);
        Button exitBtn = findViewById(R.id.cancelBtn);
        exitBtn.setOnClickListener(v -> {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });
    }

    @SuppressLint("SetTextI18n")
    private void logDDos(){
        targetURLTExtView = findViewById(R.id.ddos_url);
        ddosInfoView = findViewById(R.id.ddos_info);
        if (!targetURLTExtView.getText().toString().contains("http")){
            ddosInfoView.setText("-----------------------\n[WARNING]\tNO VALID URL!\n-----------------------");
        }else {
            ddosInfoView.setText("initialising DDoS Activity...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ddosInfoView.append("\n---[WIR ÜBERNEHMEN KEINE VERANTWORTUNG FUER JEGLICHE NUTZUNG!]---\n");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ddosInfoView.append("\nTOR-Network:\tTRUE");
            ddosInfoView.append("\nisPartOfBotnet:\tTRUE");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ddosInfoView.append("\nBotnet-size: \t4");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ddosInfoView.append("\nstarting DDoS Attack...");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10; i++) {
                ddosInfoView.append("\npinging URL...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (targetURLTExtView.getText().toString().contains("lateinon")) {
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ddosInfoView.append("\nping error... can't reach host");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ddosInfoView.append("\nSUCCESS: TARGET IS DOWN\n");
                ddosInfoView.append(asciiComputer);
                try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
                ddosInfoView.append("\n\nAber schreib uns wenn du's bis hier her geschafft hast - wir wollen wisssen wie viele das sind (:");
                try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
                ddosInfoView.append("\n\n\n\nAber no joke wenn dus ohne irgendeine Hilfe geschafft hast zählt dass als Asperger Diagnose");

            } else {
                ddosInfoView.setText("---------------");
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ddosInfoView.append("\nDeviceISRooted:\tfalse");
                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ddosInfoView.append("\nERROR: NO ROOT ACCESS!");
                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ddosInfoView.append("\nERROR: ROOT ACCESS REQUIRED TO PERFORM DDOS ATTACK!");
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ddosInfoView.append("\nexiting...");
                ddosInfoView.append("\n\n\n" + asciiBadge);
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }

        }
    }

    public void initSearchBar() {
        ApiCaller apiCaller = new ApiCaller();
        SearchView searchBar = findViewById(R.id.searchBar);
        searchBar.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (EntryActivity.internetStatus) {
                    Log.d("xxx", "onQueryTextSubmit: Query:\t" + query);
                    String apiRespo = "";
                    try {
                        apiRespo = apiCaller.run(query);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    WordObject wordObject = MainActivity.createWordObject(apiRespo, query);
                    Intent detailsIntent = new Intent(DDoSActivity.this, DetailsActivity.class);
                    detailsIntent.putExtra(MainActivity.INTENT_NAME, wordObject);
                    startActivity(detailsIntent);
                } else {
                    Toast.makeText(DDoSActivity.this, "Keine Internetverbindung ):", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
