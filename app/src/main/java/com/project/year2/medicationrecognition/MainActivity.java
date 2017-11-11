package com.project.year2.medicationrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //Initializing pre-processing object



  }

  /** Start pick image activity with chooser. */
  public void onSelectImageClick(View view) {
    CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
  }

  //called when getting back from CropImageActivity.java
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    Toast.makeText(this, "Back From Crop Activity", Toast.LENGTH_SHORT).show();
    // handle result of CropImageActivity
    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      if (resultCode == RESULT_OK) {

        ImageView imageView =  findViewById(R.id.quick_start_cropped_image);

        imageView.setImageURI(result.getUri());



        Toast.makeText(
                this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
            .show();
      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
        Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
      }
    }
  }

  //DO nothing when back is pressed to prevent user from going back to splashscreen
  @Override
  public void onBackPressed() {

  }
}
