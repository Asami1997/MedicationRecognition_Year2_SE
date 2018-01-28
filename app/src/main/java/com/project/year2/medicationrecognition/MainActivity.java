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
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.*;
import java.io.IOException;

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
    String allExtractedText;
    StringBuilder stringBuilder;
    ArrayList<String> prescriptionDetails;
    private static final int TAKE_PICTURE = 1;
    private String[] Dictionary;
    //will be used to get the image from the device's storage
    private Uri imageUri;
    private String tempArray[];
    String newline ;
    private String splitedString[];
    //This array list will contain the results of the segmentation and searching process
    private ArrayList<String> detailsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         image_view = (ImageView) findViewById(R.id.panadolImageView);
         //Dictionary array in string.xml file
         Dictionary = getResources().getStringArray(R.array.Dictionary);
         newline = System.getProperty("line.separator");
         detailsList = new ArrayList<String>();
         stringBuilder = new StringBuilder();
        naturalLangProcess("Hello I am jhon and i love my mom");
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

                    stringBuilder.append(extractedString);

                    /*
                    //getName(extractedString);

                    for(String value : Dictionary){

                        //extract the first word in the string
                        tempArray= extractedString.split(" ", 2);

                        //first word in a string
                        String firstWord = tempArray[0];


                        Log.i("First word",firstWord);
                        Log.i("Dictionary value",value);

                        //remove colon from first word

                        //if extracted string contain any word in Dictionary
                        if(extractedString.contains(value) || extractedString.contains(value.toUpperCase())){

                         //check if there is only one word

                            Log.i("has first word","yes");

                         if(extractedString.lastIndexOf(value + value.length()) == extractedString.length()-1){

                             //take the next item textRecognizer extracted
                            Log.i("length","1");

                         }else{

                             //check if there is more than one line
                             boolean hasNewline = extractedString.contains(newline);

                             if(hasNewline == false){

                                 //has only one line , this is the result

                                 Log.i("one line","yes");
                                 System.out.println(value + " " + extractedString);

                                 //if already in details arraylist , dont add it
                                 if(!detailsList.contains(extractedString)){

                                     detailsList.add(extractedString);

                                 }

                             }else{
                                 //has more than one line
                                 Log.i("more than one line","yes");
                                 //check if new lines contain keywords in dictionary

                                 splitedString  = extractedString.split("\\r?\\n");

                                 for(String line : splitedString){

                                     //if line contain keywords
                                  if(line.contains(value) || line.contains(value.toUpperCase())){

                                      //take what it after it tell end of line
                                      Log.i("next line value",line);
                                      //remove this line from extracted string
                                      extractedString = extractedString.replace(line,"");

                                      Log.i("extracted stringr",extractedString);

                                      if(!detailsList.contains(line)){
                                          detailsList.add(line);
                                      }

                                  }else{
                                      //if line dose not contain keywords
                                      //add it to result
                                      Log.i("not added value",line);

                                  }

                                 }

                             }
                         }

                        }
                    }
                    */
                }

                allExtractedText = stringBuilder.toString();

                //start the natural language processing


                //Log.i("path",String.valueOf(R.raw.ennerperson));
                Log.i("detailsArrayList",detailsList.toString());

            }
             //naturalLangProcess("Hello I am jhon and i love my mom");

    }

    private void getName(String extractedString) {

        String[] splitedText = extractedString.split("\\r?\\n");

        for(String item : splitedText){
            Pattern pattern = Pattern.compile("^[a-zA-z ]*$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(item);
            if (matcher.matches()) {
                Log.i("This item is a name",item);
            }else {
                Log.i("This item is not a name", "No");
                //loop through dictionary
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

    //extract name using natural language processing
    public void naturalLangProcess(String text){

        InputStream inputStream = null;
        inputStream =  getResources().openRawResource(R.raw.ennerperson);
       // Log.i("path",String.valueOf(R.raw.ennerperson));


        TokenNameFinderModel model = null;

        try {
            model = new TokenNameFinderModel(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        NameFinderME nameFinder = new NameFinderME(model);
        String[] tokens = new String[0];
        try {
            tokens = tokenize(text);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Span nameSpans[] = nameFinder.find(tokens);
        if(nameSpans == null){
            Log.i("NameSpans ","null");
        }
        for(Span s: nameSpans)


            Log.i("Iam Name",tokens[s.getStart()]);
            Log.i("Iam here","yes");


    }

    public String[] tokenize(String sentence) throws IOException{
        InputStream inputStreamTokenizer = getResources().openRawResource(R.raw.entoken);
        TokenizerModel tokenModel = new TokenizerModel(inputStreamTokenizer);
        TokenizerME tokenizer = new TokenizerME(tokenModel);
        return tokenizer.tokenize(sentence);
    }
}
