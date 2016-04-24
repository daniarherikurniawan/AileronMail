package com.mail.aileron.object;

import com.mail.aileron.signature.Point;

import java.math.BigInteger;

/**
 * Created by daniar on 23/04/16.
 */
public class Message {
    public String phone;
    public String message;
    public String id;
    public String status;

    public Message(String phone, String message, int id, String status) {
        this.phone = phone;
        this.message = message;
        this.id = ""+id;
        this.status = status;
    }

    public String getEncryptedText(){
        int startIdx = message.lastIndexOf("<enc>")+5;
        int endIdx = message.indexOf("</enc>");
        return message.substring(startIdx,endIdx);
    }

    public boolean isEncrypted(){
        return message.contains("<enc>") && message.contains("</enc>");
    }

    public String getContentMessage() {
        if(isEncrypted()){
            return getEncryptedText();
        }else{
            return getMessage();
        }
    }

    public String getMessage() {
        return message.substring(0, message.indexOf("<sign")-1);
    }


    public Point getSignature(){
        int startIdx = message.lastIndexOf("<sign")+5;
        int endIdx = message.lastIndexOf("/>");
        String[] value =  message.substring(startIdx,endIdx).split(",");
        return new Point(new BigInteger(value[0]), new BigInteger(value[1]));
    }

    public boolean isSignatured(){
        return message.contains("<sign") && message.contains("/>");
    }
}
