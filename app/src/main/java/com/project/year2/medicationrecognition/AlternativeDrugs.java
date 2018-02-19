package com.project.year2.medicationrecognition;

import android.util.Log;

import com.google.common.collect.ArrayListMultimap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.common.collect.Multimap;

/**
 * Created by asami on 2/19/2018.
 *
 * This class is for finding drug alternatives
 */

public class AlternativeDrugs {

    private String drugs[];
    public Multimap<String, String> alterantives = ArrayListMultimap.create();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference drugsReference = FirebaseDatabase.getInstance().getReference().child("Drugs");
    boolean doneFindingAlternatives = false;
    TransactionDetails transactionDetails = new TransactionDetails();
    public String details= "";
    public AlternativeDrugs(String[] drugs) {

        this.drugs = drugs;
        getActiveIngredints();
    }

    public void getActiveIngredints() {

        for (final String drug : drugs) {

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


    }

    private void getAlternativeForDrug(final String drug, final String active_ingredient) {

        drugsReference
                .orderByChild("active_ingredient").equalTo(active_ingredient)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child :dataSnapshot.getChildren()){

                            if(!(child.getKey().replaceAll("\\s+","").
                                    equalsIgnoreCase(drug.toLowerCase().replaceAll("\\s+","")))){

                                Log.i("drug",drug.toLowerCase());

                                String alternativeActiveIngredient = child.child("active_ingredient").getValue().toString();
                                String alternative = child.getKey();
                                Log.i("alternativeActiveingnt", alternativeActiveIngredient);
                                if (alternativeActiveIngredient.equals(active_ingredient)) {
                                    Log.i("alternative",child.getKey());
                                    alterantives.put(drug,alternative);
                                    details+="\n" + drug + ": " + alternative;

                                }
                            }
                        }
                       // transactionDetails.addToAlternativesLayout();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

}
