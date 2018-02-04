package com.project.year2.medicationrecognition;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by asami on 2/2/2018.
 */

public class TransactionObject {

    public String NAME ;
    public String  AGE;
    public String PHONE;
    public String BIRTHDATE;
    public String GENDER;
    public String DRUGS;

    public TransactionObject(){

    }
    public TransactionObject(String name , String age , String phone , String birthDate
    ,String gender, String drugs) {

           this.NAME = name;
           this.AGE = age;
           this.PHONE = phone;
           this.BIRTHDATE = birthDate;
           this.GENDER = gender;
           this.DRUGS = drugs;
        Log.i("drugscaptial",DRUGS);
        Log.i("drugssmall",drugs);

    }
}
