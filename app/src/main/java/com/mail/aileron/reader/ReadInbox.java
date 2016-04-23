package com.mail.aileron.reader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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

        Message msg = mydbInbox.getMessage(id+1);

        phoneView = (TextView) findViewById(R.id.no_sender);
        messageView = (TextView) findViewById(R.id.message_inbox);

        phoneView.setText(msg.phone);
        messageView.setText(msg.message);
    }
}
