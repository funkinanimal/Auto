package com.vyaches.auto.Replacements;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.vyaches.auto.DatabaseHelper;
import com.vyaches.auto.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddReplacementActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    EditText repName;
    EditText repLT;
    EditText repBrand;
    EditText repDate;
    EditText repKil;
    CheckBox notify;

    Button addRep;

    int vId;
    int tId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_replacement);

        repName = (EditText)findViewById(R.id.repNameText);
        repLT = (EditText)findViewById(R.id.repLTText);
        repBrand = (EditText)findViewById(R.id.repBrandText);
        repDate = (EditText)findViewById(R.id.repDateText);
        repKil = (EditText)findViewById(R.id.repMileageText);

        notify = (CheckBox)findViewById(R.id.notifyCheck);

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

        //repName.setText("");
        //repLT.setText("");

        Intent extIntent = getIntent();
        vId = extIntent.getIntExtra("vId", 0);

        if(extIntent.hasExtra("tId")){
            tId = extIntent.getIntExtra("tId", 0);
            Cursor cursor = mDb.rawQuery("select name, lifetime from templates where _id =" + tId, null);
            cursor.moveToFirst();
            repName.setText(cursor.getString(0));
            String temp = String.valueOf(cursor.getInt(1)/2592000); // month
            repLT.setText(temp);
            cursor.close();
        }

        Log.v("time ", String.valueOf(System.currentTimeMillis() / 1000));

        repDate.setText(getCurrentDate());

        addRep = (Button)findViewById(R.id.addRepButton);

        addRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String repNameString = repName.getText().toString();
                String repLTString = repLT.getText().toString();

                String repBrandString = repBrand.getText().toString();
                String repDateString = repDate.getText().toString();

                String repKilString = repKil.getText().toString();

                if(repNameString.isEmpty() || repLTString.isEmpty() || repBrandString.isEmpty() || repDateString.isEmpty() || repKilString.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Заполните необходимые поля", Toast.LENGTH_SHORT).show();
                }
                else{
                    int repLTInt = Integer.parseInt(repLTString)*2628000;
                    long repDateInt = new SimpleDateFormat("dd.MM.yyyy").parse(repDateString, new ParsePosition(0)).getTime() / 1000;

                    ContentValues values = new ContentValues();
                    values.put("vehicle_id", vId);
                    values.put("name", repNameString);
                    values.put("lifetime", repLTInt);
                    values.put("repdate", repDateInt);
                    values.put("kilometrage", Integer.parseInt(repKilString));

                    if(notify.isChecked()){
                        values.put("notify", 1);
                        Toast.makeText(getApplicationContext(), "not", Toast.LENGTH_SHORT).show();

                        mDb.insert("templates", null, values);
                    }else
                        values.put("notify", 0);

                    values.put("brand", repBrandString);

                    mDb.insert("replacementform", null, values);


                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(AddReplacementActivity.this)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(repNameString)
                                    .setContentText("record made at " + repDateInt);

                    Notification notification = builder.build();

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(1, notification);

                    finish();
                }
            }
        });
    }


    public String getCurrentDate()
    {
        String formattedDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));

        return formattedDate;
    }

}
