package com.mail.aileron.aileronmail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mail.aileron.object.Message;

import java.util.ArrayList;

/**
 * Created by daniar on 23/04/16.
 */
public class MyListAdapter extends ArrayAdapter<Message> {
    ArrayList<Message> ArrayOfSMS;
    Context context;
    public MyListAdapter(Context context, ArrayList<Message> ArrayOfSMS) {
        super(context, R.layout.sms_view, ArrayOfSMS);
        this.context = context;
        this.ArrayOfSMS = ArrayOfSMS;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        if (itemView == null){
            itemView = inflater.inflate( R.layout.sms_view, parent, false);
        }

        Message currentSMS = ArrayOfSMS.get(position);

        TextView phoneView = (TextView) itemView.findViewById(R.id.viewPhone);
        TextView smsView = (TextView) itemView.findViewById(R.id.viewSMS);

        phoneView.setText(currentSMS.phone);
        String message = currentSMS.message;
        message = message.replaceAll("(\\r|\\n)", " ");
        if (message.length()>25){
            smsView.setText(message.substring(0,25)+" ...");
        }else {
            smsView.setText(message);
        }

        return itemView;
    }
}
