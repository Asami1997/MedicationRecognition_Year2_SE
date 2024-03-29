package com.project.year2.medicationrecognition;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class UserOCR extends AppCompatActivity {

    //View in our layout
    StringBuilder stringBuilder;
    private static final int TAKE_PICTURE = 1;
    private String[] DATE_Dictionary;
    //will be used to get the image from the device's storage
    private Uri imageUri;
    //This array list will contain the results of the segmentation and searching process
    private ArrayList<String> detailsList;
    private ArrayList<String> toBeChecked;
    private String NAME = "";
    private String GENDER = "";
    private String BIRTHDATE = "";
    private String AGE = "";
    private String PHONE = "";
    private String DRUGS="";
    private String EMAIL = "";
    private int month;
    SimpleDateFormat month_date ;
    String month_name ;
    private int year;
    private ArrayList<String> tempArrayList;
    private ArrayList<String>allDrugs;
    private ArrayList<String>drugsInImage;
    private TransactionObject transactionObject;
    private TextView nameTextView;
    private TextView ageTextView;
    private TextView genderTextView;
    private TextView birthDateTextView;
    private TextView phoneTextView;
    private TextView rXTextView;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private Bitmap preprocessed_bitmap;
    private Preprocessing preprocessing;
    private Calendar calendar;
    FirebaseUser user;
    FirebaseAuth Auth;
    String currentUserUID;
    String alternatives;
    boolean imageTaken = false;
    ActionProcessButton processButton;
    int progress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ocr);

        //ask for storage permission


        isStoragePermissionGranted();

        //If version is above 24 , then
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        myRef = FirebaseDatabase.getInstance().getReference().child("Drugs");

        ref = FirebaseDatabase.getInstance().getReference();

        Auth = FirebaseAuth.getInstance();

        mAuth =  FirebaseAuth.getInstance();
        //initializing textview

        currentUserUID = mAuth.getCurrentUser().getUid();

        nameTextView = (TextView) findViewById(R.id.nameTextView);

        ageTextView = (TextView) findViewById(R.id.ageTextView);

        genderTextView = (TextView) findViewById(R.id.genderTextView);

        birthDateTextView = (TextView) findViewById(R.id.birthDateTextView);

        phoneTextView = (TextView) findViewById(R.id.phoneTextView);

        rXTextView = (TextView) findViewById(R.id.rxTextView);

        processButton = (ActionProcessButton) findViewById(R.id.processButton);

        preprocessing = new Preprocessing();

        //Dictionary array in string.xml file
        DATE_Dictionary = getResources().getStringArray(R.array.dateArray);
        detailsList = new ArrayList<>();
        tempArrayList = new ArrayList<>();
        allDrugs = new ArrayList<>();
        toBeChecked = new ArrayList<>();
        drugsInImage = new ArrayList<>();
        stringBuilder = new StringBuilder();

        checkForNotifications();

        calendar = Calendar.getInstance();

        //cuurent month will be used later when updating requests on firebase
        getCurrentMonth();
        getCurrentYear();
    }


    private void checkForNotifications() {

        final DatabaseReference notifications = FirebaseDatabase.getInstance().getReference().child("Notifications");

        notifications.child(currentUserUID).child("notification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.getValue() != null){

                   String value = dataSnapshot.getValue().toString();

                    if(value.equals("yes")){

                        notifications.child(currentUserUID).child("notification").setValue("no");
                        getAlternativesIfExist();
                    }else{

                        pushNotification("No");
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //notify the user about the transaction status
    private void pushNotification(String notify) {

        if(notify.equals("No")){

            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    this);
            builder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Previous Transaction Status")
                    .setContentText("Approved")
                    .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setStyle(new NotificationCompat.BigTextStyle());


            Notification notification = builder.build();
            notificationManager.notify(1, notification);

        }else{

            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    this);
            builder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Previous Transaction Status")
                    .setContentText("Alternative drugs has been chosen for you which are :" + "\n" + alternatives)
                    .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setStyle(new NotificationCompat.BigTextStyle());


            Notification notification = builder.build();
            notificationManager.notify(1, notification);
        }
    }

    private void getAlternativesIfExist() {

        final DatabaseReference notifications = FirebaseDatabase.getInstance().getReference().child("Notifications");

        notifications.child(currentUserUID).child("alternative").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.getValue().toString().length() != 0){


                     alternatives= dataSnapshot.getValue().toString();

                     pushNotification("Yes");
                }else{

                    pushNotification("No");

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //to be stored in database
    private void getCurrentYear() {

        year = calendar.get(Calendar.YEAR);

    }

    //be stored in database
    private void getCurrentMonth() {
        java.util.Date date= new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        month = cal.get(Calendar.MONTH);

        month_date = new SimpleDateFormat("MMM");
        month_name = month_date.format(calendar.getTime());
    }

    public void extractText(Bitmap bitmap) {

        //Detect text

        //The Text Recognizer Object Will Detect The Text

        Context context = getApplicationContext();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();

        //check if the text Recognizer object is working before using it to detect text

        if (!textRecognizer.isOperational()) {


            Log.e("ERROR", "DETECTOR DEPENDENCIES ARE NOT AVAILABLE YET");

        } else {

            // Text Recognizer Is Working

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });

            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                }
            }

            Log.i("detected Text", detectedText.toString());

            extactNameAge(detectedText.toString());
            extractBirthDate(detectedText.toString());
            extractGender(detectedText.toString());
            extractPhone(detectedText.toString());
            extractRx(detectedText.toString());

            processButton.setProgress(100);
            Log.i("drugsrx",DRUGS);

        }
    }

    public void openCamera(View view) {

        //reset textviews

        resetTextViews();

        processButton.setProgress(0);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //File will be created in the device public/shared storage , outside the app
        //this file will contain the image
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        //This will allow the full sized imaged to be stored in the file
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    imageTaken = true;
                    Uri imageTaken = imageUri;
                    //This notifies the content resolver that change has happened in the file ( In this case that an image has been saved in the file)
                    getContentResolver().notifyChange(imageTaken, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    //try retrieving the image from teh devices's storage
                    try {
                        //This will retrieve the image from the given URI as a bitmap
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageTaken);
                        Log.i("valuecamera","yes");

                        //preprocess image

                        preprocessed_bitmap = preprocessing.toGrayscale(bitmap);



                    } catch (Exception e) {

                        Log.i("exceptionbit",e.getMessage().toLowerCase().toString());
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT);
                         //       .show();
                       Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                        Log.i("valueExcption",e.getMessage());
                    }
                }
        }
    }

    //look for patient name in the ocr result
    public void extactNameAge(String ocrResult) {
        //reset name
        NAME = "";
        //reset age
        AGE = "";
        //split array by new line
        String[] splitedArray = ocrResult.split("\\r?\\n");
        //loop through name dictionary
        for (int i = 0; i < splitedArray.length - 1; i++) {

            //remove special characters from a string
            splitedArray[i] = splitedArray[i].replaceAll("[\\-\\+\\^:,]", "");

            Log.i("value", splitedArray[i].toLowerCase());

            //extract name
            if (Pattern.compile(Pattern.quote("name"), Pattern.CASE_INSENSITIVE).matcher(splitedArray[i].toLowerCase()).find() && NAME.isEmpty()) {
                addToList(splitedArray[i]);
                int indexOfName = splitedArray[i].toLowerCase().indexOf("name");
                NAME = splitedArray[i].substring(indexOfName + 4);
                addToList(NAME);
                if (NAME.isEmpty()) {

                    NAME = splitedArray[i + 1];
                    addToList(NAME);
                }
                Log.i("valuename", NAME);
                if(NAME != null){
                    //append to nameTextView

                    nameTextView.setText("Patient Name : " + NAME);
                }
            }

            //extract age
            if (Pattern.compile(Pattern.quote("age"), Pattern.CASE_INSENSITIVE).matcher(splitedArray[i].toLowerCase()).find() && AGE.isEmpty()) {
                addToList(splitedArray[i]);
                //only if its a digit
                if (splitedArray[i].substring(3).matches("-?\\d+(\\.\\d+)?")) {
                    AGE = splitedArray[i].substring(3);
                    addToList(AGE);
                }


                if (AGE.isEmpty()) {
                    //only if its a digit
                    if (splitedArray[i + 1].matches("-?\\d+(\\.\\d+)?")) {
                        AGE = splitedArray[i + 1];
                        addToList(AGE);
                    }
                }

                if(AGE != null){
                    //append to nameTextView

                    ageTextView.setText("Age : " + AGE);
                }
                Log.i("valueage", AGE);
            }

        }
    }

    //look for date of birth
    public void extractBirthDate(String ocrResult) {

        //reset name
        BIRTHDATE = "";
        //split array by new line
        String[] splitedArray = ocrResult.split("\\r?\\n");
        //loop through name dictionary
        for (int i = 0; i < splitedArray.length - 1; i++) {

            //remove special characters from a string
            splitedArray[i] = splitedArray[i].replaceAll("[\\-\\+\\^:,]", "");

            Log.i("value", splitedArray[i].toLowerCase());

            for (String dicValue : DATE_Dictionary) {

                //extract name
                if (Pattern.compile(Pattern.quote(dicValue), Pattern.CASE_INSENSITIVE).matcher(splitedArray[i].toLowerCase()).find() && BIRTHDATE.isEmpty()) {
                    addToList(splitedArray[i]);
                    int indexOfName = splitedArray[i].toLowerCase().indexOf(dicValue);
                    BIRTHDATE = splitedArray[i].substring(dicValue.length());
                    addToList(BIRTHDATE);
                    if (BIRTHDATE.isEmpty()) {

                        BIRTHDATE = splitedArray[i + 1];
                        addToList(BIRTHDATE);
                    }
                    if(BIRTHDATE != null){
                        //append to nameTextView

                        birthDateTextView.setText("BirthDate : " + BIRTHDATE);
                    }
                    Log.i("valuedate", BIRTHDATE);
                }
            }
        }
    }

    //extract gender
    public void extractGender(String ocrResult) {
        //reset gender
        GENDER = "";
        //split array by new line
        String[] splitedArray = ocrResult.split("\\r?\\n");
        //loop through name dictionary
        for (int i = 0; i < splitedArray.length - 1; i++) {

            if (splitedArray[i].toLowerCase().contains("male")) {
                addToList(splitedArray[i]);
                GENDER = "Male";
            } else if (splitedArray[i].toLowerCase().contains("female")) {
                addToList(splitedArray[i]);
                GENDER = "Female";
            }
        }

        if(GENDER != null){
            //append to nameTextView

            genderTextView.setText("Gender : " + GENDER);
        }

        Log.i("valuegender", GENDER);
    }

    //extract phone
    public void extractPhone(String ocrResult) {
        //reset phone
        PHONE = "";
        //split array by new line
        String[] splitedArray = ocrResult.split("\\r?\\n");
        //loop through name dictionary
        for (int i = 0; i < splitedArray.length - 1; i++) {

            //remove special characters from a string
            splitedArray[i] = splitedArray[i].replaceAll("[\\-\\+\\^:,]", "");

            Log.i("value", splitedArray[i].toLowerCase());

            //extract phone
            if (Pattern.compile(Pattern.quote("phone"), Pattern.CASE_INSENSITIVE).matcher(splitedArray[i].toLowerCase()).find() && PHONE.isEmpty()) {
                addToList(splitedArray[i]);
                int indexOfName = splitedArray[i].toLowerCase().indexOf("phone");
                PHONE = splitedArray[i].substring(indexOfName + 5);
                addToList(PHONE);

                if (PHONE.isEmpty()) {

                    PHONE = splitedArray[i + 1];
                    addToList(PHONE);
                }

                if(PHONE != null){
                    //append to nameTextView

                    phoneTextView.setText("Phone : " + PHONE);
                }

                Log.i("valuephone", PHONE);
            }
        }
    }

    public void extractRx(String ocrResult){

        tempArrayList.clear();

        Log.i("valuelist",toBeChecked.toString());
        String[] splitedArray = ocrResult.split("\\r?\\n");
        for(int i = 0 ; i<=splitedArray.length-1;i++){
            splitedArray[i] = splitedArray[i].replaceAll("[\\-\\+\\^:,]", "");
            if(toBeChecked.contains(splitedArray[i])){

            }else{
                Log.i("valuevalue",splitedArray[i]);
                tempArrayList.add(splitedArray[i]);
            }
        }

        myRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        Log.i("here","yes");
                        getAllDrugsIngredient((Map<String,Object>) dataSnapshot.getValue());

                        user = mAuth.getCurrentUser();

                        EMAIL = user.getEmail();

                        //create a transaction object after all data has been extracted
                        transactionObject = new TransactionObject(NAME,AGE,PHONE,BIRTHDATE,GENDER,EMAIL,DRUGS);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    //gets all drugs and their active ingredients from the database
    private void getAllDrugsIngredient(Map<String, Object> value) {

        allDrugs.clear();
        Log.i("valuehere","yes");
        ArrayList<String> activeIngredients = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : value.entrySet()){

            //Get drugs map
            Map singleDrug = (Map) entry.getValue();
            //save drug name into arraylist
            allDrugs.add(entry.getKey());
            Log.i("valuedrug",entry.getKey());
            //Get active ingredient field and append to list
            activeIngredients.add( singleDrug.get("active_ingredient").toString());
        }

        prescriptionDrug();
        Log.i("drugstext",DRUGS);
        System.out.println(activeIngredients.toString());

    }

    private void prescriptionDrug() {

        DRUGS = "";
        rXTextView.setText("Rx : " );

        for(String line : tempArrayList){

            for(String drug : allDrugs){

                if(line.toLowerCase().contains(drug.toLowerCase())){

                    Log.i("drugfound",drug);

                    drugsInImage.add(drug);
                    rXTextView.append(" , " + drug);
                    DRUGS+="," + drug;
                }
            }
        }

    }

    public void addToList(String item){

        if(item != null){
            toBeChecked.add(item);
        }
    }

    //send transaction to firebase
    public void sendTransaction(View view){

        FirebaseUser user = mAuth.getCurrentUser();

        ref.child("Transactions").child(user.getUid()).setValue(transactionObject);

        for(String drug : drugsInImage){

            Log.i("drugnow",drug);
            editRequestNumber(drug);

        }

        Toast.makeText(this, "Transaction Sent", Toast.LENGTH_SHORT).show();
    }

    //the function edits how many requests has been made on the database for the drugs
    private void editRequestNumber( final String drug) {

        final DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("Requests");

        requestsRef.child(drug).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.getValue() != null) {


                        String currentVlaue =  dataSnapshot.getValue().toString();


                        int valueNew = Integer.parseInt(currentVlaue) + 1;

                        requestsRef.child(drug).child(month_name).setValue(valueNew);
                        requestsRef.child(drug).child("amount").setValue(valueNew);


                } else {


                    requestsRef.child(drug).child(month_name).setValue(1);
                    requestsRef.child(drug).child("amount").setValue(1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("permission","Permission is granted");
                return true;
            } else {

                Log.v("permission","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("permission","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("permssion","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
    //To  Prevent user from going back LoginRegister Activity
    @Override
    public void onBackPressed() {
        //do nothing when back is pressed
    }

    public void processImage (View view){


       processButton.setMode(ActionProcessButton.Mode.PROGRESS);


        if(!imageTaken){

            Toast.makeText(this, "Please take an image", Toast.LENGTH_SHORT).show();
        }else{

            imageTaken = false;
            // extract text in the image taken by user
            extractText(preprocessed_bitmap);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_signout,menu);
        return true;
    }


    //resets the textviews
    public void resetTextViews (){

        nameTextView.setText("Patient Name : ");

        phoneTextView.setText("Phone : ");

        genderTextView.setText("Gender : ");

        rXTextView.setText("Rx : " );

        birthDateTextView.setText("BirthDate : ");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.sign_out){

            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();


            Auth.signOut();

            Intent intent = new Intent(getApplicationContext(),LoginRegisterActivity.class);

            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}
