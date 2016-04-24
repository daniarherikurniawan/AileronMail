package com.mail.aileron.reader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mail.aileron.aileronmail.R;
import com.mail.aileron.database.DBHelperInbox;
import com.mail.aileron.database.DBHelperOutbox;
import com.mail.aileron.encryption.modeCFB;
import com.mail.aileron.object.Message;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class ReadEncryptedSMS extends AppCompatActivity {

    private DBHelperInbox mydbInbox;
    private DBHelperOutbox mydbOutbox;
    private modeCFB cfb;
    Message msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mydbInbox = new DBHelperInbox(this);
        mydbOutbox = new DBHelperOutbox(this);
        cfb = new modeCFB("aileronmail");

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

        if((msg.isEncrypted())){
            String encryptedText = msg.getEncryptedText();
            cfb.plainText = convertStringToInt(encryptedText);
            getSupportActionBar().setTitle("Decrypted Message");
            cfb.resultText = cfb.startDecryptionModeCFB(cfb.plainText);
            tv.setText(convertIntToHex(cfb.resultText));
        } else {
            getSupportActionBar().setTitle("Original Message");
            tv.setText((msg.message ));
        }
    }


    private ArrayList<Integer> convertStringToInt(String str){
        ArrayList<Integer> result = new ArrayList<>();
            for (int i = 0; i < str.length(); i ++){
                result.add((int)str.charAt(i));
            }

        return result;
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
