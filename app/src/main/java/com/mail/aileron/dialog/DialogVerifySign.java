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
import com.mail.aileron.database.DBHelperUser;
import com.mail.aileron.object.Message;
import com.mail.aileron.signature.EllipticCurveDS;
import com.mail.aileron.signature.Point;

import java.math.BigInteger;

/**
 * Created by daniar on 24/04/16.
 */
public class DialogVerifySign extends DialogFragment {
    String tag;
    String title;
    int id;
    Message msg;

    private DBHelperInbox mydbInbox;
    private DBHelperOutbox mydbOutbox;
    private EllipticCurveDS digitalSignature;
    private DBHelperUser mydbUser ;
    private EditText pub_key_x;
    private EditText pub_key_y;

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.dialog_set_pub_key, container, false);
        pub_key_x = (EditText) rootView.findViewById(R.id.set_public_key_x);
        pub_key_y  = (EditText) rootView.findViewById(R.id.set_public_key_y);

        mydbInbox = new DBHelperInbox(rootView.getContext());
        mydbOutbox = new DBHelperOutbox(rootView.getContext());
        digitalSignature = new EllipticCurveDS();
        mydbUser = new DBHelperUser(rootView.getContext());

        getDialog().setTitle(title);
        pub_key_x.setText(mydbUser.getPubKeyX());
        pub_key_y.setText(mydbUser.getPubKeyY());


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

                String message = msg.getContentMessage();
                Point signature = msg.getSignature();
                Point pubKey = new Point(new BigInteger(pub_key_x.getText().toString())
                        , new BigInteger(pub_key_y.getText().toString()));

                String verification = digitalSignature.signatureVerification(message, signature, pubKey);

                alertDialog.setMessage("The signature "+(msg.getSignature())+" is "+verification);

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

}
