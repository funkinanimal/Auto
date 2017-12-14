package com.vyaches.auto.Templates;

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

public class TemplateInfo extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    ListView tempList;
    private int tId;

    private HashMap<String, String> item;
    private ArrayList<HashMap<String, String>> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_info);

        tempList = (ListView)findViewById(R.id.templateInfoList);

        Intent goToTempInfo = getIntent();
        tId = goToTempInfo.getIntExtra("tId", 0);

        setTitle("Template info");

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

        Cursor cursor = mDb.rawQuery("Select * from templates where _id=" + tId, null);
        cursor.moveToFirst();
        String tName = cursor.getString(1);
        String tLifetime = String.valueOf(cursor.getInt(2)/60); //minutes
        //String tLifetime = String.valueOf(cursor.getInt(2)/282600); // month
        String tComment = cursor.getString(3);

        String[] data = {tName, tLifetime, tComment};
        String[] titles_en = {"Name", "Lifetime", "Comment"};
        //String[] titles_ru = {"Имя", "Срок службы", "Комментарий"};

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
        tempList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
