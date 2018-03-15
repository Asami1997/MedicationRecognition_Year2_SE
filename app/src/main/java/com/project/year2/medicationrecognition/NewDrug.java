package com.project.year2.medicationrecognition;

/**
 * Created by asami on 3/15/2018.
 */

public class NewDrug {


    public String active_ingredient;

    public String dosage;

    public  String inventory;


    public  NewDrug(){

    }


    public NewDrug(String active_ingredient, String dosage, String inventory) {
        this.active_ingredient = active_ingredient;
        this.dosage = dosage;
        this.inventory = inventory;
    }
}
