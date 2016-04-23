package com.mail.aileron.aileronmail;

/**
 * Created by daniar on 23/04/16.
 */
public class MessageOutbox {
    public int id;
    public String no_receiver;
    public String name_receiver;
    public String message;

    public MessageOutbox(int id, String no_receiver, String name_receiver, String message){
        this.id = id;
        this.no_receiver = no_receiver;
        this.name_receiver = name_receiver;
        this.message = message;
    }

    public String toString(){
        return ""+id+" "+no_receiver+" \n "+name_receiver+" "+" \n "+message+" ";
    }

}
