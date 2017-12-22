package com.project.year2.medicationrecognition;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class PreprocessingActivity extends AppCompatActivity {


     ImageView temprorary_imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preprocessing);
    }


    public Bitmap darkenImage(Bitmap bitmap) {

        //inflate the imageView with the bitmap

        temprorary_imageView = CropImageActivity.pre_processing_imageView;

        temprorary_imageView.setImageBitmap(bitmap);

        //will generate error because the layout has not been generated yet , so the imageView technically dose not exist yet
        temprorary_imageView.setImageBitmap(bitmap);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        temprorary_imageView.setColorFilter(filter);

        //convert imageView to Bitmap

        BitmapDrawable drawable = (BitmapDrawable) temprorary_imageView.getDrawable();
        Bitmap dark_bitmap = drawable.getBitmap();

        return dark_bitmap;

    }

    public Bitmap sharpen (Bitmap mBitmap, double weight) {
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
    public Bitmap RemoveNoise(Bitmap bmap) {
        for (int x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                if (R < 162 && G < 162 && B < 162)
                    bmap.setPixel(x, y, Color.BLACK);
            }
        }
        for (int  x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                if (R > 162 && G > 162 && B > 162)
                    bmap.setPixel(x, y, Color.WHITE);
            }
        }
        return bmap;
    }
}
