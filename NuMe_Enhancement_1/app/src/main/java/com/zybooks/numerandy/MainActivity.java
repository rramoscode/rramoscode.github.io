package com.zybooks.numerandy;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText mUsernameInput, mPasswordInput;
    private Button mLoginButton, mCreateAccountButton;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsernameInput = findViewById(R.id.editTextUsername);
        mPasswordInput = findViewById(R.id.editTextPassword);
        mLoginButton = findViewById(R.id.buttonLogin);
        mCreateAccountButton = findViewById(R.id.buttonCreateAccount);

        // Initialize database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mLoginButton.setOnClickListener(v -> {
            String username = mUsernameInput.getText().toString();
            String password = mPasswordInput.getText().toString();

            if (checkUserCredentials(username, password)) {
                Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                // Navigate to the next screen
                Intent intent = new Intent(MainActivity.this, DataDisplayActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
            }
        });

        mCreateAccountButton.setOnClickListener(v -> {
            String username = mUsernameInput.getText().toString();
            String password = mPasswordInput.getText().toString();

            if (createNewAccount(username, password)) {
                Toast.makeText(MainActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to check if user credentials are valid
    private boolean checkUserCredentials(String username, String password) {
        Cursor cursor = mDb.query("users", new String[]{"username", "password"},
                "username = ? AND password = ?", new String[]{username, password},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Method to create a new account
    private boolean createNewAccount(String username, String password) {
        // Check if user already exists
        Cursor cursor = mDb.query("users", new String[]{"username"}, "username = ?",
                new String[]{username}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return false; // Username already exists
        }
        cursor.close();

        // Insert new user
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        mDb.insert("users", null, values);
        return true;
    }
}