package com.mail.aileron.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by daniar on 20/04/16.
 */
public class DBTableCreator extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Aileron.db";
    SQLiteDatabase db;
    private HashMap hp;

    public DBTableCreator (Context context) {
        super(context, DATABASE_NAME , null, 1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table user " +
                        "(email text primary key, password text, status text, pri_key text, pub_key_x text, pub_key_y text)"
        );
        db.execSQL(
                "create table inbox " +
                        "(id integer primary key, time date, no_sender text, message text, status text)"
        );
        db.execSQL(
                "create table outbox " +
                        "(id integer primary key, time date, no_receiver text, message text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS inbox");
        db.execSQL("DROP TABLE IF EXISTS outbox");
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }


}
