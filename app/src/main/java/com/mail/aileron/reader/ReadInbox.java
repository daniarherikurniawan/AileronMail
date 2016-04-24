package com.mail.aileron.reader;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mail.aileron.aileronmail.MainActivity;
import com.mail.aileron.aileronmail.R;
import com.mail.aileron.aileronmail.WriteSMSActivity;
import com.mail.aileron.database.DBHelperInbox;
import com.mail.aileron.object.Message;

public class ReadInbox extends AppCompatActivity {

    TextView messageView;
    private DBHelperInbox mydbInbox;
    Message msg;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mydbInbox = new DBHelperInbox(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_inbox);

        Intent intent = getIntent();
        id = Integer.parseInt(intent.getStringExtra("id"));

        msg = mydbInbox.getMessage(id);
        getSupportActionBar().setTitle("Received from: " +msg.phone);

        messageView = (TextView) findViewById(R.id.message_inbox);
        Button reply_button = (Button) findViewById(R.id.reply_btn);

        messageView.setText(msg.message);

        if(msg.status!= ""){
            //remove new sign
            mydbInbox.markInboxAsRead(id);
        }

        reply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ReadInbox.this, WriteSMSActivity.class);
                intent.putExtra("no_receiver",msg.phone);
                startActivity(intent);
            }
        });

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
