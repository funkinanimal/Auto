package com.vyaches.auto.Templates;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vyaches.auto.DatabaseHelper;
import com.vyaches.auto.R;

import java.io.IOException;

public class AddTemplateActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    Button addTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template);
        setTitle("Add template");

        mDBHelper = new DatabaseHelper(this);

        addTemplate = (Button)findViewById(R.id.addTemplateButton);

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

        addTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView nameText = (TextView)findViewById(R.id.templateName);
                TextView lifetimeText = (TextView)findViewById(R.id.templateLifetime);
                TextView commentText = (TextView)findViewById(R.id.templateComment);

                String tName = nameText.getText().toString();
                String tLifetime = lifetimeText.getText().toString();
                String tComment = commentText.getText().toString();

                if(tName.isEmpty() || tLifetime.isEmpty())
                    Toast.makeText(getApplicationContext(), "Заполните необходимые поля", Toast.LENGTH_SHORT).show();
                else{
                    int lt = Integer.parseInt(tLifetime);
                    int mLifetime = lt * 2592000; // месяцы
                    //int mLifetime = lt * 60; // минуты

                    ContentValues values = new ContentValues();
                    values.put("name", tName);
                    values.put("lifetime", mLifetime);
                    values.put("comment", tComment);
                    mDb.insert("templates", null, values);
                    finish();
                }
            }
        });
    }
}
