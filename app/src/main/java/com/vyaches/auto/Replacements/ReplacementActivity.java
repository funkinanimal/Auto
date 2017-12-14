package com.vyaches.auto.Replacements;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.vyaches.auto.DatabaseHelper;
import com.vyaches.auto.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ReplacementActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private int vId;
    private ListView replacementList;

    private ArrayList<HashMap<String, String>> replacements;
    private HashMap<String, String> replacement;
    private ArrayList<String> names;
    private ArrayList<Integer> ids;
    private SimpleAdapter repAdapter;

    private void refresh(){
        replacements = new ArrayList<HashMap<String, String>>();
        ids = new ArrayList<Integer>();

        Cursor cursor = mDb.rawQuery("Select * from replacements where _id=" + vId, null);
        cursor.moveToLast();

        while(!cursor.isBeforeFirst()){
            replacement = new HashMap<String, String>();

            String repdate = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date(cursor.getInt(3)*1000));
            replacement.put("name", cursor.getString(1));
            replacement.put("repdate", repdate);
            replacements.add(replacement);

            ids.add(cursor.getInt(0));

            cursor.moveToPrevious();
        }
        cursor.close();

        String[] from = {"name", "brand"};
        int[] to = {R.id.textView, R.id.textView2};

        repAdapter = new SimpleAdapter(this, replacements, R.layout.adapter_item, from, to);
        replacementList.setAdapter(repAdapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replacement);

        Intent goToVehicle = getIntent();
        vId = goToVehicle.getIntExtra("vId", 0);

        replacementList = (ListView)findViewById(R.id.replacementList);

        mDBHelper = new DatabaseHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }


    }

    @Override
    public void onResume(){
        super.onResume();

        refresh();
        replacementList.invalidate();
    }
}
