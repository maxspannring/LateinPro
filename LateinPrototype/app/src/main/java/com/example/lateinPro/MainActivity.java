package com.example.lateinPro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.HapticFeedbackConstants;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements Serializable {
    //Deklaration von Variablen
    String currentPhotoPath;
    Uri photoURI;
    File photoFile;
    //keep track of cropping intent
    final int PIC_CROP = 3;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "xxx";
    public static final String INTENT_NAME = "wordObjecEctForDetailsActivity";

    private static final String MY_CAMERA_ID = "my_camera_id";
    private ArrayList<Integer> WpLArray = new ArrayList<Integer>();

    private InterstitialAd interstitialAd;

    private ArrayList<WordObject> wordObjectArrayList = new ArrayList<>();
    private StringBuilder allWords = new StringBuilder();

    private ProgressDialog mProgressDialog;
    private Boolean doProgressDialog; //bestimmt ob der Progress dialog gezeigt werden soll oder nicht

    private ImageView imageView;
    private TableLayout tableLayout;
    private SearchView searchBar;
    private LinearLayout background;
    private Switch darkModeSwitch;
    private Button copyBtn;
    //-----------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Oncreate aufgerufen...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisiert die AdMob mobile Ads
        MobileAds.initialize(this, getString(R.string.adMob_app_id));
        initInterstitialAd();

        imageView = findViewById(R.id.imageView);
        initSearchBar();
        initDarkModeSwitch();
        initCopyBtn();

        darkModeSwitch.setChecked(getSharedPreferences(EntryActivity.SHARED_PREFS, MODE_PRIVATE).getBoolean(EntryActivity.DARK_MODE_SWITCH_CHECKED, false));
        try {
            dispatchTakePictureIntent();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        imageView.setImageURI(photoURI);
        photoFile.delete();
    }

    //--------------------------------------------------
    //-------------[ALLES FÜR DIE KAMERA]---------------
    //--------------------------------------------------
    public  void dispatchTakePictureIntent() throws CameraAccessException {
        Log.d("xxx", "dispatchTakePictureIntent: methode gestarted...");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
                Log.d("xxx", "dispatchTakePictureIntent: An error Occured:\t" + ex.getMessage());
            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, getPackageName() + ".contentprovider", photoFile);
                Log.d(TAG, "dispatchTakePictureIntent: Photo URI : " + photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d("xxx", "dispatchTakePictureIntent: " + takePictureIntent);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //imageView.setImageURI(photoURI);
            Log.d(TAG, "onActivityResult: Camera Result");
            cropper2point0();
        }else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                //photoFile.delete();//idk
                photoURI = result.getUri();
                imageView.setImageURI(photoURI);
                displayInterstitialAd();  //zeigt werbung
                detectText();
                File fdelete = new File(String.valueOf(photoURI));
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Log.d(TAG, "onActivityResult: " + "file Deleted :" + photoURI);
                    } else {
                        Log.d(TAG, "onActivityResult: " + "file NOT Deleted :" + photoURI);
                    }
                }

            }
        } else {
            Intent backToStartIntent = new Intent(MainActivity.this, EntryActivity.class);
            startActivity(backToStartIntent);   //geht zurück zu start falls user kamera verlässt
            Log.d("xxx", "onActivityResult: OUOUOUH EIN ERROR!!!11!");
        }
    }

    private  File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "LateinPro_image_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "createImageFile: " + currentPhotoPath);
        return image;
    }

    //viel Bessere Lösung!!!!
    private void cropper2point0(){
        CropImage.activity(photoURI).start(MainActivity.this);
    }

    //--------------------------------------------------
    //-------------[Kamera Ende]------------------------
    //--------------------------------------------------

    //--------------------------------------------------
    //-------------[FIREBASE OCR]-----------------------
    //--------------------------------------------------

    //Getting camera rotaion
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(Activity activity, Context context)
            throws CameraAccessException {

        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        String[] id = cameraManager.getCameraIdList();
        Log.d(TAG, "getRotationCompensation: Camera IDs" + id.toString());
        String cameraId = id[0];
        Log.d(TAG, "getRotationCompensation: CameraId: " + cameraId);

        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Log.e(TAG, "Bad rotation value: " + rotationCompensation);
        }
        return result;
    }





    private void detectText(){
        ApiCaller apiCaller = new ApiCaller();
        Log.d(TAG, "detectText: Aufgerufen");
        doProgressDialog = true;
        startProgressDialog();
        try {
            int rotation = getRotationCompensation((Activity) this, this);
            Log.d(TAG, "detectText: Rotation: " + rotation);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        FirebaseVisionImage firebaseVisionImage = null;
        try {
            firebaseVisionImage = FirebaseVisionImage.fromFilePath(this, photoURI);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        assert firebaseVisionImage != null;
        textRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(firebaseVisionText -> {
            //Wird aufgerufen falls Text erfolgreich erkannt wurde.
            //Bestimmt den weiteren lverauf der App
            Log.d(TAG, "onSuccess: Text wird erfolgreich verarbeitet...");

            List<FirebaseVisionText.TextBlock> blockList = firebaseVisionText.getTextBlocks();
            if(blockList.size() == 0){
                tableLayout = findViewById(R.id.TableLayout);
                makeText(MainActivity.this, "Kein Text erkannt ):", LENGTH_SHORT).show();
                TextView noResultText = new TextView(MainActivity.this);
                noResultText.setText("Kein Text im Bild");
                if(darkModeSwitch.isChecked()){
                    noResultText.setTextColor(Color.WHITE);
                } else { noResultText.setTextColor(Color.BLACK); }
                noResultText.setTextSize(25);
                TableRow tableRow = new TableRow(MainActivity.this);
                tableRow.addView(noResultText);
                tableLayout.addView(tableRow);
                doProgressDialog = false;   //beendet Lade Dialog
            } else {
                for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                    for(FirebaseVisionText.Line line : block.getLines()) {
                        allWords.append("\n");
                        int numberOfWordsPerLine = line.getElements().size();
                        WpLArray.add(numberOfWordsPerLine); //fügt anzahl der Wörter pro Zeile Array hinzu, damit nahcher die anzahl der Buttons pro row stimmt
                        for (FirebaseVisionText.Element element : line.getElements()){
                            allWords.append(element.getText()).append(" ");
                            //jedes element wird gelogdt, und über die API übersetzt, dann daraus ein Word Object erstellt
                            String elementString = element.getText().replace("[0-9]", "");      //entfernt Zahlen
                            Log.d(TAG, "onSuccess: Element: "  + element.getText());
                            Rect elementFrame = element.getBoundingBox();
                            String apiRespo = "";
                            try {
                                apiRespo = apiCaller.run(elementString);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "onSuccess: APiREsponse: " + apiRespo);
                            WordObject wordObject = createWordObject(apiRespo, elementString);
                            wordObjectArrayList.add(wordObject);
                        }
                    }
                }
                mProgressDialog.setMessage("UI wird geladen...");
                createButton();     //Erstellt Buttons
                doProgressDialog = false;   //beendet Lade Dialog
            }
        }).addOnFailureListener(e -> {
            //Fehler beim Verarbeiten des textes
            Log.d(TAG, "onFailure: Fehler beim verarbeiten des Textes!!!!");
            Log.d(TAG, "onFailure: Exception: " + e);
            Toast.makeText(MainActivity.this,"Bitte Warten-beim ersten Mal muss das Text Recognition Model noch heruntergeladen werden - dass kann ein paar Minuten dauern", Toast.LENGTH_LONG).show();
            doProgressDialog = false;   //beendet Lade Dialog
        });
    }
    //erstellt ein word Object aus der ApiResponse. Diese wird in die einzelnen bestandteile Aufgespalten und in ein Word Object Übertragen, dass zu einem Array
    //hinzugefügt wird. Falls die APIRespo null ist, wird nichts eingetragen.
    public static WordObject createWordObject(String apiRespo, String Suchwort) {
        WordObject wordObject = new WordObject();
        ArrayList<HashMap> entryList = new ArrayList<>();
        if (apiRespo == null){
            apiRespo = "";
        }
        for (String entry: apiRespo.split(Pattern.quote("$$$"))){
            entry = entry.trim();
            if (entry.length() > 3){
                HashMap<String, String> FormDict = new HashMap<>();
                FormDict.put("Suchwort", Suchwort);
                FormDict.put("Woerterbuchform", entry.split("///")[0]);
                String type = entry.split("///")[0].split(" ")[1].replace(")", "").replace("(", "");
                Log.d(TAG, "createWordObject: Word Type: " + type);
                FormDict.put("WordType", type);
                FormDict.put("DictID", entry.split("///")[1]);
                FormDict.put("infinitiv", entry.split("///")[2]);
                FormDict.put("translation", entry.split("///")[3]);
                FormDict.put("Faelle", entry.split("///")[4]);
                entryList.add(FormDict);
                } else {
                Log.d(TAG, "createWordObject: Kein eintrag vorhanden!");
                HashMap<String, String> FormDict = new HashMap<>();
                FormDict.put("Suchwort", Suchwort);
                entryList.add(FormDict);
            }
        }
        wordObject.setEntryList(entryList);
        Log.d(TAG, "createWordObject: wordObject: " + wordObject.toString());
        return wordObject;
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void createButton(){
        Log.d(TAG, "createButton: aufgerufen..");
        int btnCounter = 0;
        //Erstellt für jedes WO einen Button
        tableLayout = findViewById(R.id.TableLayout);
        for (int wordsPerLine : WpLArray){
            TableRow tableRow = new TableRow(this);
            for (int i = 0; i< wordsPerLine; i++){
                WordObject correspondingWordObject = wordObjectArrayList.get(btnCounter);
                Button btn = new Button(this);
                btn.setId(btnCounter);
                Log.d(TAG, "createButton: whole EntryList: " + correspondingWordObject.getEntryList().toString());
                btn.setText(String.valueOf(correspondingWordObject.getEntryList().get(0).get("Suchwort")));

                if (correspondingWordObject.getEntryList().get(0).size() > 2) {
                    if (darkModeSwitch.isChecked()){
                        btn.setBackgroundColor(Color.parseColor("#00001a"));
                        btn.setTextColor(Color.WHITE);
                    } else {
                        btn.setBackgroundColor(Color.WHITE);
                        btn.setTextColor(Color.BLACK);
                    }
                    //btn.setTextColor(Color.parseColor(" #ffa31a"));
                    String type = Objects.requireNonNull(correspondingWordObject.getEntryList().get(0).get("WordType")).toString();
                    switch (type) {
                        case "Substantiv":
                            btn.setTextColor(Color.parseColor("#0099ff"));
                            btn.setTypeface(Typeface.defaultFromStyle((Typeface.BOLD)));
                            break;
                        case "Verb":
                            btn.setTextColor(Color.RED);
                            //btn.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                            btn.setTypeface(Typeface.defaultFromStyle((Typeface.BOLD)));
                            break;
                        case "Adjektiv":
                            btn.setTextColor(Color.parseColor("#d9ac26"));
                            btn.setTypeface(Typeface.defaultFromStyle((Typeface.BOLD)));
                            break;
                    }
                } else {
                    btn.setBackgroundColor(Color.LTGRAY);
                    btn.setAlpha((float) 0.25);
                    btn.setTextColor(Color.DKGRAY);
                }
                btn.setId(420);
                btn.setOnClickListener(v -> {
                    Log.d(TAG, "onClick: Btn clicked");
                    //STartet nur wenn wordobject auch voll ist
                    if(correspondingWordObject.getEntryList().get(0).size() > 2){
                        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                        intent.putExtra(INTENT_NAME, (Serializable) correspondingWordObject);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Keine Übersetzung Verfügbar ):", Toast.LENGTH_LONG).show();
                    }

                });
                tableRow.addView(btn);
                btnCounter++;
            }
            tableLayout.addView(tableRow);
        }
    };
    
    //Ladebalken, während die Wörter aus dem Internet geladen werden -  läuft in einem seperatem Thread
    private void startProgressDialog(){
        mProgressDialog = ProgressDialog.show(MainActivity.this, "Wörter werden geladen..",
                "Wörter und Übersetzungen dazu werden aus dem Internet geladen - das kann ein paar Sekunden dauern", true);
        mProgressDialog.show();
         new Thread(() -> {
             boolean whileBool = true;
             while (whileBool){
                 if (!doProgressDialog){
                    mProgressDialog.dismiss();
                    whileBool = false;
                    Log.d(TAG, "run: Progress Thread beendet");
                     File fdelete = new File(String.valueOf(currentPhotoPath));
                     if (fdelete.exists()) {
                         if (fdelete.delete()) {
                             Log.d(TAG, "run: " + "file Deleted :" + photoURI);
                         } else {
                             Log.d(TAG, "run: " + "file NOT Deleted :" + photoURI);
                         }
                     }
                     photoFile.delete();
                 }
             }
         }).start();
    }


    //für die Suchleiste im Top Bar- hätte man auch anders lösen können, aber es funst
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
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
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

    @SuppressLint("ResourceType")
    private void initDarkModeSwitch(){
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        background = findViewById(R.id.background);
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TextView noResulttext = findViewById(69);
            TextView topBarText = findViewById(R.id.appTitle);
            Button emptyWordBtn = findViewById(1337);

            SharedPreferences sharedPreferences = getSharedPreferences(EntryActivity.SHARED_PREFS, MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(EntryActivity.DARK_MODE_SWITCH_CHECKED, darkModeSwitch.isChecked());
            editor.apply();

            Log.d("xxx", "onCheckedChanged: Switch changed");
            if (isChecked){
                background.setBackgroundColor(Color.parseColor("#00001a"));
                darkModeSwitch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                if(noResulttext != null) noResulttext.setTextColor(Color.WHITE);
                topBarText.setTextColor(Color.BLACK);
                if(emptyWordBtn != null){
                    emptyWordBtn.setBackgroundColor(Color.WHITE);
                    emptyWordBtn.setTextColor(Color.BLACK);}
            } else {
                background.setBackgroundColor(Color.WHITE);
                darkModeSwitch.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                //findViewById(420).setBackgroundColor(Color.WHITE);
                if(noResulttext != null) noResulttext.setTextColor(Color.BLACK);
                topBarText.setTextColor(Color.WHITE);
                if(emptyWordBtn != null){
                    emptyWordBtn.setBackgroundColor(Color.parseColor("#00001a"));
                    emptyWordBtn.setTextColor(Color.WHITE);}

            }
        });
        darkModeSwitch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick: AllWords: " + allWords.toString());
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    assert clipboard != null;
                    clipboard.setText(allWords.toString());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("LateinPro Text", allWords.toString());
                    assert clipboard != null;
                    clipboard.setPrimaryClip(clip);
                }
                makeText(MainActivity.this, "Text in Zwischenablage kopiert!", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }
    private void initCopyBtn(){
        copyBtn = findViewById(R.id.secondUseBtn);
        copyBtn.setBackground(getDrawable(R.drawable.copy));
        copyBtn.setVisibility(View.VISIBLE);
        copyBtn.setOnClickListener(v -> {
            copyBtn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("LateinPro Text", allWords.toString());
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            makeText(MainActivity.this, "Text in Zwischenablage kopiert!", Toast.LENGTH_LONG).show();
            //crashed App
            //Button b = null;
            //b.setTextColor(Color.RED);
            //TDo: wieder entfernen!!!!!
        });
    }
    private void initInterstitialAd(){
        //App iD ca-app-pub-5192204082942271~8610725171
        Log.d(TAG, "initInterstitialAd: initing ad...");
        List<String> testDeviceIds = Arrays.asList("D871BFDDD5456D8679882006C3E9F636");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        MobileAds.initialize(MainActivity.this, getString(R.string.adMob_app_id));
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("D871BFDDD5456D8679882006C3E9F636").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        interstitialAd = new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId(getString(R.string.adMob_app_interstitialAdd_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        //interstitialAd.loadAd(adRequest);
        Log.d(TAG, "initInterstitialAd: is test device: " + adRequest.isTestDevice(this));
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded(){
                Log.d(TAG, "onAdLoaded: loaded");
                //displayInterstitialAd();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(TAG, "onAdFailedToLoad: Add failed to laod ): + " + i);
                super.onAdFailedToLoad(i);
            }
        });
        interstitialAd.loadAd(adRequest);
    }
    
    private void displayInterstitialAd(){
        if (interstitialAd.isLoaded()){
            Log.d(TAG, "displayInterstitialAd: Jouhou, Werbung wird gezeigt");
            //Nachher wieder anmachen nicht vergessen!
            interstitialAd.show();
        } else{
            Log.d(TAG, "displayInterstitialAd: Werbung konnte nicht gezeigt werden, weil sie nicht geladen war ):");
        }
    }
}
