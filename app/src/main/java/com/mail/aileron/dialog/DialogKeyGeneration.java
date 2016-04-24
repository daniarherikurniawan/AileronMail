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

import com.mail.aileron.aileronmail.R;

/**
 * Created by daniar on 24/04/16.
 */
public class DialogKeyGeneration extends DialogFragment {
    String tag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.generated_key, container, false);
        EditText pri_key = (EditText) rootView.findViewById(R.id.private_key);
        EditText pub_key_x = (EditText) rootView.findViewById(R.id.public_key_x);
        EditText pub_key_y = (EditText) rootView.findViewById(R.id.public_key_y);
        final Button dismiss = (Button) rootView.findViewById(R.id.dismiss);

        if(tag == "New Key"){
            getDialog().setTitle("          "+tag);
            pri_key.setText("22222");
            pub_key_x.setText("44444");
            pub_key_y.setText("44444");
            dismiss.setText("Save");
        }else{
            getDialog().setTitle("         "+tag);
            pri_key.setText("11111");
            pub_key_x.setText("55555");
            pub_key_y.setText("44444");
        }

        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(dismiss.getText()=="Save"){
                    //save
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