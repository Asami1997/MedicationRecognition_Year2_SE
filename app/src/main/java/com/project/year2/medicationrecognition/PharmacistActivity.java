package com.project.year2.medicationrecognition;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class PharmacistActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    TransactionAdapter transactionAdapter;

    DatabaseReference transactionsReference;

    //contain all transactions
    public static ArrayList<TransactionObject> transactionObjects;

    ArrayList<PharamTransaction> pharamTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist);

        transactionObjects = new ArrayList<>();

        pharamTransactions = new ArrayList<>();

        transactionsReference = FirebaseDatabase.getInstance().getReference().child("Transactions");

        recyclerView = (RecyclerView) findViewById(R.id.transactionsRecyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //get transactions fromfire base

        getFireBaseTransactions();

    }

    private void prepareCardViewDetails() {

        for(TransactionObject transaction : transactionObjects){

            pharamTransactions.add(new PharamTransaction(transaction.EMAIL,transaction.NAME));
        }
        Log.i("sizepharma",String.valueOf(pharamTransactions.size()));

        setRecyclerViewAdapter();
    }

    private void getFireBaseTransactions() {

        transactionsReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        Log.i("here","yes");
                        getAllTransactions((Map<String,Object>) dataSnapshot.getValue());

                        //create a transaction object after all data has been extracted
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    private void getAllTransactions(Map<String, Object> value) {

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : value.entrySet()){

            //Get drugs map
            Map singleTransaction = (Map) entry.getValue();
            //save drug name into arraylist
            TransactionObject transactionObject = new TransactionObject(singleTransaction.get("NAME").toString(),
                    singleTransaction.get("AGE").toString(),singleTransaction.get("PHONE").toString(),
                    singleTransaction.get("BIRTHDATE").toString(),singleTransaction.get("GENDER").toString(),
                    singleTransaction.get("EMAIL").toString(),singleTransaction.get("DRUGS").toString());

            Log.i("sizeemail",transactionObject.EMAIL);
            Log.i("sizename",transactionObject.NAME);

            transactionObjects.add(transactionObject);
        }
        Log.i("sizePtrans",String.valueOf(transactionObjects.size()));

        prepareCardViewDetails();

    }

    private void setRecyclerViewAdapter(){

        transactionAdapter = new TransactionAdapter(this,pharamTransactions);

        recyclerView.setAdapter(transactionAdapter);
    }

    //start transaction activity
    public void startTransactionActivity(int position){

        Intent intent = new Intent(PharmacistActivity.this, TransactionDetails.class);

        intent.putExtra("transactionObject",transactionObjects.get(position));

        startActivity(intent);
    }
}
