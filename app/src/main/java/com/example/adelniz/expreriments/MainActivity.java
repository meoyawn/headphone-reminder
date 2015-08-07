package com.example.adelniz.expreriments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends Activity {
    static final Executor SINGLE = Executors.newSingleThreadExecutor();

    final Activity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putLong(Long.toString(System.currentTimeMillis()), System.currentTimeMillis())
                .apply();

        SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                final ContentValues cv = new ContentValues();
                cv.put("col1", System.currentTimeMillis());
                cv.put("col4", System.currentTimeMillis());

                final DatabaseHelper dh = new DatabaseHelper(self);
                dh.getWritableDatabase().insert("test_table", null, cv);
                dh.close();
            }
        });

        SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                write(new File(getCacheDir(), "cache.txt"));
            }
        });

        SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                write(new File(getFilesDir(), "file.txt"));
            }
        });

        SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                write(new File(getExternalCacheDir(), "cache.txt"));
            }
        });

        SINGLE.execute(new Runnable() {
            @Override
            public void run() {
                write(new File(getExternalFilesDir("text"), "text.txt"));
            }
        });
    }

    static void write(File file) {
        try {
            final PrintWriter pw = new PrintWriter(file);
            pw.println(System.currentTimeMillis());
            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
