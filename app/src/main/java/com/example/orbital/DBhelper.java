package com.example.orbital;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBhelper extends SQLiteOpenHelper {

    public static final String DBNAME = "MyLogin.db";

    public DBhelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users(username TEXT primary key, password TEXT, email TEXT, fullName TEXT, phone TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        MyDB.execSQL("drop Table if exists users");
    }

    public Boolean insertData(String username, String password, String email, String fullName, String phone) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("email", email);
        contentValues.put("fullname", fullName);
        contentValues.put("phone", phone);
        long result = MyDB.insert("users",null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkusername(String username) {
        SQLiteDatabase MYDB = this.getWritableDatabase();
        Cursor cursor = MYDB.rawQuery("Select * from users where username = ?", new String[] {username});
        return cursor.getCount() > 0;
    }

    public Boolean checkuserpassword(String username, String password) {
        SQLiteDatabase MYDB = this.getWritableDatabase();
        Cursor cursor = MYDB.rawQuery("Select * from users where username = ? and password = ?", new String[] {username, password});
        return cursor.getCount() > 0;
    }

    public String getPhoneNumber(String userName) {
        SQLiteDatabase MYDB = this.getWritableDatabase();
        String phone = "";
        Cursor cursor = MYDB.rawQuery("Select * from users where username = ?", new String[] {userName});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    phone = cursor.getString(cursor.getColumnIndex("phone"));
                    cursor.moveToNext();
                }
            }
        }
        return phone;
    }

    public String getUserName(String phone) {
        SQLiteDatabase MYDB = this.getWritableDatabase();
        String username = "";
        Cursor cursor = MYDB.rawQuery("Select * from users where phone = ?", new String[] {phone});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    username = cursor.getString(cursor.getColumnIndex("username"));
                    cursor.moveToNext();
                }
            }
        }
        return username;
    }
}
