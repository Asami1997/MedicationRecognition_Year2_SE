package com.project.year2.medicationrecognition;

import java.util.ArrayList;

/**
 * Created by asami on 2/2/2018.
 */

public class TransactionObject {

    private String NAME ;
    private String  AGE;
    private String PHONE;
    private String BIRTHDATE;
    private ArrayList<String>rxArrayList;

    public TransactionObject(String name , String age , String phone , String birthDate
    , ArrayList<String> rX) {

           this.NAME = name;
           this.AGE = age;
           this.PHONE = phone;
           this.BIRTHDATE = birthDate;
           this.rxArrayList = rX;

    }
}
