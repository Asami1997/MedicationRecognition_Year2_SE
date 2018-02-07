package com.project.year2.medicationrecognition;

/**
 * Created by asami on 2/5/2018.
 */

public class PharamTransaction {

    public String Email;
    private String Name;

    public PharamTransaction(String email, String name) {
        this.Email = email;
        this.Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public String getName() {
        return Name;
    }
}
