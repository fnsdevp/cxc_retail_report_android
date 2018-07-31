package com.hipla.retail.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hipla.retail.model.Profile;
import com.hipla.retail.model.ZoneInfo;

import java.util.ArrayList;

import io.paperdb.Paper;

/**
 * Created by FNSPL on 8/25/2017.
 */

public class Db_helper extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "retail.db";
    public static final String TAG = "retail";
    //+ Db_contracts.Entries._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
    public static final String SQL_CREATE_TABLE_USER = "CREATE TABLE " + Db_contracts.Entries.TABLE_USER + " ( " +
            Db_contracts.Entries.COLUMN_UID + " INTEGER PRIMARY KEY , " +
            Db_contracts.Entries.COLUMN_EMAIL + " TEXT NOT NULL UNIQUE , " +
            Db_contracts.Entries.COLUMN_MOBILE + " INTEGER NOT NULL UNIQUE , " +
            Db_contracts.Entries.COLUMN_FNAME + " TEXT , " +
            Db_contracts.Entries.COLUMN_LNAME + " TEXT , " +
            Db_contracts.Entries.COLUMN_PIN + " TEXT , " +
            Db_contracts.Entries.COLUMN_USER_TYPE + " TEXT , " +
            Db_contracts.Entries.COLUMN_LOCATION + " TEXT )";

    public static final String SQL_CREATE_TABLE_PRODUCT = "CREATE TABLE " + Db_contracts.Product.TABLE_PRODUCT + " ( " +
            Db_contracts.Product.COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY , " +
            Db_contracts.Product.COLUMN_PRODUCT_NAME + " TEXT , " +
            Db_contracts.Product.COLUMN_PRODUCT_IMAGE + " TEXT , " +
            Db_contracts.Product.COLUMN_PRICE + " TEXT , " +
            Db_contracts.Product.COLUMN_QUANTITY + " INTEGER )";

    public static final String SQL_CREATE_TABLE_ZONEINFO = "CREATE TABLE " + Db_contracts.ZoneInfo.TABLE_ZONE + " ( " +
            Db_contracts.ZoneInfo.COLUMN_ZONE_ID + " INTEGER PRIMARY KEY , " +
            Db_contracts.ZoneInfo.COLUMN_CENTER + " TEXT , " +
            Db_contracts.ZoneInfo.COLUMN_POINT_A + " TEXT , " +
            Db_contracts.ZoneInfo.COLUMN_POINT_B + " TEXT , " +
            Db_contracts.ZoneInfo.COLUMN_POINT_C + " TEXT , " +
            Db_contracts.ZoneInfo.COLUMN_POINT_D + " TEXT )";


    public Db_helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_TABLE_ZONEINFO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insert_zone(ZoneInfo zoneInfo) {
        try {
            ContentValues values = new ContentValues();
            values.put(Db_contracts.ZoneInfo.COLUMN_ZONE_ID, zoneInfo.getId());
            values.put(Db_contracts.ZoneInfo.COLUMN_CENTER, zoneInfo.getCenterPoint());
            values.put(Db_contracts.ZoneInfo.COLUMN_POINT_A, zoneInfo.getPointA());
            values.put(Db_contracts.ZoneInfo.COLUMN_POINT_B, zoneInfo.getPointB());
            values.put(Db_contracts.ZoneInfo.COLUMN_POINT_C, zoneInfo.getPointC());
            values.put(Db_contracts.ZoneInfo.COLUMN_POINT_D, zoneInfo.getPointD());

            if (!getZoneId(zoneInfo.getId())) {

                getWritableDatabase().insert(Db_contracts.ZoneInfo.TABLE_ZONE, null, values);

                Log.d(TAG, "Zone Added");
                return true;
            } else {

                getWritableDatabase().update(Db_contracts.ZoneInfo.TABLE_ZONE, values, Db_contracts.ZoneInfo.COLUMN_ZONE_ID + "=" + zoneInfo.getId(), null);

                Log.d(TAG, "Zone updated");
                return false;
            }
        } finally {
            try {
                getWritableDatabase().close();
            } catch (Exception ignore) {
            }
        }

    }

    public boolean getZoneId(int uid) {
        String read_query = " SELECT " + Db_contracts.ZoneInfo.COLUMN_ZONE_ID + " FROM " + Db_contracts.ZoneInfo.TABLE_ZONE;
        Cursor cursor = getReadableDatabase().rawQuery(read_query, null);

        try {
            if (cursor == null)
                return false;

            if (cursor.moveToFirst()) {
                Log.d(TAG, "getUid: " + cursor.getInt(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_ZONE_ID)));
                do {
                    if (uid == cursor.getInt(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_ZONE_ID))) {
                        return true;
                    }
                }while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {

        } finally {
            try {
                cursor.close();
            } catch (Exception ignore) {
            }
        }
        return false;
    }

    public ZoneInfo getZoneInfo(String uid) {
        SQLiteDatabase db = getReadableDatabase();
        ZoneInfo zoneInfo = null;
        try {
            String[] params = new String[]{uid};

            Cursor cursor = db.query(Db_contracts.ZoneInfo.TABLE_ZONE, null,
                    Db_contracts.ZoneInfo.COLUMN_ZONE_ID + " = ?", params,
                    null, null, null);

            try {
                if (cursor != null && cursor.moveToNext()) {
                    Log.d("dev", "database read: " + cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_CENTER)));
                    zoneInfo = new ZoneInfo();
                    zoneInfo.setId(cursor.getInt(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_ZONE_ID)));
                    zoneInfo.setCenterPoint(cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_CENTER)));
                    zoneInfo.setPointA(cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_POINT_A)));
                    zoneInfo.setPointB(cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_POINT_B)));
                    zoneInfo.setPointC(cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_POINT_C)));
                    zoneInfo.setPointD(cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_POINT_D)));
                }
            } catch (SQLiteException e) {

            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }
        return zoneInfo;
    }

    public ArrayList<ZoneInfo> getAllZoneInfo() {

        ArrayList<ZoneInfo> list = new ArrayList<ZoneInfo>();

        String selectQuery = "SELECT  * FROM " + Db_contracts.ZoneInfo.TABLE_ZONE;

        SQLiteDatabase db = this.getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, null);
            try {

                if (cursor.moveToFirst()) {
                    do {
                        ZoneInfo zoneInfo = new ZoneInfo();
                        zoneInfo.setId(cursor.getInt(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_ZONE_ID)));
                        zoneInfo.setCenterPoint(cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_CENTER)));
                        zoneInfo.setPointA(cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_POINT_A)));
                        zoneInfo.setPointB(cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_POINT_B)));
                        zoneInfo.setPointC(cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_POINT_C)));
                        zoneInfo.setPointD(cursor.getString(cursor.getColumnIndex(Db_contracts.ZoneInfo.COLUMN_POINT_D)));

                        list.add(zoneInfo);
                    } while (cursor.moveToNext());
                }

            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }

        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }

        return list;
    }

}
