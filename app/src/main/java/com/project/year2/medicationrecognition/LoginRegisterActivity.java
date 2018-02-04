package com.project.year2.medicationrecognition;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button registerButton;
    private Button loginButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private String email="";
    private String password ="";
    private boolean emailResult;
    private int operation;
    private String userType;
    private String type;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef =  FirebaseDatabase.getInstance().getReference();
    public static  final String TAG = "LoginRegisterActivity";
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        mAuth = FirebaseAuth.getInstance();
        //remove action bar
        getSupportActionBar().hide();

        //reference to dataBase;

        registerButton = (Button) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.signInButton);

        loginButton.setOnClickListener(this);

        emailEditText =  (EditText) findViewById(R.id.emailEditText);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        //default user type
        userType = "User";

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.userRadio:
                        radioButton = (RadioButton) findViewById(R.id.userRadio);
                         userType = radioButton.getText().toString();
                         Log.i("selected",userType);
                        break;
                    case R.id.pharmacistRadio:
                        radioButton  = (RadioButton) findViewById(R.id.pharmacistRadio);
                        userType = radioButton.getText().toString();
                        Log.i("selected",userType);
                        break;
                    case R.id.adminRadio:
                        radioButton = (RadioButton) findViewById(R.id.adminRadio);
                        userType = radioButton.getText().toString();
                        Log.i("selected",userType);
                        break;
                }
            }
        });

        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    //User is Signed In
                    Log.d(TAG,"onAuthStateStateChanged:signed_in:"+ user.getUid());
                }else{
                    //User is Signed Out
                    Log.d(TAG,"onAuthStateStateChanged:signed_out:");
                }
            }
        };

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
                    if(password.length()<6){
                        Toast.makeText(this, "PassWord Should be At least 6 characters", Toast.LENGTH_SHORT).show();
                    }else{
                        if (operation == 1){
                            //register
                            Log.i("Register","yes");
                            //create new user
                            registerUser();

                        }else {
                            //Sign in
                            loginUser();
                            Log.i("Login","yes");
                        }
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

    private void loginUser() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginRegisterActivity.this, "Successfully Signed In ", Toast.LENGTH_SHORT).show();
                            //get user type
                             validateUserType(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginRegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }

    private void validateUserType(FirebaseUser fUser) {

        myRef.child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserNew user = dataSnapshot.getValue(UserNew.class);
                if(user!=null)
                {
                    type = user.userType;
                    Log.i("type",type);
                    Log.i("UserType",user.userType);
                    if(type != null){
                        if(!type.equals(userType)){

                            Toast.makeText(LoginRegisterActivity.this, "You Have No Access To "+
                                    userType +"side", Toast.LENGTH_SHORT).show();
                        }else{
                            goToActivity(type);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void goToActivity(String type) {

        switch (type){

            case "User":
                Intent intent = new Intent(getApplicationContext(),UserOCR.class);
                startActivity(intent);
                break;
            case "Pharmacist":
                //pharmacist activity
                break;
            case "Admin":
                //Admin Activity
                break;
        }

    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            //save data to database
                            UserNew user1 = new UserNew(email,userType);
                            myRef.child("Users").child(user.getUid()).setValue(user1);
                            goToActivity(userType);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginRegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListner);

        }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListner!=null){
            mAuth.removeAuthStateListener(mAuthStateListner);
        }
    }
}
