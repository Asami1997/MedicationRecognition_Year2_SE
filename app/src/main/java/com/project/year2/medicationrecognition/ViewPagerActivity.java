package com.project.year2.medicationrecognition;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class ViewPagerActivity extends AppCompatActivity {


    //view_pager in activity_main.xml
    ViewPager FEATURES_PAGER;

    //adapter for the view_pager in activity_main.xml
    App_features_Pager_Adapter FEATURES_VIEW_PAGER_ADAPTER;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        //hide action bar to make activity full screen
        getSupportActionBar().hide();

        //Initializing
        FEATURES_PAGER = (ViewPager) findViewById(R.id.viewPager);

        //Initializing
        FEATURES_VIEW_PAGER_ADAPTER = new App_features_Pager_Adapter(getSupportFragmentManager());

        //Binding the adapter with the view pager
        FEATURES_PAGER.setAdapter(FEATURES_VIEW_PAGER_ADAPTER);

        //Initializing
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);

        //Binding the tablayout with the veiw pager
        tabLayout.setupWithViewPager(FEATURES_PAGER);
    }
}
