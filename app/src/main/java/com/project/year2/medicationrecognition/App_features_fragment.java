package com.project.year2.medicationrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class App_features_fragment extends Fragment {


    Button tryItButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.features_fragment, container, false);


         tryItButton =  view.findViewById(R.id.goToAppButton);

           tryItButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                   startMain();
               }
           });
        return view;
    }


    public void startMain(){

        Intent intent = new Intent(getContext(),MainActivity.class);

        startActivity(intent);
    }


}
