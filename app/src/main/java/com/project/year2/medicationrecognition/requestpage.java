package com.project.year2.medicationrecognition;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Nixon on 26/12/2017.
 */

public class requestpage extends AppCompatActivity {

    DatabaseHelper db;
    SearchView searchView;
    private ListView mListView;

    public void onCreate(Bundle savedInstanceStance){
        super.onCreate(savedInstanceStance);
        setContentView(R.layout.requestpage);
        mListView = (ListView) findViewById(R.id.medList);
        ImageView myimageview = (ImageView) findViewById(R.id.imageView);
        myimageview.setImageResource(R.drawable.draw);

        db = new DatabaseHelper(this);

        Cursor data = db.getData();
        ArrayList<String> ListData = new ArrayList<>();
        while (data.moveToNext()){
            ListData.add(data.getString(1));
        }

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListData);
        mListView.setAdapter(adapter);
    }

}

