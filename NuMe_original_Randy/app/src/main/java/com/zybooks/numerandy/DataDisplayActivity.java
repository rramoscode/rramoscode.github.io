package com.zybooks.numerandy;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DataDisplayActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private WeightAdapter mWeightAdapter;
    private SQLiteDatabase mDb;
    private EditText mWeightInput, mDateInput, mGoalWeightInput;
    private Button mAddWeightButton, mSetGoalWeightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        mRecyclerView = findViewById(R.id.dataGrid);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mWeightInput = findViewById(R.id.editTextWeightInput);
        mDateInput = findViewById(R.id.editTextDateInput);
        mGoalWeightInput = findViewById(R.id.editTextGoalWeight);
        mAddWeightButton = findViewById(R.id.buttonAddWeight);
        mSetGoalWeightButton = findViewById(R.id.buttonSetGoalWeight);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        mDb = dbHelper.getWritableDatabase();

        // Load existing data from the database
        loadWeightData();

        // Add a new weight entry when the Add Weight button is clicked
        mAddWeightButton.setOnClickListener(v -> addWeightEntry());

        // Save the goal weight when Set Goal button is clicked
        mSetGoalWeightButton.setOnClickListener(v -> {
            String goalWeightString = mGoalWeightInput.getText().toString();
            if (!goalWeightString.isEmpty()) {
                double goalWeight = Double.parseDouble(goalWeightString);
                saveGoalWeight(goalWeight);
            }
        });
    }

    private void saveGoalWeight(double goalWeight) {
        Cursor cursor = mDb.query("goal_weight", null, null, null, null, null, null);
        ContentValues values = new ContentValues();
        values.put("goal_weight", goalWeight);

        if (cursor != null && cursor.getCount() > 0) {
            // Update the goal weight if it already exists
            mDb.update("goal_weight", values, null, null);
            Toast.makeText(this, "Goal weight updated!", Toast.LENGTH_SHORT).show();
        } else {
            // Insert the goal weight if it's the first time
            mDb.insert("goal_weight", null, values);
            Toast.makeText(this, "Goal weight set!", Toast.LENGTH_SHORT).show();
        }
        if (cursor != null) cursor.close();
    }

    // Check if the user's daily weight reaches the goal weight
    private void checkIfGoalReached(double currentWeight) {
        Cursor cursor = mDb.query("goal_weight", new String[]{"goal_weight"}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            double goalWeight = cursor.getDouble(cursor.getColumnIndex("goal_weight"));
            if (currentWeight <= goalWeight) {
                // Send SMS when the goal is reached
                sendSMSNotification();
            }
            cursor.close();
        }
    }

    private void sendSMSNotification() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            return;
        }

        // Send SMS as permission has been granted
        String phoneNumber = "1234567890";
        String message = "Congratulations! You've reached your goal weight!";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
    }


    private void loadWeightData() {
        List<WeightEntry> weightList = new ArrayList<>();

        Cursor cursor = mDb.query("daily_weights", null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                double weight = cursor.getDouble(cursor.getColumnIndex("weight"));
                weightList.add(new WeightEntry(date, weight));
            }
            cursor.close();
        }

        mWeightAdapter = new WeightAdapter(weightList, mDb);
        mRecyclerView.setAdapter(mWeightAdapter);
    }

    private void addWeightEntry() {
        String weightString = mWeightInput.getText().toString();
        String date = mDateInput.getText().toString();

        if (weightString.isEmpty()) {
            Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show();
            return;
        }

        double weight = Double.parseDouble(weightString);

        ContentValues values = new ContentValues();
        values.put("weight", weight);
        values.put("date", date.isEmpty() ? "Unknown" : date);
        long newRowId = mDb.insert("daily_weights", null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Weight added successfully!", Toast.LENGTH_SHORT).show();

            mWeightInput.setText("");
            mDateInput.setText("");

            checkIfGoalReached(weight);
            loadWeightData();
        } else {
            Toast.makeText(this, "Error adding weight", Toast.LENGTH_SHORT).show();
        }
    }
}
