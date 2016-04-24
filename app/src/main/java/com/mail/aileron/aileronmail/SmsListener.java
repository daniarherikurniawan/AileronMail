package com.mail.aileron.aileronmail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.mail.aileron.database.DBHelperInbox;

/**
 * Created by daniar on 18/04/16.
 */
public class SmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;
    private DBHelperInbox mydbInbox;


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        mydbInbox = new DBHelperInbox(context);

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from = null;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                    }
                    if (msgs.length > -1) {
                        mydbInbox.insertInbox(msg_from, msgs[0].getMessageBody());
                        Toast.makeText(context, " Message recieved from: " +msg_from.toString(), Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){
                            Log.d("Exception caught", e.getMessage());
                }
            }
        }
    }
}