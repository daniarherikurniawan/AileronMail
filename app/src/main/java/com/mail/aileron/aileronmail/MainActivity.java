package com.mail.aileron.aileronmail;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.MenuInflater;
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
import com.mail.aileron.dialog.DialogKeyGeneration;
import com.mail.aileron.dialog.DialogVerifySign;
import com.mail.aileron.login.LoginActivity;
import com.mail.aileron.object.Message;
import com.mail.aileron.reader.ReadEncryptedSMS;
import com.mail.aileron.reader.ReadInbox;
import com.mail.aileron.reader.ReadOutbox;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView msg;
    ListView list;

    private DBTableCreator mydbCreator;
    private DBHelperUser mydbUser ;
    private DBHelperInbox mydbInbox;
    private DBHelperOutbox mydbOutbox;
    private int idSelectedMsg;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydbUser = new DBHelperUser(this);
        mydbInbox = new DBHelperInbox(this);
        mydbOutbox = new DBHelperOutbox(this);
        setContentView(R.layout.activity_main);


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getSupportActionBar().getTitle() == "Inbox") {
                    refreshInboxDisplay();
                } else {
                    refreshOutboxDisplay();
                }
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inbox");

        list = (ListView) findViewById(R.id.listSMS);
        registerForContextMenu(list);
        refreshInboxDisplay();


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int pos,
                                    long id) {
                Intent intent;
                TextView idView = ((TextView) view.findViewById(R.id.idMsg));
//                Toast.makeText(getApplicationContext(), " "+ tv.getText() , Toast.LENGTH_SHORT).show();

                if (getSupportActionBar().getTitle() == "Inbox") {

                    finish();
                    intent = new Intent(MainActivity.this, ReadInbox.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);

                } else {
                    intent = new Intent(MainActivity.this, ReadOutbox.class);
                }
                intent.putExtra("id", "" + idView.getText());
                startActivity(intent);
            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
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
        getMenuInflater().inflate(R.menu.main, menu);

        TextView userEmail = (TextView) findViewById(R.id.userEmail);
        userEmail.setText(mydbUser.getEmail());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        } else if (id == R.id.action_generate_key){
            FragmentManager fm = getFragmentManager();
            DialogKeyGeneration dialogFragment = new DialogKeyGeneration();
            dialogFragment.show(fm, "New Key");
        } else if (id == R.id.action_show_current_key){
            FragmentManager fm = getFragmentManager();
            DialogKeyGeneration dialogFragment = new DialogKeyGeneration();
            dialogFragment.show(fm, "Current Key");
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
            refreshInboxDisplay();
        } else if (id == R.id.nav_sent) {
            getSupportActionBar().setTitle("Outbox");
            refreshOutboxDisplay();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void refreshInboxDisplay(){
        ArrayList <Message> ArrayOfSMS = mydbInbox.getAllInbox();
        ArrayAdapter<Message> adapter = new MyListAdapter(this, ArrayOfSMS);
        list.setAdapter(adapter);
    }


    public void refreshOutboxDisplay(){
        ArrayList <Message> ArrayOfSMS = mydbOutbox.getAllOutbox();
        ArrayAdapter<Message> adapter = new MyListAdapter(this, ArrayOfSMS);
        list.setAdapter(adapter);
    }
    /**
     * MENU
     */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

        idSelectedMsg = Integer.parseInt(((TextView) info.targetView.findViewById(R.id.idMsg)).getText().toString());
        if (v.getId()==R.id.listSMS) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.decrypt:
                Intent intent = new Intent(MainActivity.this, ReadEncryptedSMS.class);
                intent.putExtra("id",""+idSelectedMsg);
                intent.putExtra("tag", getSupportActionBar().getTitle().toString());
                startActivity(intent);
                return true;
            case R.id.verify:
                Message msg;
                if (getSupportActionBar().getTitle() == "Inbox") {
                    msg = mydbInbox.getMessage(idSelectedMsg);
                } else {
                    msg = mydbOutbox.getMessage(idSelectedMsg);
                }

                if((msg.isSignatured())){
                    FragmentManager fm = getFragmentManager();
                    DialogVerifySign dialogFragment = new DialogVerifySign();
                    dialogFragment.setId(idSelectedMsg);
                    dialogFragment.setTag(getSupportActionBar().getTitle().toString());
                    dialogFragment.show(fm, "Set Public Key");
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setMessage("There is no signature in this message");
                    alertDialog.show();
                }
                return true;
            case R.id.delete:
                if (getSupportActionBar().getTitle() == "Inbox") {
                    mydbInbox.deleteInbox(idSelectedMsg);
                    refreshInboxDisplay();
                    Toast.makeText(getApplicationContext(), "Successfully deleted! ", Toast.LENGTH_SHORT).show();
                } else {
                    mydbOutbox.deleteOutbox(idSelectedMsg);
                    refreshOutboxDisplay();
                    Toast.makeText(getApplicationContext(), "Successfully deleted! ", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}
