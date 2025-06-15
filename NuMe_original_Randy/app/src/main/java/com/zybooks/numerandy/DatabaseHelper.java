package com.zybooks.numerandy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weight_tracker.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create the users table with username and password columns
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");

        // create weight tracking table
        db.execSQL("CREATE TABLE daily_weights (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, date TEXT, weight REAL)");

        // create a table to store the goal weight
        db.execSQL("CREATE TABLE goal_weight (id INTEGER  PRIMARY KEY AUTOINCREMENT, user_id INTEGER, goal_weight REAL)");

        /*
         * added as part of the Databases enhancement will help to
         * track when goal is achieved
         * log every new goal weight set
         */
        //new goal_history table ()
        db.execSQL("CREATE TABLE goal_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "goal_weight REAL, " +
                "set_date TEXT, "+
                "achieved_date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS daily_weights");
        db.execSQL("DROP TABLE IF EXISTS goal_weight");
        db.execSQL("DROP TABLE IF EXISTS goal_history");
        onCreate(db);
    }
}
