package com.project.year2.medicationrecognition;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    DatabaseHelper myDb;
    EditText nameText,ingredientText,inventoryText,idText;
    Button insertButton,displayButton,updateButton,deleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);
        //instances of the text box
        nameText = (EditText)findViewById(R.id.editTextName);
        ingredientText = (EditText)findViewById(R.id.editTextIngredient);
        inventoryText = (EditText)findViewById(R.id.editTextInventory);
        idText = (EditText)findViewById(R.id.editTextID);

        insertButton = (Button)findViewById(R.id.btnInsert);
        displayButton = (Button)findViewById(R.id.BtnViewData);
        updateButton = (Button)findViewById(R.id.btnUpdate);
        deleteButton = (Button)findViewById(R.id.btnDelete);

        addData();
        //add the function in the main
       displayData();
        updateData();
        deleteData();

    }

    public void deleteData()
    {
        deleteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer deletedRow = myDb.deleteData(idText.getText().toString());

                        if (deletedRow > 0)
                        {
                            Toast.makeText(MainActivity.this, "Data Deleted", Toast.LENGTH_LONG).show();

                        }else
                            Toast.makeText(MainActivity.this, "Data not Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    //update the data in the table
    public void updateData()
    {
        updateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isUpdated = myDb.updateData(idText.getText().toString(),
                                nameText.getText().toString(),ingredientText.getText().toString(),inventoryText.getText().toString());

                        if (isUpdated == true)
                        {
                            Toast.makeText(MainActivity.this, "Data Updated", Toast.LENGTH_LONG).show();

                        }else
                            Toast.makeText(MainActivity.this, "Data not Updated", Toast.LENGTH_SHORT).show();

                    }
                }
        );
    }

//    //Create to display the values function
    public void displayData()
    {
        displayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Cursor resource = myDb.displayData();
                //now we will check resources
                if (resource.getCount()==0)
                {
                    showMessage("Error","Nothing found");
                }else
                {
                    StringBuffer buffer = new StringBuffer();
                    while (resource.moveToNext())
                    {
                        buffer.append("Medication ID :" + resource.getString(0)+ "\n");
                        buffer.append("Name          :" + resource.getString(1)+ "\n");
                        buffer.append("Ingredient    :" + resource.getString(2)+ "\n");
                        buffer.append("Inventory     :" + resource.getString(3)+ "\n\n");

                    }
                    showMessage("Medication record",buffer.toString());
                }
            }

        });
    }
        //Now create the showDialog method
        public void showMessage(String title,String Message)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(Message);
            builder.show();
        }

        public void addData()
        {
            insertButton.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            boolean isInserted = myDb.insertData(nameText.getText().toString(),ingredientText.getText().toString(),inventoryText.getText().toString());

                            if(isInserted)
                                Toast.makeText(MainActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(MainActivity.this, "Data not Inserted", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
}
