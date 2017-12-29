package com.project.year2.medicationrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Nixon on 26/12/2017.
 */
public class Main extends AppCompatActivity {
    Button rtnDatabase,requestMed;

    protected void onCreate(Bundle savedInstanceStance)
    {
        super.onCreate(savedInstanceStance);
        setContentView(R.layout.mainpage);

        rtnDatabase = (Button)findViewById(R.id.rtnDatabse);
        requestMed = (Button)findViewById(R.id.request);


        rtnDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent databaseRtn = new Intent(Main.this,MainActivity.class);
                startActivity(databaseRtn);

            }
        });

        requestMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requestMed = new Intent(Main.this,requestpage.class);
                startActivity(requestMed);
            }
        });
    }
}
