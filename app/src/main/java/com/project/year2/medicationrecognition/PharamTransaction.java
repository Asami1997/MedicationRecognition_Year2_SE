package com.project.year2.medicationrecognition;


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
