

package com.tetris.tetris;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database_Code extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "codes.db";
    public static final String TABLE_NAME = "codes_table";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_RANDOM_CODE = "random_code";
    public static final String COLUMN_IS_USED = "is_used";

    public Database_Code(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RANDOM_CODE + " TEXT, " +
                COLUMN_IS_USED + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long InsertCode(String randomCode, boolean isUsed) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RANDOM_CODE, randomCode);
        contentValues.put(COLUMN_IS_USED, isUsed ? 1 : 0);
        return sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public boolean UpdateIsUsedStatus(String randomCode, boolean isUsed) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IS_USED, isUsed ? 1 : 0);
        int rowsAffected = sqLiteDatabase.update(TABLE_NAME, contentValues,
                COLUMN_RANDOM_CODE + "=?", new String[]{randomCode});
        return rowsAffected > 0;
    }

    public boolean CheckCodeExistence(String randomCode) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_RANDOM_CODE + "=?", new String[]{randomCode});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean DeleteCode(String randomCode) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int rowsAffected = sqLiteDatabase.delete(TABLE_NAME,
                COLUMN_RANDOM_CODE + "=?", new String[]{randomCode});
        return rowsAffected > 0;
    }
}