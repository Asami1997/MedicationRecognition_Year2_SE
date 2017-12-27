package com.project.year2.medicationrecognition;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * In this class , i am trying to find the required information from the generated text , instead of the image itself
 * Using Regular Expressions
 *
 */
public class MainActivity extends AppCompatActivity {

    //View in our layout

    ImageView image_view;
    String mCurrentPhotoPath;
    ArrayList<String> prescriptionDetails;
    private static final int TAKE_PICTURE = 1;
    private String[] Dictionary;
    //will be used to get the image from the device's storage
    private Uri imageUri;
    private String tempArray[];
    String newline ;
    private String splitedString[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         image_view = (ImageView) findViewById(R.id.panadolImageView);
         //Dictionary array in string.xml file
         Dictionary = getResources().getStringArray(R.array.Dictionary);
         newline = System.getProperty("line.separator");
    }

    public void extractText(Bitmap bitmap) {

            //Detect text

            //The Text Recognizer Object Will Detect The Text

            Context context = getApplicationContext();

            TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();


            //check if the text Recognizer object is working before using it to detect text

            if(!textRecognizer.isOperational()){


                Log.e("ERROR","DETECTOR DEPENDENCIES ARE NOT AVAILABLE YET");

            }else {

                Toast.makeText(context, "here1", Toast.LENGTH_SHORT).show();
                // Text Recognizer Is Working

                Frame frame = new Frame.Builder().setBitmap(bitmap).build();

                //Detecting Text
                //Storing all the results in a sparsearray
                //sparse array maps integers to objects

                SparseArray<TextBlock> items = textRecognizer.detect(frame);

                Log.i("size",String.valueOf(items.size()));

                for (int i = 0 ; i<items.size();i++){

                    TextBlock item = items.valueAt(i);

                    String extractedString = item.getValue();

                    Log.i("item " + String.valueOf(i),extractedString);

                    //identify the item generated

                    //regexCheaker("Patient|Age|Medication|Date|Dosage",extractedString);

                   //loop through dictionary

                    for(String value : Dictionary){

                        //extract the first word in the string
                        tempArray= extractedString.split(" ", 2);

                        //first word in a string
                        String firstWord = tempArray[0];

                        //if first word is in Dictionary
                        if(firstWord.equals(value)){

                         //check if there is only one wordd

                         if(tempArray.length == 1){

                             //take the next item textRecognizer extracted

                         }else{

                             //check if there is more than one line
                             boolean hasNewline = extractedString.contains(newline);

                             if(hasNewline == false){

                                 //has only one line , this is the result

                             }else{
                                 //has more than one line

                                 //check if new lines contain keywords in dictionary

                                 splitedString  = extractedString.split("\\r?\\n");

                                 for(String line : splitedString){

                                     //if line contain keywords
                                  if(line.contains(value)){

                                  }else{
                                      //if line dose not contain keywords
                                      //add it to result
                                  }

                                 }

                             }
                         }

                        }
                    }
                }

            }


    }

    public void openCamera(View view){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //File will be created in the device public/shared storage , outside the app
        //this file will contain the image
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
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
                    Uri imageTaken = imageUri;
                    //This notifies the content resolver that change has happened in the file ( In this case that an image has been saved in the file)
                    getContentResolver().notifyChange(imageTaken, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    //try retrieving the image from teh devices's storage
                    try {
                        //This will retrieve the image from the given URI as a bitmap
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageTaken);

                        image_view.setImageBitmap(bitmap);
                        // extract text in the image taken by user
                        extractText(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }



    /**
     *
     *
     * @param theRegex will contain the regular expression itself
     * @param str2cheak will contain the string i want to search in
     */

    /*
    public  void regexCheaker(String theRegex,String str2cheak){

        String lines[];

        prescriptionDetails= new ArrayList<>();

        //define the regular expression

        Pattern checkRegex= Pattern.compile(theRegex);

        Matcher regexMatcher = checkRegex.matcher(str2cheak);

        //find all the matches for this

		// will give all the matches for us using the matcher

        while(regexMatcher.find()){

            //checking if the match is not null

            //.group will give the match
            if(regexMatcher.group().length() != 0 ){

                System.out.println(regexMatcher.group().trim());

            }else{

                System.out.println("No matches found");
            }

            //getting the starting index of the match

            System.out.println(regexMatcher.start());

            //getting the ending index of the match

            System.out.println(regexMatcher.end());

            //splitting the string whenever there is a new line

            lines  = str2cheak.split("\\r?\\n");

           //adding the array elements to arraylist if they are not already in the arraylist

            for(String value : lines){

                if(prescriptionDetails.contains(value)){


                }else{

                    prescriptionDetails.add(value);


                }
            }

        }

        Log.i("prescriptiondetails",prescriptionDetails.toString());

    }
    */

}
