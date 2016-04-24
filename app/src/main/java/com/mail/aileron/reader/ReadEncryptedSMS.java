package com.mail.aileron.reader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mail.aileron.aileronmail.R;
import com.mail.aileron.database.DBHelperInbox;
import com.mail.aileron.database.DBHelperOutbox;
import com.mail.aileron.object.Message;

import org.w3c.dom.Text;

public class ReadEncryptedSMS extends AppCompatActivity {

    private DBHelperInbox mydbInbox;
    private DBHelperOutbox mydbOutbox;
    Message msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mydbInbox = new DBHelperInbox(this);
        mydbOutbox = new DBHelperOutbox(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_encrypted_sms);


        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getStringExtra("id"));
        String tag = intent.getStringExtra("tag");

        if(tag.compareTo("Inbox") == 0) {
            msg = mydbInbox.getMessage(id);
        }else {
            msg = mydbOutbox.getMessage(id);
        }

        TextView tv = (TextView) findViewById(R.id.message_decrypted);

        if(isEncrypted(msg.message)){
            getSupportActionBar().setTitle("Decrypted Message");
            tv.setText(getEncryptedText(msg.message ));
        } else {
            getSupportActionBar().setTitle("Original Message");
            tv.setText((msg.message ));
        }
    }

    public String getEncryptedText(String plainMessage){
        int startIdx = plainMessage.lastIndexOf("<encrypted>")+11;
        int endIdx = plainMessage.indexOf("</encrypted>");
        return plainMessage.substring(startIdx,endIdx);
    }

    public boolean isEncrypted(String plainMessage){
        return plainMessage.contains("<encrypted>") && plainMessage.contains("</encrypted>");
    }
}
