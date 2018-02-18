package com.project.year2.medicationrecognition;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by asami on 2/5/2018.
 */

/*
   RecyclerView.Adapter -> Binds The Data to the View
   RecyclerView.ViewHolder -> Holds The View
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    //context deines where are you currently at , like which activity
    private Context mCtx;
    private List<PharamTransaction> transactionObjects;
    private PharmacistActivity pharmacistActivity = new PharmacistActivity();

    public TransactionAdapter(Context mCtx, List<PharamTransaction> transactionObjects) {
        this.mCtx = mCtx;
        this.transactionObjects = transactionObjects;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);

        View view = layoutInflater.inflate(R.layout.list_layout,null);

        TransactionViewHolder transactionViewHolder = new TransactionViewHolder(view);

        return transactionViewHolder;
    }

    //binds data to the view holder
    @Override
    public void onBindViewHolder(TransactionViewHolder holder, final int position) {

        final PharamTransaction pharamTransaction = transactionObjects.get(position);

        holder.nameTextView.setText(pharamTransaction.getName());

        holder.emailTextView.setText(pharamTransaction.getEmail());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mCtx, pharamTransaction.getName(), Toast.LENGTH_SHORT).show();

                //start transaction details activity with passing data to it
                Intent intent = new Intent(mCtx, TransactionDetails.class);

                intent.putExtra("transactionObject",pharmacistActivity.transactionObjects.get(position));

                mCtx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return transactionObjects.size();
    }

    //create the UI elements for the card
    class TransactionViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;
        TextView emailTextView;
        LinearLayout linearLayout;

        public TransactionViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.p_nameTextView);

            emailTextView = itemView.findViewById(R.id.p_emailTextView);

            linearLayout = itemView.findViewById(R.id.detailsLinerLayout);

        }
    }

}
