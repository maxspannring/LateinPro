package com.example.lateinPro;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;


class PreviousRequestManager extends AppCompatActivity {
    //So wird die Map nicht überschrieben
    static HashMap<String, String> prMap = new HashMap<>();
    private String FILE_NAME = "previousRequests.properties";

    public HashMap readMapfromFile(){
        Properties properties = new Properties();
        HashMap<String , String> hashMap = new HashMap<String, String>();
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            properties.load(isr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String key : properties.stringPropertyNames()) {
            hashMap.put(key, properties.get(key).toString());
            Log.d("xxx", "readMapfromFile: filling hashmap..." + key);
        }
        return hashMap;
    }

    @SuppressLint("CommitPrefEdits")
    public void saveMapToFile(HashMap<String, String> hashMap) throws IOException {
        //FileOutputStream fos = null;
        //fos = PreviousRequestManager.this.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);

        Log.d("xxx", "saveMapToFile: svaing Map to File...");
        Properties properties = new Properties();
        properties.putAll(hashMap);
        properties.store(new FileOutputStream(FILE_NAME), null);
        //properties.store(fos, null);
        //if(fos != null){
        //    fos.close();
        //}

    }
}
