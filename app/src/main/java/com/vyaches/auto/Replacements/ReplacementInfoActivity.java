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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ReplacementInfoActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private HashMap<String, String> item;
    private ArrayList<HashMap<String, String>> items;

    private ListView repInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replacement_info);

        repInfoList = (ListView)findViewById(R.id.repInfoList);

        Intent extIntent = getIntent();
        int rId = extIntent.getIntExtra("rId", 0);

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

        Cursor cursor = mDb.rawQuery("select * from replacementform where _id=" + rId, null);
        cursor.moveToFirst();

        String name = cursor.getString(1);
        String mileage = cursor.getString(4);
        String brand = cursor.getString(6);

        int not = cursor.getInt(5);
        int lifeTimeInt = cursor.getInt(2);
        int repDateInt = cursor.getInt(3);

        cursor.close();

        String changeAt = "";

        String lifeTime = String.valueOf(lifeTimeInt/2592000) + " mon.";
        String repDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date(repDateInt*1000L));
        if(not == 1){
            int sum = (lifeTimeInt + repDateInt);
            changeAt = new SimpleDateFormat("dd.MM.yyyy").format(new Date(sum*1000L));
        }
        else if(not == 0){
            changeAt = "not defined";
        }

        String data[] = {name, brand, lifeTime, repDate, mileage, changeAt};
        String titles_en[] = {"Name", "Brand", "Lifetime", "Date", "Mileage", "Change at"};

        items = new ArrayList<HashMap<String, String>>();

        for(int i = 0; i < data.length; i++){
            item = new HashMap<String, String>();

            item.put("title", titles_en[i]);
            item.put("data", data[i]);
            items.add(item);
        }

        String[] from = {"title", "data"};
        int[] to = {R.id.textView4, R.id.textView5};

        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.info_item_adapter, from, to);
        repInfoList.setAdapter(adapter);
    }
}
