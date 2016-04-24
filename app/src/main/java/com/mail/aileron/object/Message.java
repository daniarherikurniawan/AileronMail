package com.mail.aileron.object;

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

}
