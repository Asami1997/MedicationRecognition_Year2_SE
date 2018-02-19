package com.project.year2.medicationrecognition;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fasterxml.jackson.databind.ser.SerializerCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
    private TextView inventoryDetailsTextView;
    private LinearLayout inverntoryLayout;
    private DatabaseReference drugsReference;
    private ArrayList<String> outOfStockDrugs;
    TransactionObject transactionObject;
    TextView alternativesTextView;
    AlternativeDrugs alternativeDrugs;
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

        outOfStockDrugs = new ArrayList<>();

        //initializing views

         intializeViews();

         addtoTransactionLayout();

         saveTransactionDrugsToArray();

         checkInventory();

         alternativeDrugs = new AlternativeDrugs(drugs);

         addToAlternativesLayout();
    }

    public void addToAlternativesLayout() {

        alternativesTextView.append(alternativeDrugs.details);
    }

    private void intializeViews() {

        nameTextView = (TextView) findViewById(R.id.d_nameTextView);
        ageTextView = (TextView) findViewById(R.id.d_ageTextView);
        genderTextView = (TextView) findViewById(R.id.d_genderTextView);
        birthDateTextView = (TextView) findViewById(R.id.d_birthDateTextView);
        phoneTextView = (TextView) findViewById(R.id.d_phoneTextView);
        rXTextView = (TextView) findViewById(R.id.d_rxTextView);
        inverntoryLayout = (LinearLayout) findViewById(R.id.inventoryLinerLayout);
        inventoryDetailsTextView = (TextView) findViewById(R.id.inventoryDetailsTextView);
        alternativesTextView = (TextView) findViewById(R.id.alternativesView);

    }


    private void addtoTransactionLayout() {

        nameTextView.append(" " + transactionObject.NAME);
        ageTextView.append(" " + transactionObject.AGE);
        genderTextView.append(" " + transactionObject.GENDER);
        birthDateTextView.append(" " + transactionObject.BIRTHDATE);
        phoneTextView.append(" " + transactionObject.PHONE);
        rXTextView.append(" " + transactionObject.DRUGS);

    }

    private void addToInventoryLayout(String drug) {

        inventoryDetailsTextView.append("\n" + drug + ":" + " is out of stock");
     }

    private void saveTransactionDrugsToArray() {


        drugs = transactionObject.DRUGS.split(",");

    }

    //checks if the medication exist in the pharmacy's inventory
    private void checkInventory() {

        for(final String drug : drugs){
            DatabaseReference singleDrug = drugsReference.child(drug.toLowerCase().trim());
            singleDrug.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Long inventory = (Long) dataSnapshot.child("inventory").getValue();

                    Log.i("drug",drug.toLowerCase());

                    Log.i("temp",String.valueOf(inventory));

                    if(inventory == 0){
                        outOfStockDrugs.add(drug.toLowerCase());
                        addToInventoryLayout(drug);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

}