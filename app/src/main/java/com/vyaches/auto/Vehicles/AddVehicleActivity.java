package com.vyaches.auto.Vehicles;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vyaches.auto.DatabaseHelper;
import com.vyaches.auto.R;

import java.io.IOException;

public class AddVehicleActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        //setTitle("Добавить автомобиль");
        setTitle("Add vehicle");

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

        Button addVehicle = (Button)findViewById(R.id.addCarButton);

        addVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView nameTxt = (TextView)findViewById(R.id.carName);
                TextView brandTxt = (TextView)findViewById(R.id.carBrand);
                TextView yearNum = (TextView)findViewById(R.id.carYear);
                TextView plateTxt = (TextView)findViewById(R.id.carPlate);
                TextView commTxt = (TextView)findViewById(R.id.carComment);


                String vName = nameTxt.getText().toString();
                String vBrand = brandTxt.getText().toString();
                String vYear = yearNum.getText().toString();
                String vPlate = plateTxt.getText().toString();
                String vComment = commTxt.getText().toString();

                if(vName.isEmpty() || vBrand.isEmpty() || vYear.isEmpty() || vPlate.isEmpty())
                    Toast.makeText(getApplicationContext(), "Заполните необходимые поля", Toast.LENGTH_LONG).show();
                else {
                    ContentValues values = new ContentValues();
                    values.put("name", vName);
                    values.put("brand", vBrand);
                    values.put("year", Integer.parseInt(vYear));
                    values.put("plate", vPlate);
                    values.put("comment", vComment);

                    mDb.insert("vehicles", null, values);
                    finish();
                }
            }
        });
    }
}
