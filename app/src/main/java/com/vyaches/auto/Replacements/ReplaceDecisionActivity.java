package com.vyaches.auto.Replacements;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.vyaches.auto.DatabaseHelper;
import com.vyaches.auto.R;

import java.io.IOException;
import java.util.ArrayList;

public class ReplaceDecisionActivity extends AppCompatActivity {


    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private ArrayList<Integer> ids;
    private ArrayList<String> templates;
    ListView repTempList;
    Button create;

    Intent intent = null;

    int vId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_decision);

        Intent extIntent = getIntent();
        vId = extIntent.getIntExtra("vId", 0);

        repTempList = (ListView)findViewById(R.id.repTempList);

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


        intent = new Intent(ReplaceDecisionActivity.this, AddReplacementActivity.class);
        intent.putExtra("vId", vId);

        repTempList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int tId = ids.get(position);
                intent.putExtra("tId", tId);
                startActivity(intent);
                finish();
            }
        });

        create = (Button)findViewById(R.id.noTempRepButton);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
                finish();
            }
        });


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
        repTempList.setAdapter(arrayAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();

        refresh();
        repTempList.invalidate();
    }
}
