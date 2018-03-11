package com.project.year2.medicationrecognition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    DatabaseReference drugsReference;
    //will contain all drugs in firebase
    ArrayList<String> drugs;
    //will contain all the active ingredients of drugs in firebase
    ArrayList<String> activeIngredients;
    //will contain the inventory for each drug in the database
    ArrayList<String> inventory;
    //will contain the dosage of every drug (if it exists)
    ArrayList<String> dosage;
    ArrayAdapter drugsAdapter;
    ListView drugsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        drugsReference = FirebaseDatabase.getInstance().getReference().child("Drugs");

        drugsListView = (ListView) findViewById(R.id.drugsListView);

        drugs = new ArrayList<String>();

        activeIngredients = new ArrayList<>();

        inventory = new ArrayList<>();

        dosage = new ArrayList<>();

        drugsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("itemclclicked",drugs.get(i));

                String drugClicked = (String) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(getApplicationContext(),AdminDetails.class);

                Bundle mBundle = new Bundle();

                mBundle.putString("drugName",drugClicked);

                mBundle.putString("activeIngredient",activeIngredients.get(i));

                mBundle.putString("dosage",dosage.get(i));

                mBundle.putString("inventory",inventory.get(i));

                intent.putExtras(mBundle);

                startActivity(intent);
            }
        });

        getFireBaseDrugs();
    }


    private void getFireBaseDrugs() {

        drugsReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("here", "yes");
                            getDrugDetails((Map<String, Object>) dataSnapshot.getValue());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    private void getDrugDetails(Map<String, Object> value) {

        for (Map.Entry<String, Object> entry : value.entrySet()) {
            Map drug = (Map) entry.getValue();


            Log.i("drugname",entry.getKey());

            Log.i("drugdosage",String.valueOf(drug.get("dosage")));

            drugs.add(entry.getKey());

            activeIngredients.add(String.valueOf(drug.get("active_ingredient")));

            dosage.add((String.valueOf(drug.get("dosage"))));

            inventory.add(String.valueOf(drug.get("inventory")));


        }

        drugsAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,drugs);

        drugsListView.setAdapter(drugsAdapter);

    }

    public void addDrug(View view){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_search,menu);

        MenuItem item = menu.findItem(R.id.search_bar);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                drugsAdapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
