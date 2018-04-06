package com.project.year2.medicationrecognition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner;
import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

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
    private ArrayList<String> selectedAlternatives;
    TransactionObject transactionObject;
    String userId;
    ArrayList<String> alternatives ;
    //will contain all the drugs in the transaction

    private String drugs[];
  //  public Multimap<String, String> alterantives = ArrayListMultimap.create();
   // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
  //  boolean doneFindingAlternatives = false;
  //  TransactionDetails transactionDetails = new TransactionDetails();
    public String details= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        Intent intent = getIntent();

        userId = intent.getStringExtra("id");

        //contains all the transaction details
        transactionObject = (TransactionObject) intent.getSerializableExtra("transactionObject");

        drugsReference = FirebaseDatabase.getInstance().getReference().child("Drugs");

        alternatives = new ArrayList<String>();

        selectedAlternatives = new ArrayList<String>();

        outOfStockDrugs = new ArrayList<>();

        //initializing views

         intializeViews();

         addtoTransactionLayout();

         saveTransactionDrugsToArray();

         checkInventory();
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

                    if(inventory != null){

                        if(inventory == 0){
                            outOfStockDrugs.add(drug.toLowerCase());
                            addToInventoryLayout(drug);
                            Log.i("outofstock",outOfStockDrugs.toString());
                            getActiveIngredints(drug);

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }

        if(outOfStockDrugs.size() == 0){

            inventoryDetailsTextView.setText("All Drugs Are Available");
        }
    }

    public void getActiveIngredints(final String drug) {

            Log.i("drugdss",drug);

            DatabaseReference singleDrug = drugsReference.child(drug.toLowerCase().trim());
            singleDrug.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String active_ingredient = (String) dataSnapshot.child("active_ingredient").getValue();

                    Log.i("active_ingredient",String.valueOf(active_ingredient));

                    getAlternativeForDrug(drug,active_ingredient);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    private void getAlternativeForDrug(final String drug, final String active_ingredient) {

        drugsReference
                .orderByChild("active_ingredient").equalTo(active_ingredient)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child :dataSnapshot.getChildren()){

                            if(!(child.getKey().replaceAll("\\s+","").
                                    equalsIgnoreCase(drug.toLowerCase().replaceAll("\\s+",""))) &&
                                    !child.child("inventory").getValue().toString().equals("0")){

                                Log.i("drug",drug.toLowerCase());

                                String alternativeActiveIngredient = child.child("active_ingredient").getValue().toString();
                                String alternative = child.getKey();
                                Log.i("alternativeActiveingnt", alternativeActiveIngredient);
                                if (alternativeActiveIngredient.equals(active_ingredient)) {
                                    Log.i("alternative",child.getKey());

                                    String tempString = drug + ": " + alternative;

                                    if(!alternatives.contains(tempString)){

                                        details+=tempString;

                                        alternatives.add(tempString);

                                    }
                                }
                            }
                        }

                        addAlternativesToSpinner();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    private void addAlternativesToSpinner() {


        final MultiSelectSpinner multiSelectSpinner = (MultiSelectSpinner) findViewById(R.id.multiselectSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, alternatives);

        multiSelectSpinner
                .setListAdapter(adapter)

                .setListener(new MultiSelectSpinner.MultiSpinnerListener() {
                    @Override
                    public void onItemsSelected(boolean[] selected) {

                        for(int i = 0 ; i< selected.length;i++){

                            Log.i("selected",String.valueOf(selected[i]));

                            if(selected[i]){

                                selectedAlternatives.add(alternatives.get(i));

                                Log.i("selectedalternative",selectedAlternatives.get(i));
                            }

                        }


                    }
                })
                .setAllCheckedText("All Selected")
                .setAllUncheckedText("none selected")
                .setSelectAll(true)
                .setTitle("Alternatives")
                .setMinSelectedItems(1);

    }





    // will begin the process of notifying the user that the transaction has been approved
    public void approveTransaction(View view){

        final DatabaseReference notifications = FirebaseDatabase.getInstance().getReference().child("Notifications");

        notifications.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                    notifications.child(userId).child("notification").setValue("yes");
                    if(selectedAlternatives.toString().length() != 0){

                        //remove brackets from string us9ing regex

                        String modifedString  = selectedAlternatives.toString().replaceAll("\\p{P}","");

                        notifications.child(userId).child("alternative").setValue(modifedString);

                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(this, "Transaction Approved", Toast.LENGTH_LONG).show();

        //remove transaction after its approval

        removeTransaction();

    }

    private void removeTransaction() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Transactions").child(userId);

        ref.removeValue();

    }

}