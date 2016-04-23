package com.mail.aileron.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mail.aileron.aileronmail.MessageInbox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by daniar on 19/04/16.
 */
public class DBHelperInbox extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Aileron.db";
    public static final String INBOX_TABLE_NAME = "inbox";
    public static final String INBOX_TABLE_ID= "id";
    public static final String INBOX_TABLE_NO_SENDER= "no_sender";
    public static final String INBOX_COLUMN_NAME_SENDER= "name_sender";
    public static final String INBOX_COLUMN_MESSAGE= "message";
    public static final String INBOX_COLUMN_STATUS= "status";
    SQLiteDatabase db;
    private HashMap hp;

    public DBHelperInbox(Context context) {
        super(context, DATABASE_NAME , null, 1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean insertInbox (String no_sender, String name_sender, String message, String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("no_sender", no_sender);
        contentValues.put("name_sender", name_sender);
        contentValues.put("message", message);
        contentValues.put("status", status);
        db.insert("inbox", null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from inbox where id="+Integer.toString(id)+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, INBOX_TABLE_NAME);
        return numRows;
    }

    public boolean updateInbox (int id, String no_sender, String name_sender, String message, String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("no_sender", no_sender);
        contentValues.put("name_sender", name_sender);
        contentValues.put("message", message);
        contentValues.put("status", status);
        db.update("inbox", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean isMessageAlreadyRead(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from inbox where id="+Integer.toString(id)+" and status='read'", null );
        return res.getCount() == 1;
    }

    public Integer deleteInbox(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("inbox",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<MessageInbox> getAllInbox()
    {
        ArrayList<MessageInbox> array_list = new ArrayList<MessageInbox>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from inbox", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){;
            int id = Integer.parseInt(res.getString(res.getColumnIndex(INBOX_TABLE_ID)));
            String no_sender = (res.getString(res.getColumnIndex(INBOX_TABLE_NO_SENDER)));
            String name_sender = (res.getString(res.getColumnIndex(INBOX_COLUMN_NAME_SENDER)));
            String message = (res.getString(res.getColumnIndex(INBOX_COLUMN_MESSAGE)));
            String status = (res.getString(res.getColumnIndex(INBOX_COLUMN_STATUS)));
            MessageInbox msg  = new MessageInbox(id,no_sender,name_sender,message,status);
            array_list.add(msg);
            res.moveToNext();
        }
        return array_list;
    }
}
