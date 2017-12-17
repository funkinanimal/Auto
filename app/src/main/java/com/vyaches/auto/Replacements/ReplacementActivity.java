package com.vyaches.auto.Replacements;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.vyaches.auto.DatabaseHelper;
import com.vyaches.auto.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ReplacementActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private int vId;
    private ListView replacementList;

    private ArrayList<HashMap<String, String>> replacements;
    private HashMap<String, String> replacement;
    private ArrayList<Integer> ids;
    private SimpleAdapter repAdapter;


    private void refresh(){
        replacements = new ArrayList<HashMap<String, String>>();
        ids = new ArrayList<Integer>();

        Cursor cursor = mDb.rawQuery("Select * from replacementform where vehicle_id=" + vId, null);
        cursor.moveToLast();


        while(!cursor.isBeforeFirst()){
            replacement = new HashMap<String, String>();
            int time = cursor.getInt(3);
            String repdate = new SimpleDateFormat("dd.MM.yyyy").format(new Date(time*1000L));

            replacement.put("name", cursor.getString(1));
            replacement.put("repdate", repdate);
            replacements.add(replacement);

            ids.add(cursor.getInt(7));

            cursor.moveToPrevious();
        }
        cursor.close();

        String[] from = {"name", "repdate"};
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


        FloatingActionButton replacementFab = (FloatingActionButton)findViewById(R.id.replacementFab);

        replacementFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReplacementActivity.this, ReplaceDecisionActivity.class);
                intent.putExtra("vId", vId);
                startActivity(intent);
            }
        });

        replacementList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent infoIntent = new Intent(ReplacementActivity.this, ReplacementInfoActivity.class);
                infoIntent.putExtra("rId", ids.get(position));
                startActivity(infoIntent);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();

        refresh();
        replacementList.invalidate();
    }
}
