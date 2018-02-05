package com.project.year2.medicationrecognition;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by asami on 2/5/2018.
 */

/*
   RecyclerView.Adapter -> Binds The Data to the View
   RecyclerView.ViewHolder -> Holds The View
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context mCtx;
    private List<PharamTransaction> transactionObjects;

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
    public void onBindViewHolder(TransactionViewHolder holder, int position) {

        PharamTransaction pharamTransaction = transactionObjects.get(position);

        holder.nameTextView.setText(pharamTransaction.getName());

        holder.emailTextView.setText(pharamTransaction.getEmail());
    }

    @Override
    public int getItemCount() {
        return transactionObjects.size();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder{

        //create the UI elements for the card

        TextView nameTextView;
        TextView emailTextView;

        public TransactionViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.p_nameTextView);

            emailTextView = itemView.findViewById(R.id.p_emailTextView);

        }
    }

}
