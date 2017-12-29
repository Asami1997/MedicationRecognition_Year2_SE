package com.project.year2.medicationrecognition;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nixon on 5/11/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "medication.db";
    public static final String TABLE_NAME = "medication_table";
    public static final String COLUMN_1 = "ID";
    public static final String COLUMN_2 = "NAME";
    public static final String COLUMN_3 = "INGREDIENT";
    public static final String COLUMN_4 = "INVENTORY";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }
    private static final String Create_Table_medication = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_2 + " TEXT," + COLUMN_3 + " TEXT," + COLUMN_4 +" INTEGER);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Table_medication);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public boolean insertData(String name,String ingredient,String inventory)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_2,name);
        contentValues.put(COLUMN_3,ingredient);
        contentValues.put(COLUMN_4,inventory);

        long result = db.insert(TABLE_NAME,null,contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }


    public Cursor displayData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id,String name,String ingredient,String inventory )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_1,id);
        contentValues.put(COLUMN_2,name);
        contentValues.put(COLUMN_3,ingredient); 
        contentValues.put(COLUMN_4,inventory);

        db.update(TABLE_NAME,contentValues,"ID = ?", new String[] { id });
        return true;

    }

    public Integer deleteData(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
         return db.delete(TABLE_NAME,"ID = ?",new String[] {id});

    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query,null);
    }



}
