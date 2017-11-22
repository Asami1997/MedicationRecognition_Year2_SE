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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //View in our layout

    ImageView image_view;
    String mCurrentPhotoPath;
    private static final int TAKE_PICTURE = 1;

    //will be used to get the image from the device's storage
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         image_view = (ImageView) findViewById(R.id.panadolImageView);

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

            Toast.makeText(context, "here1", Toast.LENGTH_SHORT).show();
            // Text Recognizer Is Working

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            //Detecting Text
            //Storing all the results in a sparsearray
            //sparse array maps integers to objects

            SparseArray<TextBlock> items = textRecognizer.detect(frame);

            Log.i("size", String.valueOf(items.size()));


            for (int i = 0; i < items.size(); i++) {

                TextBlock item = items.valueAt(i);

                Log.i("item " + String.valueOf(i), item.getValue());


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

}
