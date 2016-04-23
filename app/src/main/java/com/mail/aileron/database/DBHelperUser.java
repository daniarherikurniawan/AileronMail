package com.mail.aileron.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelperUser extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Aileron.db";
    public static final String USER_TABLE_NAME = "user";
    public static final String USER_COLUMN_EMAIL= "email";
    public static final String USER_COLUMN_PASSWORD= "password";
    public static final String USER_COLUMN_STATUS= "status";
    SQLiteDatabase db;
    private HashMap hp;

    public DBHelperUser(Context context) {
        super(context, DATABASE_NAME , null, 1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean insertUser (String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("status", "not_logged_in");
        db.insert("user", null, contentValues);
        return true;
    }

    public Cursor getData(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user where email="+email+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, USER_TABLE_NAME);
        return numRows;
    }

    public boolean updateUser (String email,  String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("status", status);
        db.update("user", contentValues, "email = ? ", new String[] { email } );
        return true;
    }

    public boolean isUserRegistered (String email, String password)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user where email='"+email+"' and password='"+password+"'", null );
        return res.getCount() != 0;
    }

    public boolean isAccountLoggedIn ()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user where status='logged_in'", null );
        return res.getCount() == 1;
    }


    public boolean isEmailRegistered (String email)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user where email='"+email+"'", null );
        return res.getCount() != 0;
    }

    public Integer deleteUser(String email)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("user",
                "email = ? ",
                new String[] { email });
    }

    public ArrayList<String> getAllUsers()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(USER_COLUMN_EMAIL)));
            array_list.add(res.getString(res.getColumnIndex(USER_COLUMN_STATUS)));
            res.moveToNext();
        }
        return array_list;
    }

    public String getEmail()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user", null );
        res.moveToFirst();

        if (res.getCount() != 0){
            return res.getString(res.getColumnIndex(USER_COLUMN_EMAIL));
        }else{
            return null;
        }
    }
}
