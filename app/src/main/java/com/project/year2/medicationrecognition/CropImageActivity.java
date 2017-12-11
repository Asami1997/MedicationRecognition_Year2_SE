// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.project.year2.medicationrecognition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
package com.example.androidimageview;
package pete.android.study;
import android.graphics.Bitmap;
import android.graphics.Color
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;

 /* Built-in activity for image cropping.<br>
 * Use {@link CropImage#activity(Uri)} to create a builder to start this activity.
 */
public class CropImageActivity extends AppCompatActivity
    implements CropImageView.OnSetImageUriCompleteListener,
        CropImageView.OnCropImageCompleteListener {


    // Below Is Where The Prescription Details Will Be Stored

    private  String doctorName;

    private String patientName;

    private String expirationDate;

    private String medicationName;

    private String patientIC;

    private int medicationQuantity;

    private int medicationDosage;

    private int detailsCounter = 0;

     //doctor's signature ??

  /** The crop image view library widget used in the activity */
  private CropImageView mCropImageView;

   static ImageView pre_processing_imageView;

  /** Persist URI image to crop URI if specific permissions are required */
  private Uri mCropImageUri;

  //will contain uri of original uncropped image taken or selected by user
  private Uri original_image_uri;

  /** the options that were set for the crop image */
  private CropImageOptions mOptions;

  private  PreprocessingActivity preprocessingActivity;

  private Bitmap pre_processed_bitmap;

  @Override
  @SuppressLint("NewApi")
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.crop_image_activity);

    pre_processing_imageView = (ImageView) findViewById(R.id.preprocessing_image_view);

    preprocessingActivity = new PreprocessingActivity();

    mCropImageView = findViewById(R.id.cropImageView);

    Bundle bundle = getIntent().getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE);
    mCropImageUri = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE);
    mOptions = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS);

    if (savedInstanceState == null) {
      if (mCropImageUri == null || mCropImageUri.equals(Uri.EMPTY)) {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
          // request permissions and handle the result in onRequestPermissionsResult()
          requestPermissions(
              new String[] {Manifest.permission.CAMERA},
              CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
          CropImage.startPickImageActivity(this);
        }
      } else if (CropImage.isReadExternalStoragePermissionsRequired(this, mCropImageUri)) {
        // request permissions and handle the result in onRequestPermissionsResult()
        requestPermissions(
            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
      } else {
        // no permissions required or already grunted, can start crop image activity
        mCropImageView.setImageUriAsync(mCropImageUri);
      }
    }

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      CharSequence title =
          mOptions.activityTitle != null && mOptions.activityTitle.length() > 0
              ? mOptions.activityTitle
              : getResources().getString(R.string.crop_image_activity_title);
      actionBar.setTitle(title);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

  }

  @Override
  protected void onStart() {


    super.onStart();
    mCropImageView.setOnSetImageUriCompleteListener(this);
    mCropImageView.setOnCropImageCompleteListener(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    mCropImageView.setOnSetImageUriCompleteListener(null);
    mCropImageView.setOnCropImageCompleteListener(null);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.crop_image_menu, menu);

    if (!mOptions.allowRotation) {
      menu.removeItem(R.id.crop_image_menu_rotate_left);
      menu.removeItem(R.id.crop_image_menu_rotate_right);
    } else if (mOptions.allowCounterRotation) {
      menu.findItem(R.id.crop_image_menu_rotate_left).setVisible(true);
    }

    if (!mOptions.allowFlipping) {
      menu.removeItem(R.id.crop_image_menu_flip);
    }

    if (mOptions.cropMenuCropButtonTitle != null) {
      menu.findItem(R.id.crop_image_menu_crop).setTitle(mOptions.cropMenuCropButtonTitle);
    }

    Drawable cropIcon = null;
    try {
      if (mOptions.cropMenuCropButtonIcon != 0) {
        cropIcon = ContextCompat.getDrawable(this, mOptions.cropMenuCropButtonIcon);
        menu.findItem(R.id.crop_image_menu_crop).setIcon(cropIcon);
      }
    } catch (Exception e) {
      Log.w("AIC", "Failed to read menu crop drawable", e);
    }

    if (mOptions.activityMenuIconColor != 0) {
      updateMenuItemIconColor(
          menu, R.id.crop_image_menu_rotate_left, mOptions.activityMenuIconColor);
      updateMenuItemIconColor(
          menu, R.id.crop_image_menu_rotate_right, mOptions.activityMenuIconColor);
      updateMenuItemIconColor(menu, R.id.crop_image_menu_flip, mOptions.activityMenuIconColor);
      if (cropIcon != null) {
        updateMenuItemIconColor(menu, R.id.crop_image_menu_crop, mOptions.activityMenuIconColor);
      }
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.crop_image_menu_crop) {
      cropImage();
      return true;
    }
    if (item.getItemId() == R.id.crop_image_menu_rotate_left) {
      rotateImage(-mOptions.rotationDegrees);
      return true;
    }
    if (item.getItemId() == R.id.crop_image_menu_rotate_right) {
      rotateImage(mOptions.rotationDegrees);
      return true;
    }
    if (item.getItemId() == R.id.crop_image_menu_flip_horizontally) {
      mCropImageView.flipImageHorizontally();
      return true;
    }
    if (item.getItemId() == R.id.crop_image_menu_flip_vertically) {
      mCropImageView.flipImageVertically();
      return true;
    }
    if (item.getItemId() == android.R.id.home) {
      setResultCancel();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    setResultCancel();
  }

  //This function will be called when the image is taken or selected by the user
  @Override
  @SuppressLint("NewApi")
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    // handle result of pick image chooser
    if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_CANCELED) {
        // User cancelled the picker. We don't have anything to crop
        setResultCancel();
      }

      if (resultCode == Activity.RESULT_OK) {

        //asking patient to select his name first
        //This is case 0

        Toast.makeText(this, "Please select patient name ", Toast.LENGTH_SHORT).show();

        mCropImageUri = CropImage.getPickImageResultUri(this, data);

        // For API >= 23 we need to check specifically that we have permissions to read external
        // storage.
        if (CropImage.isReadExternalStoragePermissionsRequired(this, mCropImageUri)) {
          // request permissions and handle the result in onRequestPermissionsResult()
          requestPermissions(
              new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
              CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
        } else {
          // no permissions required or already grunted, can start crop image activity

          //The Image View is inflated with image taken here
          mCropImageView.setImageUriAsync(mCropImageUri);

          //Original uncropped image
          original_image_uri = mCropImageUri;
        }
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
    if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
      if (mCropImageUri != null
          && grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // required permissions granted, start crop image activity
        mCropImageView.setImageUriAsync(mCropImageUri);
      } else {
        Toast.makeText(this, R.string.crop_image_activity_no_permissions, Toast.LENGTH_LONG).show();
        setResultCancel();
      }
    }

    if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
      // Irrespective of whether camera permission was given or not, we show the picker
      // The picker will not add the camera intent if permission is not available
      CropImage.startPickImageActivity(this);
    }
  }

  @Override
  public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
    if (error == null) {
      if (mOptions.initialCropWindowRectangle != null) {
        mCropImageView.setCropRect(mOptions.initialCropWindowRectangle);
      }
      if (mOptions.initialRotation > -1) {
        mCropImageView.setRotatedDegrees(mOptions.initialRotation);
      }
    } else {
      setResult(null, error, 1);
    }
  }

  //This method is called after the cropping has completed
  @Override
  public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
    setResult(result.getUri(), result.getError(), result.getSampleSize());

    //sending uri to function to extract bitmap
    crropedImageURI(result.getUri());


  }

  // region: Private methods

  /** Execute crop image and save the result tou output uri. */
  protected void cropImage() {


    if (mOptions.noOutputImage) {
      setResult(null, null, 1);
    } else {

      Uri outputUri = getOutputUri();

      mCropImageView.saveCroppedImageAsync(
          outputUri,
          mOptions.outputCompressFormat,
          mOptions.outputCompressQuality,
          mOptions.outputRequestWidth,
          mOptions.outputRequestHeight,
          mOptions.outputRequestSizeOptions);


      //increment with every crop
      detailsCounter++;

    }
    Toast.makeText(this, "Cropping Done", Toast.LENGTH_SHORT).show();
  }

  /** Rotate the image in the crop image view. */
  protected void rotateImage(int degrees) {
    mCropImageView.rotateImage(degrees);
  }
  /**
   * Get Android uri to save the cropped image into.<br>
   * Use the given in options or create a temp file.
   */
  protected Uri getOutputUri() {
    Uri outputUri = mOptions.outputUri;
    if (outputUri == null || outputUri.equals(Uri.EMPTY)) {
      try {
        String ext =
            mOptions.outputCompressFormat == Bitmap.CompressFormat.JPEG
                ? ".jpg"
                : mOptions.outputCompressFormat == Bitmap.CompressFormat.PNG ? ".png" : ".webp";
        outputUri = Uri.fromFile(File.createTempFile("cropped", ext, getCacheDir()));
      } catch (IOException e) {
        throw new RuntimeException("Failed to create temp file for output image", e);
      }
    }
    return outputUri;
  }

  /** Result with cropped image data or error if failed. */
  //This method is called after the cropping is done to check the result of the cropping

  protected void setResult(Uri uri, Exception error, int sampleSize) {
    int resultCode = error == null ? RESULT_OK : CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE;
    setResult(resultCode, getResultIntent(uri, error, sampleSize));

    //displaying to the user what he needs to select next after each crop

    if(detailsCounter <7){


      switch (detailsCounter){

        case 1 :

          Toast.makeText(this, "Please Select Patient IC", Toast.LENGTH_SHORT).show();

          break;

        case 2 :

          Toast.makeText(this, "Please Select Doctor Name", Toast.LENGTH_SHORT).show();

          break;

        case 3:

          Toast.makeText(this, "Please select Medication Name", Toast.LENGTH_SHORT).show();

          break;
        case 4:

          Toast.makeText(this, "Please Select Medication Dosage", Toast.LENGTH_SHORT).show();

          break;
        case 5:

          Toast.makeText(this, "Please Select Medication Quantity", Toast.LENGTH_SHORT).show();

          break;
        case 6:
          Toast.makeText(this, "Please Select Medicine Expiration Date", Toast.LENGTH_SHORT).show();
          break;
      }




      //cropImage(); wrong function to call , i think you should not call cuz the calling should be don't when the user click s crop , you should just have a counter

    }else {

      //go back to main activity only if all the details have been taken
      //this activity is started with using the method startActivityForResult() so when finish is called this activity will end and the user will be directed back to main activity.
      finish();
    }

  }

  /** Cancel of cropping activity. */
  protected void setResultCancel() {
    setResult(RESULT_CANCELED);
    finish();
  }

  /** Get intent instance to be used for the result of this activity. */
  protected Intent getResultIntent(Uri uri, Exception error, int sampleSize) {
    CropImage.ActivityResult result =
        new CropImage.ActivityResult(
            mCropImageView.getImageUri(),
            uri,
            error,
            mCropImageView.getCropPoints(),
            mCropImageView.getCropRect(),
            mCropImageView.getRotatedDegrees(),
            mCropImageView.getWholeImageRect(),
            sampleSize);
    Intent intent = new Intent();
    intent.putExtras(getIntent());
    intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result);
    return intent;
  }

  /** Update the color of a specific menu item to the given color. */
  private void updateMenuItemIconColor(Menu menu, int itemId, int color) {
    MenuItem menuItem = menu.findItem(itemId);
    if (menuItem != null) {
      Drawable menuItemIcon = menuItem.getIcon();
      if (menuItemIcon != null) {
        try {
          menuItemIcon.mutate();
          menuItemIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
          menuItem.setIcon(menuItemIcon);
        } catch (Exception e) {
          Log.w("AIC", "Failed to update menu item color", e);
        }
      }
    }
  }


  //This method is called after each crop is done in the cropImageActivity

  public Bitmap crropedImageURI(Uri uri){

    //identify this is the uri of which cropped image , ie( which detail in the prescription) and this depends on the detailCounter in the CropImageActivity at the time of calling this function


    try {

      //bitmap of cropped image
      Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

      //Pre-processing should take place here

      //DARKING THE IMAGE
      pre_processed_bitmap = preprocessingActivity.darkenImage(mBitmap);

      //DISPLAYING THE DARKEND IMAGE THE IMAGEVIEW AT THE BOTTOM
      pre_processing_imageView.setImageBitmap(pre_processed_bitmap);
        public static Bitmap sharpen(Bitmap mBitmap, double weight) {
            double[][] SharpConfig = new double[][] {
                    { 0 , -2    , 0  },
                    { -2, weight, -2 },
                    { 0 , -2    , 0  }
            };
            ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
            convMatrix.applyConfig(SharpConfig);
            convMatrix.Factor = weight - 8;
            return ConvolutionMatrix.computeConvolution3x3(mBitmap, convMatrix);
        }

      if(mBitmap == null){


          Log.i("bitmap is ","Empty");
      }
     extractText(pre_processed_bitmap);

    } catch (IOException e) {
      e.printStackTrace();
    }
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

      Frame frame = new Frame.Builder().setBitmap(bitmap)
              .build();

      //Detecting Text
      //Storing all the results in a sparsearray
      //sparse array maps integers to objects

      SparseArray<TextBlock> items = textRecognizer.detect(frame);



      Log.i("size",String.valueOf(items.size()));


      for (int i = 0 ; i<items.size();i++){

        //the data should be saved in the variables after the text has been extracted from the ocr


        TextBlock item = items.valueAt(i);

        Log.i("item",item.getValue());

      }


    }

  }

  //this function assigns the information extracted from the ocr to their appropriate variables.

  // endregion
}
}