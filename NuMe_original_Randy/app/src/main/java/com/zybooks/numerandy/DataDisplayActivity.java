package com.zybooks.numerandy;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
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

        /*
        * as part of Software Engineering and Design add Text Watcher
        * this will implement real-time goal validation feedback when user
        * enters a goal weight.
        */
        mGoalWeightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        double goal = Double.parseDouble(s.toString());
                        Cursor cursor = mDb.rawQuery("SELECT weight FROM daily_weights ORDER BY date DESC LIMIT 1", null);
                        if (cursor.moveToFirst()) {
                            double latestWeight = cursor.getDouble(0);
                            String message;
                            if (goal >= latestWeight) {
                               message =  ("Goal is above or equal to current weight.");
                            } else if (goal < latestWeight && goal >= latestWeight - 5) {
                                message = "Moderate goal.";
                            } else {
                                message = "Aggressive goal.";
                            }
                            Toast.makeText(DataDisplayActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }

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

    // added with database enhancement updateGoalAchievementDate ()
    private void updateGoalAchievementDate() {
        ContentValues values = new ContentValues();
        values.put("achieved_date", LocalDate.now().toString());
        mDb.update("goal_history", values, "achieved_date IS NULL", null);
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

        //updated as part of algorithms and data structures
        values.put("date", date.isEmpty() ? LocalDate.now().toString() : date);
        long newRowId = mDb.insert("daily_weights", null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Weight added successfully!", Toast.LENGTH_SHORT).show();
            mWeightInput.setText("");
            mDateInput.setText("");
            checkIfGoalReached(weight);
            calculateWeeklyChange(weight); //added as part of algorithms and data structures
            loadWeightData();
        } else {
            Toast.makeText(this, "Error adding weight", Toast.LENGTH_SHORT).show();
        }
    }

        /* as part of algorithms and data structures add calculateWeeklyChange ()
        * This method will filter the entries from the past 7 days, calculate the average,
        * and display a message comparing the current weight to the weekly average.
         */

        private void calculateWeeklyChange(double currentWeight) {
            String sevenDaysAgo = LocalDate.now().minusDays(7).toString();
            double total = 0.0;
            int count = 0;
            Cursor cursor = mDb.query("daily_weights", new String[]{"weight"}, "date >= ?", new String[]{sevenDaysAgo}, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    total += cursor.getDouble(0);
                    count++;
                }
                cursor.close();
            }
            if (count > 0) {
                double avg = total / count;
                double diff = currentWeight - avg;
                String feedback =  String.format("Change in last 7 days: %.2f lbs", diff);
                Toast.makeText(this, feedback, Toast.LENGTH_LONG).show();
            }
        }
    }
