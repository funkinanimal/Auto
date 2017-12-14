package com.vyaches.auto.Templates;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.vyaches.auto.DatabaseHelper;
import com.vyaches.auto.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TemplateActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    //private ArrayList<HashMap<String, String>> templates;
    private HashMap<String, String> template;
    private ArrayList<Integer> ids;
    private ArrayList<String> titles;
    private SimpleAdapter adapter;

    private ArrayList<String> templates;
    ListView templatesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        setTitle("Templates");

        templatesList = (ListView)findViewById(R.id.templatesList);

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

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.templateFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TemplateActivity.this, AddTemplateActivity.class));
            }
        });

        templatesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent goToTempInfo = new Intent(TemplateActivity.this, TemplateInfo.class);
                int tId = ids.get(position);
                goToTempInfo.putExtra("tId", tId);
                startActivity(goToTempInfo);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        refresh();
        templatesList.invalidate();
    }

    private void refresh()
    {
        //templates = new ArrayList<HashMap<String, String>>();
        ids = new ArrayList<Integer>();
        templates = new ArrayList<String>();


        Cursor cursor = mDb.rawQuery("Select * from templates", null);
        cursor.moveToLast();

        while(!cursor.isBeforeFirst()){
            //template = new HashMap<String, String>();

            templates.add(cursor.getString(1));
            ids.add(cursor.getInt(0));
            cursor.moveToPrevious();
        }
        cursor.close();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, templates);
        templatesList.setAdapter(arrayAdapter);
    }
}
