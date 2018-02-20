package com.project.year2.medicationrecognition;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nixon on 26/12/2017.
 */

public class requestpage extends AppCompatActivity {

    DatabaseHelper db;
    SearchView searchView;
    EditText actualMedicineET;
    TextView MedInventory;
    Spinner alternativeSP;

    public void onCreate(Bundle savedInstanceStance){
        super.onCreate(savedInstanceStance);
        setContentView(R.layout.requestpage);

        actualMedicineET = (EditText) findViewById(R.id.actualMedicineET);
        alternativeSP = (Spinner) findViewById(R.id.alternativeSP);
        MedInventory = (TextView) findViewById(R.id.MedInventory);

        actualMedicineET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get Actual Medicine Name
                String actualMedicine = actualMedicineET.getText().toString();
                process(actualMedicine);
            }
        });

        alternativeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext()) ;
                String selectedMedicine = (String) alternativeSP.getSelectedItem();
                Medicine m = databaseHelper.getSelectedMedicine(selectedMedicine);

                int i  = m.getInventory();
                MedInventory.setText(Integer.toString(i));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




//        mListView = (ListView) findViewById(R.id.medList);
//        ImageView myimageview = (ImageView) findViewById(R.id.imageView);
//        myimageview.setImageResource(R.drawable.draw);
//
//        db = new DatabaseHelper(this);
//
//        Cursor data = db.getData();
//        ArrayList<String> ListData = new ArrayList<>();
//        while (data.moveToNext()){
//            ListData.add(data.getString(1));
//        }
//
//        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListData);
//        mListView.setAdapter(adapter);
    }

    private void  process(String actualMedicine){

        DatabaseHelper databaseHelper = new DatabaseHelper(this) ;

        Medicine m = databaseHelper.getSelectedMedicine(actualMedicine);
        List medicineList = new ArrayList();

        if( m != null ) {
        if( m.getInventory() == 0) {
            Cursor cur = databaseHelper.displayData();

            while (cur.moveToNext()) {
                int medId = cur.getInt(0);
                String name = cur.getString(1);
                String ingredients = cur.getString(2);

                if (medId != m.getId() && m.getIngredients().equals(ingredients)) {
                    medicineList.add(name);
                }


            }
        }else{
            medicineList.add(actualMedicine);
        }
        }else{

            Toast.makeText(this, "Medicine Not Exists", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, medicineList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        alternativeSP.setAdapter(dataAdapter);
    }

}

