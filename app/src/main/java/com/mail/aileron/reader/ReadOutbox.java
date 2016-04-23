package com.mail.aileron.reader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mail.aileron.aileronmail.R;
import com.mail.aileron.database.DBHelperInbox;
import com.mail.aileron.database.DBHelperOutbox;
import com.mail.aileron.object.Message;

/**
 * Created by daniar on 23/04/16.
 */
public class ReadOutbox  extends AppCompatActivity {

    TextView phoneView;
    TextView messageView;
    private DBHelperOutbox mydbOutbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mydbOutbox = new DBHelperOutbox(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_outbox);

        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getStringExtra("id"));

        Message msg = mydbOutbox.getMessage(id+1);

        phoneView = (TextView) findViewById(R.id.no_receiver);
        messageView = (TextView) findViewById(R.id.message_outbox);

        phoneView.setText(msg.phone);
        messageView.setText(msg.message);
    }
}
