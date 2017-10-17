package com.project.year2.medicationrecognition;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

            if(!textRecognizer.isOperational()){


                Log.e("ERROR","DETECTOR DEPENDENCIES ARE NOT AVAILABLE YET");

            }else {

                Toast.makeText(context, "here1", Toast.LENGTH_SHORT).show();
                // Text Recognizer Is Working

                Frame frame = new Frame.Builder().setBitmap(bitmap).build();

                //Detecting Text
                //Storing all the results

                SparseArray<TextBlock> items = textRecognizer.detect(frame);

                Log.i("size",String.valueOf(items.size()));

                for (int i = 0 ; i<items.size();i++){



                    TextBlock item = items.valueAt(i);

                    Log.i("item " + String.valueOf(i),item.getValue());

                }

            }


    }


    //This function allows the user to take a picture

    public void openCamera(View view){

        //Intent to open the camera app

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //0 is the request code
        startActivityForResult(cameraIntent,0);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //open camera intent
        if(requestCode == 0){

            if(data != null){

                // The image captured
                Bitmap captured_Image_BitMap = (Bitmap) data.getExtras().get("data");

                if(captured_Image_BitMap != null){


                    image_view.setImageBitmap(captured_Image_BitMap);

                    extractText(captured_Image_BitMap);
                }
            }

        }
    }

    
}
