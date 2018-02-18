package com.project.year2.medicationrecognition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ser.SerializerCache;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransactionDetails extends AppCompatActivity {

    private TextView nameTextView;
    private TextView ageTextView;
    private TextView genderTextView;
    private TextView birthDateTextView;
    private TextView phoneTextView;
    private TextView rXTextView;
    Map<String,Integer> drugInventory;
    TransactionObject transactionObject;
    private DatabaseReference drugsReference;
    //will contain all the drugs in the transaction
    private String[] drugs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        Intent intent = getIntent();

        //contains all the transaction details
        transactionObject = (TransactionObject) intent.getSerializableExtra("transactionObject");

        drugsReference = FirebaseDatabase.getInstance().getReference().child("Drugs");

        drugInventory = new HashMap<>();

        //initializing text views

        nameTextView = (TextView) findViewById(R.id.d_nameTextView);
        ageTextView = (TextView) findViewById(R.id.d_ageTextView);
        genderTextView = (TextView) findViewById(R.id.d_genderTextView);
        birthDateTextView = (TextView) findViewById(R.id.d_birthDateTextView);
        phoneTextView = (TextView) findViewById(R.id.d_phoneTextView);
        rXTextView = (TextView) findViewById(R.id.d_rxTextView);

        appendDetails();

        saveTransactionDrugsToArray();

        for(String drug : drugs){

            checkInventory(drug);
        }



    }

    private void appendDetails() {

        
    }

    private void saveTransactionDrugsToArray() {


        drugs = transactionObject.DRUGS.split(",");

    }

    //checks if the medication exist in the pharmacy's inventory
    private void checkInventory(final String drug) {


            DatabaseReference singleDrug = drugsReference.child(drug.toLowerCase().trim());
            singleDrug.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Long temp = (Long) dataSnapshot.child("inventory").getValue();

                    Log.i("drug",drug.toLowerCase());

                    Log.i("temp",String.valueOf(temp));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


    }
}
