package com.mail.aileron.reader;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.mail.aileron.aileronmail.MainActivity;
import com.mail.aileron.aileronmail.R;
import com.mail.aileron.database.DBHelperInbox;
import com.mail.aileron.object.Message;

public class ReadInbox extends AppCompatActivity {

    TextView phoneView;
    TextView messageView;
    private DBHelperInbox mydbInbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mydbInbox = new DBHelperInbox(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_inbox);

        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getStringExtra("id"));

        Message msg = mydbInbox.getMessage(id);
        getSupportActionBar().setTitle("Received from: " +msg.phone);

//        phoneView = (TextView) findViewById(R.id.no_sender);
        messageView = (TextView) findViewById(R.id.message_inbox);

//        phoneView.setText(msg.phone);
        messageView.setText(msg.message);

        if(msg.status!= ""){
            //remove new sign
            mydbInbox.markInboxAsRead(id);
        }
    }


    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(ReadInbox.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
