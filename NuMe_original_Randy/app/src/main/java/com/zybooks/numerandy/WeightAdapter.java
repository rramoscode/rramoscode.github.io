package com.zybooks.numerandy;

import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    private List<WeightEntry> mWeightList;
    private SQLiteDatabase mDb;

    public WeightAdapter(List<WeightEntry> weightList, SQLiteDatabase db) {
        this.mWeightList = weightList;
        this.mDb = db;
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        WeightEntry currentEntry = mWeightList.get(position);
        holder.mDateTextView.setText("Date: " + currentEntry.getDate());
        holder.mWeightTextView.setText(String.format("Weight: %.2f lbs", currentEntry.getWeight()));

        // Handle delete button click
        holder.mDeleteButton.setOnClickListener(v -> {
            // Remove the item from the database and the list
            deleteWeightEntry(position);
        });
    }

    @Override
    public int getItemCount() {
        return mWeightList.size();
    }

    // ViewHolder class for RecyclerView
    public static class WeightViewHolder extends RecyclerView.ViewHolder {

        public TextView mDateTextView, mWeightTextView;
        public Button mDeleteButton;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            mDateTextView = itemView.findViewById(R.id.textViewDate);
            mWeightTextView = itemView.findViewById(R.id.textViewWeight);
            mDeleteButton = itemView.findViewById(R.id.buttonDeleteRow);
        }
    }

    // Method to delete a weight entry from the database and the RecyclerView
    private void deleteWeightEntry(int position) {
        WeightEntry entry = mWeightList.get(position);
        String date = entry.getDate();
        double weight = entry.getWeight();

        // Delete the entry from the database
        mDb.delete("daily_weights", "date = ? AND weight = ?", new String[]{date, String.valueOf(weight)});

        // Remove the item from the list and notify the RecyclerView
        mWeightList.remove(position);
        notifyItemRemoved(position);
    }
}
