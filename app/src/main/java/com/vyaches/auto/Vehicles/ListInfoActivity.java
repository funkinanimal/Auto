package com.vyaches.auto.Vehicles;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.vyaches.auto.DatabaseHelper;
import com.vyaches.auto.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ListInfoActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private ListView infoList;
    private int vId;

    private HashMap<String, String> item;
    private ArrayList<HashMap<String, String>> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_info);
        infoList = (ListView)findViewById(R.id.infoList);


        Intent goToInfo = getIntent();
        vId = goToInfo.getIntExtra("vId", 0);

        setTitle("Information");

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

        Cursor cursor = mDb.rawQuery("Select * from vehicles where _id=" + vId, null);
        cursor.moveToFirst();
        String vName = cursor.getString(1);
        String vBrand = cursor.getString(2);
        String vYear = cursor.getString(3);
        String vPlate = cursor.getString(4);
        String vComment = cursor.getString(5);

        String[] data = {vName, vBrand, vYear, vPlate, vComment};
        String[] titles_en = {"Name", "Brand", "Year", "Plates", "Comment"};
        //String[] titles_ru = {"Имя", "Марка", "Год", "Гос. номер", "Комментарий"};

        items = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < data.length; i++)
        {
            item = new HashMap<String, String>();

            item.put("title", titles_en[i]);
            item.put("data", data[i]);
            items.add(item);
        }

        String[] from = {"title", "data"};
        int[] to = {R.id.textView4, R.id.textView5};

        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.info_item_adapter, from, to);
        infoList.setAdapter(adapter);

    }
}
