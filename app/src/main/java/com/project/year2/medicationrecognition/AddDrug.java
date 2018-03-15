package com.project.year2.medicationrecognition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDrug extends AppCompatActivity {

    EditText ingredientEditText;
    EditText dosageEditText;
    EditText nameEditText;
    EditText inventoryEditText;

    String drugName;
    String drugIngredient;
    String drugDosage;
    String drugInventory;

    DatabaseReference myRef =  FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);

        initilailizeViews();


    }

    // get the text from the editTexts when the user press the ADD button
   public void getViewsText(View view) {

        drugName = nameEditText.getText().toString();

        drugIngredient = ingredientEditText.getText().toString();

        drugDosage = dosageEditText.getText().toString();

        drugInventory = inventoryEditText.getText().toString();

        saveInFireBase();
    }

    //save drug in firebase
    private void saveInFireBase() {

        NewDrug newDrug = new NewDrug(drugIngredient,drugDosage,drugInventory);

        myRef.child("Drugs").child(drugName).setValue(newDrug);


    }

    private void initilailizeViews() {

        ingredientEditText = (EditText) findViewById(R.id.add_drugIngredinet);

        dosageEditText = (EditText) findViewById(R.id.add_drugDosage);

        nameEditText = (EditText) findViewById(R.id.add_drugName);

        inventoryEditText = (EditText) findViewById(R.id.add_drugInventory);

    }

}
