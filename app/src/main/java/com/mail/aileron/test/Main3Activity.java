package com.mail.aileron.test;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mail.aileron.aileronmail.R;
import com.mail.aileron.database.DBHelperUser;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {

    String[] cars ={"Camaro", "Lamborgini","Toyota", "Ferrari", "BMW"};
    private DBHelperUser mydb ;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mydb = new DBHelperUser(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        TextView textView = (TextView) findViewById(R.id.textView2);

        Intent intent = getIntent();

        String name = intent.getStringExtra("myName");



        Button showDBButton = (Button) findViewById(R.id.show_db);
        showDBButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                ArrayList<String> contact =  mydb.getAllUsers();
                Snackbar.make(view, contact.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        ArrayList<String> contact =  mydb.getAllCotacts();

        list = (ListView) findViewById(R.id.listEmail2);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, contact);
//        list.setAdapter(adapter);

        textView.setText(name);
    }
}
