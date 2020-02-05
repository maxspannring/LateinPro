package com.example.lateinPro;


//Das Modul, dass auf die API von Latein.me zugreift, um übersetzungeng&Wortformen bereit zu stellen.

//Funktionsweise: main ruft ApiCaller auf und überreicht wort, nach dem gesucht wird.
//für jedes Result, dass die API bereitstellt, wird ein neues WordObject erstellt, dass die entsprechenden parameter beeinhaltet.
//das WordObject wird dann zu einem Array hinzugefügt.


import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class ApiCaller {
    private OkHttpClient client = new OkHttpClient();
    PreviousRequestManager previousRequestManager = new PreviousRequestManager();


    String run(String searchWord) throws IOException {
        String result;
        //Filtert das SearchWord, damit keine unnötigen sachen gesendet werden
        searchWord = searchWord.toLowerCase();
        searchWord = searchWord.replaceAll("[0-9]", "");
        searchWord = searchWord.replaceAll("[^a-zA-Z\\d\\s:]", "");
        Log.d("xxx", "run: TheSearchWord:\t" + searchWord);

        //Merkt sich Requests

        if(PreviousRequestManager.prMap.containsKey(searchWord)){
            result = PreviousRequestManager.prMap.get(searchWord);
            Log.d("xxx", "run: In previous requests!");
        } else {
            result = makeAPIRequest(searchWord);
            PreviousRequestManager.prMap.put(searchWord, result);
            Log.d("xxx", "run: Neuer reuest!");
        }
        return result;
    }

    private String makeAPIRequest(String searchWord){
        final String[] resText = {null};
        String url = "https://www.latein.me/_API_QUE.php";
        if (searchWord != null) {
            url = url + "?q=" + searchWord;
        }
        Log.d("xxx", "run: calling url: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("xxx", "onFailure: fehler beim connecten mit der API!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    assert response.body() != null;
                    resText[0] = response.body().string();
                } else {
                    Log.d("xxx", "onResponse: bad response code: " + response.code());
                }
            }
        });
        while (resText[0] == null) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return resText[0];
    }

    boolean testConnection() {
        String URl = "https://www.youtube.com/watch?v=lxRwEPvL-mQ";
        Request request = new Request.Builder().url(URl).build();
        final boolean[] status = {false};
        final boolean[] requestFinished = {false};
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("xxx", "onFailure: Internet is Neuland");
                requestFinished[0] = true;
            }

            @Override
            public void onResponse(Call call, Response response) {
                status[0] = true;
                Log.d("xxx", "onResponse: Internet funst, ");
                requestFinished[0] = true;
            }
        });
        while (!requestFinished[0]) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return status[0];

    }
}
