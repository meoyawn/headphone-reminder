package com.example.adelniz.expreriments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "app.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE test_table\n" +
                "(col1 INTEGER NOT NULL,\n" +
                "col2 CHAR(25),\n" +
                "col3 VARCHAR(25),\n" +
                "col4 NUMERIC NOT NULL,\n" +
                "col5 TEXT(25),\n" +
                "PRIMARY KEY (col1),\n" +
                "UNIQUE (col2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
