package com.mail.aileron.aileronmail;

/**
 * Created by daniar on 19/04/16.
 */
public class MessageInbox {
    public int id;
    public String no_sender;
    public String name_sender;
    public String message;
    public String status;

    public MessageInbox(int id, String no_sender, String name_sender, String message, String status){
        this.id = id;
        this.no_sender = no_sender;
        this.name_sender = name_sender;
        this.message = message;
        this.status = status;
    }

    public String toString(){
        return ""+id+" "+no_sender+" \n "+name_sender+" "+" \n "+message+" \n "+status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
