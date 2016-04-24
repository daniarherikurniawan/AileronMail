package com.mail.aileron.aileronmail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.mail.aileron.database.DBHelperUser;
import com.mail.aileron.encryption.modeCFB;
import com.mail.aileron.signature.EllipticCurveDS;
import com.mail.aileron.signature.Point;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;

import static com.mail.aileron.encryption.App.writeFile;
import static com.mail.aileron.encryption.commonOperation.convertToString;

public class WriteSMSActivity extends AppCompatActivity {

    EditText txtphoneNo;
    EditText txtMessage;
    CheckBox checkEncription;
    CheckBox checkSignature;
    private EllipticCurveDS digitalSignature;
    private DBHelperUser mydbUser ;
    private DBHelperOutbox mydbOutbox;
    private  modeCFB cfb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydbOutbox = new DBHelperOutbox(this);
        digitalSignature = new EllipticCurveDS();
        mydbUser = new DBHelperUser(this);
        setContentView(R.layout.activity_write_sms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Write SMS");
        txtphoneNo = (EditText) findViewById(R.id.editText1);
        txtMessage = (EditText) findViewById(R.id.editText2);
        checkEncription = (CheckBox) findViewById(R.id.checkBox1);
        checkSignature = (CheckBox) findViewById(R.id.checkBox2);

        cfb = new modeCFB("aileronmail");

        Intent intent = getIntent();
//        id = Integer.parseInt(intent.getStringExtra("id"));
        String no_receiver = intent.getStringExtra("no_receiver");
        if(no_receiver!=null){
            txtphoneNo.setText(no_receiver);
        }

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(WriteSMSActivity.this, MainActivity.class);
//                intent.putExtra("id",""+id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                sendSMSMessage();
            }
        });
    }


    protected void sendSMSMessage() {

        Log.i("Send SMS", "");
        String phoneNo = txtphoneNo.getText().toString();
        String plainMessage = txtMessage.getText().toString();

        String message = prepareTheMessage(plainMessage);

        int idxStart = 0;
        int idxEnd = 0;
        for (int i = (int) Math.ceil(message.length() / 70.0); i >= 1  ; i--){

            idxStart = idxEnd;
            if(i == 1){
                idxEnd = message.length();
            }else{
                idxEnd += 70;
            }

            Toast.makeText(getApplicationContext(), "Proccessing", Toast.LENGTH_LONG).show();
            try {
                SmsManager smsManager = SmsManager.getDefault();
                String currentMsg = message.substring(idxStart, idxEnd);
//                Toast.makeText(getApplicationContext(), " message : "+currentMsg, Toast.LENGTH_LONG).show();
                smsManager.sendTextMessage(phoneNo, null, currentMsg, null, null);
                Toast.makeText(getApplicationContext(), "SMS "+i+" sent", Toast.LENGTH_LONG).show();
                mydbOutbox.insertOutbox(phoneNo, currentMsg);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(WriteSMSActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public ArrayList<Integer> convertStringToInt(String str){
        ArrayList<Integer> result = new ArrayList<>();
        try {
            byte[] bytes = str.getBytes("US-ASCII");
            for (int i = 0; i < bytes.length; i ++){
                result.add((int)bytes[i]);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

    private String prepareTheMessage(String plainMessage){
        String message = "";

        if( !checkSignature.isChecked() && !mydbUser.isHasKey()) {
            Toast.makeText(getApplicationContext(), "SMS failed, you should generate your key!.", Toast.LENGTH_LONG).show();
        }else if ((checkEncription.isChecked() && checkSignature.isChecked())  ) {
                int idxLast = 0;
                for (int i = 0; i < plainMessage.length(); ) {
                /* + 28 karakter*/
                    idxLast += 42;
                    if (plainMessage.length() < idxLast)
                        idxLast = plainMessage.length();

                    String currentMsg = plainMessage.substring(i, idxLast);
                    cfb.plainText = convertStringToInt(currentMsg);
                    cfb.cipherText = cfb.startEncryptionModeCFB(cfb.plainText);
                    String cipher = convertIntToHex(cfb.cipherText);
                /* +11*/
                    message += "<enc>" + cipher + "</enc>";
                /* +17 */
                    BigInteger priKey = new BigInteger(mydbUser.getPriKey());
                    Point pubKey = new Point(new BigInteger(mydbUser.getPubKeyX())
                            , new BigInteger(mydbUser.getPubKeyY()));
                    Point signature = digitalSignature.computeSignature(cipher, priKey, pubKey);

                    message = message + "\n<sign"+signature.getX()+","+signature.getY()+"/>";
                    i = idxLast;
                }

            } else if (checkSignature.isChecked()) {
            /* + 70-17 karakter*/
                int idxLast = 0;
                for (int i = 0; i < plainMessage.length(); ) {
                /* + 17 karakter*/
                    idxLast += 53;
                    if (plainMessage.length() < idxLast)
                        idxLast = plainMessage.length();

                    String currentMsg = plainMessage.substring(i, idxLast);

                    BigInteger priKey = new BigInteger(mydbUser.getPriKey());
                    Point pubKey = new Point(new BigInteger(mydbUser.getPubKeyX())
                            , new BigInteger(mydbUser.getPubKeyY()));
                    Point signature = digitalSignature.computeSignature(currentMsg, priKey, pubKey);

                    message = message + currentMsg + "\n<sign"+signature.getX()+","+signature.getY()+"/>";
                    i = idxLast;
                }
            } else if (checkEncription.isChecked()) {
                int idxLast = 0;
                for (int i = 0; i < plainMessage.length(); ) {
                /* + 11 karakter*/
                    idxLast += 59;
                    if (plainMessage.length() < idxLast)
                        idxLast = plainMessage.length();


                    cfb.plainText = convertStringToInt(plainMessage.substring(i, idxLast));
                    cfb.cipherText = cfb.startEncryptionModeCFB(cfb.plainText);
                    String cipher = convertIntToHex(cfb.cipherText);

                    message = message + "<enc>" + cipher + "</enc>";
                    i = idxLast;
                }
            } else {
                message = plainMessage;
            }

        return message;
    }

    public String convertIntToHex(ArrayList<Integer> ArrayInt){
        String result="";
        String[] strHex = {"\u0000", "\u00001", "\u0002", "\u0003", "\u0004", "\u0005", "\u0006", "\u0007", "\u0008", "\u0009", "\n", "\u000b", "\u000c", "\r", "\u000e", "\u000f", "\u0010", "\u0011", "\u0012", "\u0013", "\u0014", "\u0015", "\u0016", "\u0017", "\u0018", "\u0019", "\u001a", "\u001b", "\u001c", "\u001d", "\u001e", "\u001f", " ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[", "\\", "]", "^", "_", "`", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "|", "}", "~", "\u007F", "\u0080", "\u0081", "\u0082", "\u0083", "\u0084", "\u0085", "\u0086", "\u0087", "\u0088", "\u0089", "\u008a", "\u008b", "\u008c", "\u008d", "\u008e", "\u008f", "\u0090", "\u0091", "\u0092", "\u0093", "\u0094", "\u0095", "\u0096", "\u0097", "\u0098", "\u0099", "\u009a", "\u009b", "\u009c", "\u009D", "\u009e", "\u009f", "\u00a0", "\u00a1", "\u00a2", "\u00a3", "\u00a4", "\u00a5", "\u00a6", "\u00a7", "\u00a8", "\u00a9", "\u00aa", "\u00ab", "\u00ac", "\u00ad", "\u00ae", "\u00af", "\u00b0", "\u00b1", "\u00b2", "\u00b3", "\u00b4", "\u00b5", "\u00b6", "\u00b7", "\u00b8", "\u00b9", "\u00ba", "\u00bb", "\u00bc", "\u00bd", "\u00be", "\u00bf", "\u00c0", "\u00c1", "\u00c2", "\u00c3", "\u00c4", "\u00c5", "\u00c6", "\u00c7", "\u00c8", "\u00c9", "\u00ca", "\u00cb", "\u00cc", "\u00cd", "\u00ce", "\u00cf", "\u00d0", "\u00d1", "\u00d2", "\u00d3", "\u00d4", "\u00d5", "\u00d6", "\u00d7", "\u00d8", "\u00d9", "\u00da", "\u00db", "\u00dc", "\u00dd", "\u00de", "\u00df", "\u00e0", "\u00e1", "\u00e2", "ã", "ä", "å", "æ", "ç", "è", "é", "ê", "ë", "\u00ec", "í", "î", "ï", "ð", "ñ", "ò", "ó", "ô", "õ", "ö", "÷", "ø", "ù", "ú", "û", "ü", "ý", "þ", "ÿ"};
        for (int i = 0; i < ArrayInt.size(); i ++){
            result += strHex[ArrayInt.get(i)];
//            Log.w("result ",result+"\u007F \u00ec ì");
        }
        return result;
    }

}
