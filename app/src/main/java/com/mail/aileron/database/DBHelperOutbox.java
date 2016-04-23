package com.mail.aileron.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mail.aileron.aileronmail.Message;
import com.mail.aileron.aileronmail.MessageOutbox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by daniar on 19/04/16.
 */
public class DBHelperOutbox extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Aileron.db";
    public static final String OUTBOX_TABLE_NAME = "outbox";
    public static final String OUTBOX_TABLE_ID= "id";
    public static final String OUTBOX_TABLE_NO_RECEIVER= "no_receiver";
    public static final String OUTBOX_COLUMN_NAME_RECEIVER= "name_receiver";
    public static final String OUTBOX_COLUMN_MESSAGE= "message";
    SQLiteDatabase db;
    private HashMap hp;

    public DBHelperOutbox(Context context) {
        super(context, DATABASE_NAME , null, 1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean insertOutbox (String no_receiver, String name_receiver, String message)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("no_receiver", no_receiver);
        contentValues.put("name_receiver", name_receiver);
        contentValues.put("message", message);
        db.insert("outbox", null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from outbox where id="+Integer.toString(id)+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, OUTBOX_TABLE_NAME);
        return numRows;
    }

    public boolean updateOutbox(int id, String no_receiver, String name_receiver, String message)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("no_receiver", no_receiver);
        contentValues.put("name_receiver", name_receiver);
        contentValues.put("message", message);
        db.update("outbox", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteOutbox(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("outbox",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<Message> getAllOutbox()
    {
        ArrayList<Message> array_list = new ArrayList<Message>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from outbox", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){;

            String no_receiver = (res.getString(res.getColumnIndex(OUTBOX_TABLE_NO_RECEIVER)));
            String name_receiver = (res.getString(res.getColumnIndex(OUTBOX_COLUMN_MESSAGE)));

            Message msg  = new Message(no_receiver,name_receiver);
            array_list.add(msg);
            res.moveToNext();
        }
        return array_list;
    }
}
