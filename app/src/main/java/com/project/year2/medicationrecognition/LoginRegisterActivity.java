package com.project.year2.medicationrecognition;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String radioSelected;
    private Button registerButton;
    private Button loginButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private String email="";
    private String password ="";
    private boolean emailResult;
    private int operation;
    private DatabaseReference databaseReference;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        //remove action bar
        getSupportActionBar().hide();

        registerButton = (Button) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.signInButton);

        loginButton.setOnClickListener(this);

        emailEditText =  (EditText) findViewById(R.id.emailEditText);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.userRadio:
                        radioButton = (RadioButton) findViewById(R.id.userRadio);
                       radioSelected = radioButton.getText().toString();
                        break;
                    case R.id.pharmacistRadio:
                        radioButton  = (RadioButton) findViewById(R.id.userRadio);
                        radioSelected = radioButton.getText().toString();
                        break;
                    case R.id.adminRadio:
                        radioButton = (RadioButton) findViewById(R.id.userRadio);
                        radioSelected = radioButton.getText().toString();
                        break;
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registerButton:
                operation = 1;
                getEmailPassword();

                break;

            case R.id.signInButton :
                operation = 0;
                getEmailPassword();
                break;
        }
    }

    private boolean checkEmail() {
        String regex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return false;
        }
         return true;
    }

    public void getEmailPassword(){

        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        if(!email.isEmpty()){
            emailResult = checkEmail();
            if(emailResult){
                if(!password.isEmpty()){
                    //register/Sign in

                    if (operation == 1){
                        //register
                        Log.i("Register","yes");
                    }else {
                        //Sign in
                        Log.i("Login","yes");
                    }
                }else{
                    Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Please Enter A Valid Email Address", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please Enter Email Address", Toast.LENGTH_SHORT).show();
        }
    }
}
