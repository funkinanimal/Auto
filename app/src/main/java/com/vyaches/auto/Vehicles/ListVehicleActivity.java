package com.vyaches.auto.Vehicles;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.vyaches.auto.DatabaseHelper;
import com.vyaches.auto.R;
import com.vyaches.auto.Replacements.ReplacementActivity;
import com.vyaches.auto.Templates.TemplateActivity;

import java.io.IOException;

public class ListVehicleActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;


    String[] menu_en = {"Replacements", "Templates", "Info", "Change car"};
    //String[] menu_ru = {"Замены", "Шаблоны", "Информация", "Сменить автомобиль"};
    private ListView menuList;
    private int vId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_vehicle);


        Intent goToVehicle = getIntent();
        vId = goToVehicle.getIntExtra("vId", 0);

        String title = goToVehicle.getStringExtra("vTitle");

        setTitle(title);

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

        menuList = (ListView)findViewById(R.id.menuList);

        ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this, R.layout.menu_item_adapter, menu_en);
        menuList.setAdapter(menuAdapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = null;
                switch (position){
                    case 0:
                        //Toast.makeText(getApplicationContext(), "Zamena", Toast.LENGTH_SHORT).show();
                        intent = new Intent(ListVehicleActivity.this, ReplacementActivity.class);
                        intent.putExtra("vId", vId);
                        break;
                    case 1:
                        //Toast.makeText(getApplicationContext(), "Wablon", Toast.LENGTH_SHORT).show();
                        intent = new Intent(ListVehicleActivity.this, TemplateActivity.class);
                        break;
                    case 2:
                        //Toast.makeText(getApplicationContext(), "Info", Toast.LENGTH_SHORT).show();
                        intent = new Intent(ListVehicleActivity.this, ListInfoActivity.class);
                        intent.putExtra("vId", vId);
                        break;
                    case 3:
                        intent = new Intent(ListVehicleActivity.this, MainActivity.class);
                        finish();
                        break;
                }
                if(intent != null){
                    startActivity(intent);
                }
            }
        });
    }
}
