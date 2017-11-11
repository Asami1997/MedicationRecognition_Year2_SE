package com.project.year2.medicationrecognition;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Ahmad Sami on 9/25/2017.
 *
 * This class contains the adapter for (Account_Types_Pager) in Main.java
 */

public class App_features_Pager_Adapter extends FragmentPagerAdapter {

    public App_features_Pager_Adapter(FragmentManager fm) {
        super(fm);
    }


    //Will get the fragment required for the specific page in the View_Pager
    @Override
    public Fragment getItem(int position) {

        switch (position){

            //Freemium Account Page
            case 0 :

                return new Feature1();

            //Premium Account Page
            case 1 :

                return new Feature2();

            //Professional Account Page
            case 2 :

                return new Feature3();
        }

        return null;
    }

    @Override
    public int getCount() {

        //Three pages in view pager
        return 3;
    }
}
