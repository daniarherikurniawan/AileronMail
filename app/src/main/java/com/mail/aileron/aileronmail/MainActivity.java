package com.mail.aileron.aileronmail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mail.aileron.database.DBHelperInbox;
import com.mail.aileron.database.DBHelperOutbox;
import com.mail.aileron.database.DBHelperUser;
import com.mail.aileron.database.DBTableCreator;
import com.mail.aileron.inbox.WriteSMSActivity;
import com.mail.aileron.login.LoginActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView msg;
    String[] cars ={"Camaro", "Lamborgini","Toyota", "Ferrari", "BMW"};
    String[] cars2 ={"Suzuki", "Honda","Daihatsu", "GT R", "Nissan"};
    ListView list;

    private DBTableCreator mydbCreator;
    private DBHelperUser mydbUser ;
    private DBHelperInbox mydbInbox;
    private DBHelperOutbox mydbOutbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydbUser = new DBHelperUser(this);
        mydbInbox = new DBHelperInbox(this);
        mydbOutbox = new DBHelperOutbox(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inbox");

        list = (ListView) findViewById(R.id.listSMS);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, cars);
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                Toast.makeText(getApplicationContext(), "long click: " + pos, Toast.LENGTH_LONG).show();

                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Toast.makeText(getApplicationContext(), "click: " + arg0, Toast.LENGTH_LONG).show();
            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                ArrayList<Message> arrayInbox = mydbInbox.getAllInbox();
//        Toast.makeText(getApplicationContext(), arrayInbox.toString(), Toast.LENGTH_LONG);

//                Snackbar.make(view, "inbox : "+mydbInbox.getAllInbox().toString(), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
                Snackbar.make(view, "outbox : "+mydbOutbox.getAllOutbox().toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

//                Snackbar.make(view, "user : "+mydbUser.getAllUsers().toString(), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                // creating required tables
//                db.execSQL(CREATE_TABLE_TODO);
//                db.execSQL(CREATE_TABLE_TAG);
//                db.execSQL(CREATE_TABLE_TODO_TAG);
//

                Intent intent = new Intent(MainActivity.this, WriteSMSActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                msg = (TextView) findViewById(R.id.msg);
                return true;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        TextView userEmail = (TextView) findViewById(R.id.userEmail);
        userEmail.setText(mydbUser.getEmail());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            ArrayList<String> contact =  mydbUser.getAllUsers();
//            Toast.makeText(getApplicationContext(), contact.toString(), Toast.LENGTH_LONG).show();
            return true;
        }else if (id == R.id.action_logout) {
            finish();
            String email =  mydbUser.getEmail();
            mydbUser.updateUser(email, "not_logged_in");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_inbox) {
            getSupportActionBar().setTitle("Inbox");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, cars);
            list.setAdapter(adapter);
        } else if (id == R.id.nav_sent) {
            getSupportActionBar().setTitle("Sent");
            ArrayList<MessageOutbox> arrayOutbox = mydbOutbox.getAllOutbox();
            Toast.makeText(getApplicationContext(), arrayOutbox.toString(), Toast.LENGTH_LONG);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, cars2);
            list.setAdapter(adapter);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
