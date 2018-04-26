package com.project.year2.medicationrecognition;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;



public class TransactionObject implements Serializable {

    public String NAME ;
    public String  AGE;
    public String PHONE;
    public String BIRTHDATE;
    public String GENDER;
    public String DRUGS;
    public String EMAIL;

    public TransactionObject(){

    }
    public TransactionObject(String name , String age , String phone , String birthDate
    ,String gender,String email, String drugs) {

           this.NAME = name;
           this.AGE = age;
           this.PHONE = phone;
           this.BIRTHDATE = birthDate;
           this.GENDER = gender;
           this.DRUGS = drugs;
           this.EMAIL = email;
    }
}
