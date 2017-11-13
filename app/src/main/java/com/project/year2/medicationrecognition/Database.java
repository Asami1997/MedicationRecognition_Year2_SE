package com.project.year2.medicationrecognition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by asami on 11/12/2017.
 */

public class Database {

    Context context;

    public Database() {

        //creating a new database or opening an existing one with mode private

        SQLiteDatabase medicationDatabase = context.openOrCreateDatabase("Medications", Context.MODE_PRIVATE, null);

        //creating a table in the database with 3 columns

        //name and activeIngredient and inventory are the columns

        medicationDatabase.execSQL("CREATE TABLE IF NOT EXISTS medications (name VARCHAR , activeIngredient VARCHAR , inventory INT (3))");

        //inserting medications into table

        medicationDatabase.execSQL("INSERT INTO Medications (name, activeIngredient , inventory) values ('Tritace' ,'ramipri' ,90)");

        medicationDatabase.execSQL("INSERT INTO Medications (name,activeIngredient,inventory) values ('panadol extra' ,'Paracetamol',40)");

        medicationDatabase.execSQL("INSERT INTO Medications (name ,activeIngredient,inventory) values ('Tylenol' ,'Paracetamol' ,0)");

    }


  public void checkForAvailabilty(String medicine){


        
  }

  public  void getActiveIngredient(String medicine){


  }

  public void getAlternativeMedicine(String activeIngredient){


  }

}
