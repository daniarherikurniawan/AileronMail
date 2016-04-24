package com.mail.aileron.dialog;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mail.aileron.aileronmail.MainActivity;
import com.mail.aileron.aileronmail.R;
import com.mail.aileron.database.DBHelperInbox;
import com.mail.aileron.database.DBHelperOutbox;
import com.mail.aileron.object.Message;

/**
 * Created by daniar on 24/04/16.
 */
public class DialogSetPubKey extends DialogFragment {
    String tag;
    String title;
    int id;
    Message msg;

    private DBHelperInbox mydbInbox;
    private DBHelperOutbox mydbOutbox;

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.dialog_set_pub_key, container, false);
        EditText pub_key = (EditText) rootView.findViewById(R.id.set_public_key);

        mydbInbox = new DBHelperInbox(rootView.getContext());
        mydbOutbox = new DBHelperOutbox(rootView.getContext());

        getDialog().setTitle(title);
        pub_key.setText("55555");


        if(tag == "Inbox")
            msg = mydbInbox.getMessage(id);
        else
            msg = mydbOutbox.getMessage(id);


        Button dismiss = (Button) rootView.findViewById(R.id.oke);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setTitle("Verification");

                if(isSignatured(msg.message)){
                    alertDialog.setMessage("The signature "+getSignature(msg.message)+" is valid");
                }else{
                    alertDialog.setMessage("There is no signature in this message");
                }
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
        return rootView;
    }

    @Override
    public void show(FragmentManager manager, String title) {
        super.show(manager, title);
        this.title = title;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSignature(String plainMessage){
        int startIdx = plainMessage.lastIndexOf("[Signature:")+11;
        int endIdx = plainMessage.lastIndexOf("]");
        return plainMessage.substring(startIdx,endIdx);
    }

    public boolean isSignatured(String plainMessage){
        return plainMessage.contains("[Signature:") && plainMessage.contains("]");
    }
}
