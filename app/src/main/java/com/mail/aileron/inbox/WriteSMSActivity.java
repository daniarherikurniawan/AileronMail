package com.mail.aileron.inbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.mail.aileron.aileronmail.R;
import com.mail.aileron.database.DBHelperOutbox;

public class WriteSMSActivity extends AppCompatActivity {

    EditText txtphoneNo;
    EditText txtMessage;
    CheckBox checkEncription;
    CheckBox checkSignature;

    private DBHelperOutbox mydbOutbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydbOutbox = new DBHelperOutbox(this);
        setContentView(R.layout.activity_write_sms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Write SMS");
        txtphoneNo = (EditText) findViewById(R.id.editText1);
        txtMessage = (EditText) findViewById(R.id.editText2);
        checkEncription = (CheckBox) findViewById(R.id.checkBox1);
        checkSignature = (CheckBox) findViewById(R.id.checkBox2);

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                sendSMSMessage();
            }
        });
    }


    protected void sendSMSMessage() {

        Log.i("Send SMS", "");
        String phoneNo = txtphoneNo.getText().toString();
        String plainMessage = txtMessage.getText().toString();

        String message = prepareTheMessage(plainMessage);
//        Toast.makeText(getApplicationContext(), "length ."+message.length(), Toast.LENGTH_LONG).show();

        int idxStart = 0;
        int idxEnd = 0;
        for (int i = (int) Math.ceil(message.length() / 158.0); i >= 1  ; i--){

            idxStart = idxEnd;
            if(i == 1){
                idxEnd = message.length();
            }else{
                idxEnd += 158;
            }

            try {
                SmsManager smsManager = SmsManager.getDefault();
                String currentMsg = message.substring(idxStart, idxEnd);
                smsManager.sendTextMessage(phoneNo, null, currentMsg, null, null);
                Toast.makeText(getApplicationContext(), "SMS "+i+" sent", Toast.LENGTH_LONG).show();
                mydbOutbox.insertOutbox(phoneNo, currentMsg);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private String prepareTheMessage(String plainMessage){
        String message = "";
        if(checkEncription.isChecked() && checkSignature.isChecked()){
            int idxLast = 0;
            for (int i= 0; i < plainMessage.length();){
                /* + 77 karakter*/
                idxLast += 81;
                if(plainMessage.length() < idxLast)
                    idxLast = plainMessage.length();

                message += "<encrypted>"+plainMessage.substring(i, idxLast)+"<encrypted>";
                message = message + "\n\n[Signature: 12547369860bc327f65492bcd2f7dba826084a76]";
                i = idxLast;
            }

        }else if(checkSignature.isChecked()){
            /* + 56 karakter*/
            message = message + "\n\n[Signature: 12547369860bc327f65492bcd2f7dba826084a76]";
        }else if(checkEncription.isChecked()){
            /* + 56 karakter*/
            message = message + "\n\n[Signature: 12547369860bc327f65492bcd2f7dba826084a76]";
        }else{
            message = plainMessage;
        }

        return message;
    }


}
