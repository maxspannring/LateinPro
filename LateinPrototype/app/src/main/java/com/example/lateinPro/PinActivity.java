package com.example.lateinPro;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;

import java.util.Objects;

import static com.example.lateinPro.R.color.LPorange;

public class PinActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final ProgressDialog[] mProgressDialog = new ProgressDialog[1];
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_activity);
        final PinView pinView = findViewById(R.id.PinView);
        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("xxx", "afterTextChanged: anzahl" + Objects.requireNonNull(pinView.getText()).length());
                Log.d("xxx", "afterTextChanged: TextChanged" + s.toString());

                if (s.toString().equals("666")){
                    mProgressDialog[0] = ProgressDialog.show(PinActivity.this, "Please Wait...",
                            "Activity is initialising...", true);
                    new Thread(() -> {
                        pinView.setLineColor(Color.GREEN);
                        pinView.setCursorColor(Color.GREEN);
                        mProgressDialog[0].show();
                        try { Thread.sleep(3500); } catch (InterruptedException e) { e.printStackTrace(); }
                        Intent ddosIntent = new Intent(PinActivity.this, DDoSActivity.class);
                        startActivity(ddosIntent);
                        mProgressDialog[0].dismiss();
                    }).start();

                } else {
                    new Thread(() -> {
                        pinView.setLineColor(Color.RED);
                        try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                        pinView.setLineColor(getColor(LPorange));
                        try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                        pinView.setLineColor(Color.RED);
                        try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                        pinView.setLineColor(getColor(LPorange));
                    }).start();

                }
                if (Objects.requireNonNull(pinView.getText()).length() > 2 && !s.toString().equals("666")){
                    runOnUiThread(() -> {
                        Log.d("xxx", "afterTextChanged: resetting PIn");
                        Handler h = new Handler();
                        h.postDelayed(() -> {
                            pinView.setText("");
                        }, 500);
                    });
                }
            }
        });
    }
}
