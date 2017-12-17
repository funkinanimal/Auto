package com.vyaches.auto.Vehicles;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.vyaches.auto.DatabaseHelper;
import com.vyaches.auto.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;


    private ArrayList<HashMap<String, String>> vehicles;
    private HashMap<String, String> vehicle;
    private ArrayList<String> names;
    private ArrayList<Integer> ids;
    private SimpleAdapter adapter;
    ListView listView;


    private void refresh(){
        vehicles = new ArrayList<HashMap<String, String>>();
        ids = new ArrayList<Integer>();

        names = new ArrayList<String>();
        Cursor cursor = mDb.rawQuery("Select * from vehicles", null);
        cursor.moveToLast();

        while(!cursor.isBeforeFirst()) {
            vehicle = new HashMap<String, String>();

            vehicle.put("name", cursor.getString(1));
            vehicle.put("brand", cursor.getString(2));
            names.add(cursor.getString(1));
            vehicles.add(vehicle);
            ids.add(cursor.getInt(0)); // хранятся id машин
            cursor.moveToPrevious();
        }
        cursor.close();

        String[] from = {"name", "brand"};
        int[] to = {R.id.textView, R.id.textView2};

        adapter = new SimpleAdapter(this, vehicles, R.layout.adapter_item, from, to);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setTitle("Мои автомобили");
        setTitle("My vehicles");

        listView = (ListView)findViewById(R.id.listView);

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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Position: " + position + " id: " + id, Toast.LENGTH_SHORT).show();
                Intent goToVehicle = new Intent(MainActivity.this, ListVehicleActivity.class);
                String vTitle = names.get(position);
                int vId = ids.get(position);
                goToVehicle.putExtra("vTitle", vTitle);
                goToVehicle.putExtra("vId", vId);
                startActivity(goToVehicle);
                finish();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent addVeh = new Intent(MainActivity.this, AddVehicleActivity.class);
                startActivity(addVeh);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        refresh();
        listView.invalidate();
    }

}
