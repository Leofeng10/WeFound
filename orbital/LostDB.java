package com.example.orbital;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LostDB extends SQLiteOpenHelper {

    public static final String DBNAME = "LostItem.db";

    public LostDB(@Nullable Context context) {

        super(context,DBNAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table LostItems(itemName TEXT , itemLocation TEXT, itemDescription TEXT, itemTime TEXT, itemPhone TEXT, itemuserName TEXT, itemImage BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists LostItems");
    }

    public Boolean insertLostItem(String itemName, String itemLocation, String itemDescription, String itemTime, String itemPhone, String itemuserName, byte[] itemImage) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.d("LostItem", itemName + itemLocation+ itemDescription + itemTime);
        contentValues.put("itemName", itemName);
        contentValues.put("itemLocation", itemLocation);
        contentValues.put("itemDescription", itemDescription);
        contentValues.put("itemTime", itemTime);
        contentValues.put("itemPhone", itemPhone);
        contentValues.put("itemuserName", itemuserName);
        contentValues.put("itemImage", itemImage);
        long result = MyDB.insert("LostItems",null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<Item> selectAllLostItem() {
        ArrayList<Item> list = new ArrayList<>();
        SQLiteDatabase MYDB = this.getWritableDatabase();
        Cursor cursor = MYDB.rawQuery("Select * from LostItems", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String name = cursor.getString(cursor.getColumnIndex("itemName"));
                    String location = cursor.getString(cursor.getColumnIndex("itemLocation"));
                    String description = cursor.getString(cursor.getColumnIndex("itemDescription"));
                    String time = cursor.getString(cursor.getColumnIndex("itemTime"));
                    String phone = cursor.getString(cursor.getColumnIndex("itemPhone"));
                    String username = cursor.getString(cursor.getColumnIndex("itemuserName"));
                    byte[] imageInByte = cursor.getBlob(cursor.getColumnIndex("itemImage"));
                    Bitmap image = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
                    Item i = new Item("Item : " + name, "Location: " + location, "Description: \n" + description,"Time: " + time,"Phone: " + phone, "User name: " +username, image);
                    list.add(i);
                    Log.d("get Lost Item", i.toString());
                    cursor.moveToNext();
                }
            }
        }
        return list;
    }
}
