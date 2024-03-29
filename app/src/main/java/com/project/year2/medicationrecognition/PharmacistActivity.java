package com.project.year2.medicationrecognition;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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

    FirebaseAuth Auth;
    //contain all transactions
    public static ArrayList<TransactionObject> transactionObjects;

    ArrayList<PharamTransaction> pharamTransactions;

    public static ArrayList<String> usersID;

    TextView transactionsTextView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist);

        Auth = FirebaseAuth.getInstance();

        transactionObjects = new ArrayList<>();

        pharamTransactions = new ArrayList<>();

        usersID = new ArrayList<>();

        transactionsReference = FirebaseDatabase.getInstance().getReference().child("Transactions");

        recyclerView = (RecyclerView) findViewById(R.id.transactionsRecyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionsTextView = (TextView) findViewById(R.id.guiedPharmacist1);

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

                        if(dataSnapshot.getValue() == null){

                            Toast.makeText(PharmacistActivity.this, "No transactions available", Toast.LENGTH_LONG).show();

                            transactionsTextView.setText("No Transactions Available");
                        }else{
                            getAllTransactions((Map<String,Object>) dataSnapshot.getValue());
                        }

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

            usersID.add(entry.getKey());

            transactionObjects.add(transactionObject);
        }
        Log.i("sizePtrans",String.valueOf(transactionObjects.size()));

        prepareCardViewDetails();

    }

    private void setRecyclerViewAdapter(){

        transactionAdapter = new TransactionAdapter(this,pharamTransactions);

        recyclerView.setAdapter(transactionAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_signout,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.sign_out){

            Auth.signOut();

            Intent intent = new Intent(getApplicationContext(),LoginRegisterActivity.class);

            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

    }
}
