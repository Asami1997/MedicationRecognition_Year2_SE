package com.project.year2.medicationrecognition;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AdminDetails extends AppCompatActivity {

    TextView name;
    TextView activeIngredient;
    TextView dosage;
    TextView inventory;
    String drugName;
    String drugIngredient;
    String drugDosage;
    String drugInventory;
    Bundle drugDetails;
    DatabaseReference drugsRef;
    AlertDialog.Builder editAlert ;
    DatabaseReference reqRef;
    //will contain new value from user when user edits ingredient , dosage , or inventory
    String value = "";
    Hashtable<String,Integer> test;
    TextView frequency;

    int [] values ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_details);

        test = new Hashtable<>();

        drugDetails = getIntent().getExtras();

        setDrugDetails(drugDetails.get("drugName").toString(),drugDetails.get("dosage").toString(),
                drugDetails.get("inventory").toString(),drugDetails.get("activeIngredient").toString());

        drugsRef = FirebaseDatabase.getInstance().getReference().child("Drugs");


        editAlert =  new AlertDialog.Builder(this);

        reqRef = FirebaseDatabase.getInstance().getReference().child("Requests");

        initializeViews();

        addDataToTextViews();

        getRequests();

    }

    private void getRequests() {

        final ArrayList<String> months = new ArrayList<>();
        final ArrayList<Integer> monthValue = new ArrayList<>();

        reqRef.child(drugName).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){

                    Toast.makeText(AdminDetails.this, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();

                    frequency.setText("Frequency: " + dataSnapshot.getValue().toString());
                }else{

                    frequency.setText("Frequency: 0" );

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setDrugDetails(String name,String dosage,String inventory,String ingredinet){

        drugName = name;
        drugDosage = dosage;
        drugInventory = inventory;
        drugIngredient = ingredinet;

    }

    private void addDataToTextViews() {

        name.append(" " + drugName);

        activeIngredient.append(" " + drugIngredient);

        dosage.append(" " + drugDosage);

        inventory.append(" " + drugInventory);
    }

    private void initializeViews() {

        name = (TextView) findViewById(R.id.a_drugName);

        activeIngredient = (TextView) findViewById(R.id.a_activeIngredient);

        dosage = (TextView) findViewById(R.id.a_dosage);

        inventory = (TextView) findViewById(R.id.a_inventory);

        frequency = (TextView) findViewById(R.id.frequency);
    }

    //this function delete the drug from the database
    public void deleteDrug(){

        drugsRef.child(drugName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AdminDetails.this, "Deleted From Database", Toast.LENGTH_LONG).show();

                //return to previous activity

                Intent intent = new Intent(getApplicationContext(),AdminActivity.class);

                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.deleteIcon) {

            displayAlertDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayAlertDialog() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminDetails.this);
        builder1.setMessage("Are you sure you want to delete this entry ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        deleteDrug();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void editData(View view, final String value){

        switch (view.getId()){

            case R.id.ingredientButton:


                Toast.makeText(this,value, Toast.LENGTH_SHORT).show();

                 DatabaseReference activeIngredientRef =  FirebaseDatabase.getInstance().getReference().child("Drugs").child(drugName)
                        .child("active_ingredient");

                 activeIngredientRef.setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {

                         Toast.makeText(AdminDetails.this, "Value Changed Successfully", Toast.LENGTH_SHORT).show();

                         activeIngredient.setText("Active Ingredient : " + value);
                     }
                 });

                break;
            case R.id.dosageButton:

                //edit
                DatabaseReference dosaageRef =  FirebaseDatabase.getInstance().getReference().child("Drugs").child(drugName)
                        .child("dosage");

                dosaageRef.setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(AdminDetails.this, "Value Changed Successfully", Toast.LENGTH_SHORT).show();

                        activeIngredient.setText("Dosage : " + value);

                    }
                });


                break;
            case R.id.inventoryButton:

                DatabaseReference inventoryRef =  FirebaseDatabase.getInstance().getReference().child("Drugs").child(drugName)
                        .child("inventory");

                inventoryRef.setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(AdminDetails.this, "Value Changed Successfully", Toast.LENGTH_SHORT).show();

                        activeIngredient.setText("Inventory : " + value);

                    }
                });

                //edit
                break;
        }

    }

     public void displayEditAlert(final View view) {

        final EditText edittext = new EditText(getApplicationContext());
        edittext.setTextColor(Color.parseColor("#000000"));
        editAlert.setTitle("Enter New Value");
        editAlert.setView(edittext);

        editAlert.setPositiveButton("Confirm Changes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                value = edittext.getText().toString();

                //passing the "view" object to know whih button was pressed.Which button triggered function displayEditAlert
                editData(view,value);
            }
        });

        editAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //
            }
        });

        editAlert.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(),AdminActivity.class);
        startActivity(intent);
    }
}
