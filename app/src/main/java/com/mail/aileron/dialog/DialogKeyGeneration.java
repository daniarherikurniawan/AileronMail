package com.mail.aileron.dialog;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mail.aileron.aileronmail.R;
import com.mail.aileron.database.DBHelperUser;
import com.mail.aileron.signature.EllipticCurveDS;
import com.mail.aileron.signature.Point;

import java.math.BigInteger;

import static com.mail.aileron.signature.EllipticCurveDS.generatePrivateKey;

/**
 * Created by daniar on 24/04/16.
 */
public class DialogKeyGeneration extends DialogFragment {
    String tag;
    private EllipticCurveDS digitalSignature;
    private DBHelperUser mydbUser ;
    String strPriKey;
    String strPubKeyX;
    String strPubKeyY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.generated_key, container, false);
        final EditText pri_key = (EditText) rootView.findViewById(R.id.private_key);

        EditText pub_key_x = (EditText) rootView.findViewById(R.id.public_key_x);
        EditText pub_key_y = (EditText) rootView.findViewById(R.id.public_key_y);
        final Button dismiss = (Button) rootView.findViewById(R.id.dismiss);

        digitalSignature = new EllipticCurveDS();
        mydbUser = new DBHelperUser(rootView.getContext());

        strPriKey = "undef";
        strPubKeyX = "undef";
        strPubKeyY = "undef";

        if(tag == "New Key"){
            BigInteger priKey = generatePrivateKey();
            Point pubKey = digitalSignature.generatePublicKey(priKey);
            strPriKey = priKey.toString();
            strPubKeyX = pubKey.getX().toString();
            strPubKeyY = pubKey.getY().toString();

            getDialog().setTitle("         " + tag);
            dismiss.setText("Save");
        }else{
            if (mydbUser.isHasKey()) {
                strPriKey = mydbUser.getPriKey();
                strPubKeyX = mydbUser.getPubKeyX();
                strPubKeyY = mydbUser.getPubKeyY();
            }
            getDialog().setTitle("         " + tag);
        }
        pri_key.setText(strPriKey);
        pub_key_x.setText(strPubKeyX);
        pub_key_y.setText(strPubKeyY);

        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(dismiss.getText()=="Save"){
                    mydbUser.updateNewKey(strPriKey, strPubKeyX, strPubKeyY);
                }
                dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        this.tag = tag;
    }
}