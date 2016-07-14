package com.example.kang.lottohelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Kang on 6/22/16.
 */
public class SQLiteHelper {

    String TAG = "DB helper";
    public static final String dbName = "Lotto.db";
    public static final String tableName = "numList";
    private static final String QUERY_CREATE_NUMLIST_TABLE =
            "CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, weekNum INTEGER NOT NULL, bonusNum INTEGER, firstWinnerPrize INTEGER, pickDay text, number1 INTEGER, number2 INTEGER, number3 INTEGER, number4 INTEGER, number5 INTEGER, number6 INTEGER, totalPrize INTEGER, howManyFirstWinner INTEGER)";
    private SQLiteDatabase mDB;
    private dbOepnHelper mDBHelper;
    private Context mCtx;
    public Cursor mCursor;

    private class dbOepnHelper extends SQLiteOpenHelper {
        public dbOepnHelper(Context context) {
            super(context, dbName, null, 1);
            Log.d(TAG, "DB helper created : " + dbName);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(QUERY_CREATE_NUMLIST_TABLE);
                Log.d(TAG, "Table created : " + dbName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public SQLiteHelper(Context context) {
        this.mCtx = context;
    }

    public SQLiteHelper open() throws SQLException {
        mDBHelper = new dbOepnHelper(mCtx);
        mDB = mDBHelper.getWritableDatabase();
        Log.d(TAG, "DB created or open : " + mDB.toString());
        return this;
    }

    public void close() {
        mDB.close();
    }

    public int getLastWeek() {
        int result = 0;
        try {
            mCursor = this.query(tableName, null, null, null, null, null, "weekNum");
            mCursor.moveToLast();
            if (mCursor != null && mCursor.getPosition() > 0) {
                result = mCursor.getInt(mCursor.getColumnIndex("weekNum"));
            }
            Log.d(TAG, "Last week Cursor Position : " + result + "(" + String.valueOf(mCursor.getPosition()) + " / " + String.valueOf(mCursor.getCount()) + ")");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCursor.close();
        }
        return result;
    }


    public void viewTable() {
        try {
            mCursor = this.query(tableName, null, null, null, null, null, "_id");
            Log.d(TAG, "Cursor Position : " + String.valueOf(mCursor.getPosition()) + " / " + String.valueOf(mCursor.getCount()));

            mCursor.moveToFirst();

            if (mCursor != null && mCursor.getCount() > 0) {
                while (mCursor.getPosition() < mCursor.getCount()) {
                    String number1 = mCursor.getString(mCursor.getColumnIndex("number1"));
                    String number2 = mCursor.getString(mCursor.getColumnIndex("number2"));
                    String number3 = mCursor.getString(mCursor.getColumnIndex("number3"));
                    String number4 = mCursor.getString(mCursor.getColumnIndex("number4"));
                    String number5 = mCursor.getString(mCursor.getColumnIndex("number5"));
                    String number6 = mCursor.getString(mCursor.getColumnIndex("number6"));
                    String week = mCursor.getString(mCursor.getColumnIndex("weekNum"));
                    Log.d(TAG, "Week : " + week + " / Numbers (" + number1 + ", " + number2 + ", " + number3 + ", " + number4 + ", " + number5 + ", " + number6 + ")");
                    mCursor.moveToNext();
                }
            } else {
                Log.d(TAG, "Viewing Error : cursor is null");
            }
        } catch (Exception e) {
            Log.d(TAG, "Excetption : Cursor is wrong");
            e.printStackTrace();
        } finally {
            mCursor.close();
        }

    }

    public long addWeek(aWeekInfo weekInfo) {

        ContentValues values = new ContentValues();

        values.put("weekNum", weekInfo.weekNum);
        values.put("bonusNum", weekInfo.bonusNum);
        values.put("firstWinnerPrize", weekInfo.firstWinnerPrize);
        values.put("pickDay", String.valueOf(weekInfo.pickDay));
        values.put("number1", weekInfo.number1);
        values.put("number2", weekInfo.number2);
        values.put("number3", weekInfo.number3);
        values.put("number4", weekInfo.number4);
        values.put("number5", weekInfo.number5);
        values.put("number6", weekInfo.number6);
        values.put("totalPrize", weekInfo.totalPrize);
        values.put("howManyFirstWinner", weekInfo.howManyFirstWinner);

        long result = mDB.insert(tableName, null, values);
        Log.d("return cursor", String.valueOf(result));
        return result;
    }


    public int[][] getNumFrequency(int startWeek, int currentWeek) {

        Log.d(TAG, "From " + startWeek + " to " + currentWeek);

        int[][] temp = new int[45][5];
        String[] mColumns = {"_id", "weekNum", "number1", "number2", "number3", "number4", "number5", "number6", "bonusNum", "firstWinnerPrize"};

        String[] weekStr = {String.valueOf(startWeek - 1), String.valueOf(currentWeek + 1)};

        try {
            mCursor = this.query(tableName, mColumns, "? < weekNum and weekNum < ?", weekStr, null, null, null);
            Log.d(TAG, "Select info cursor numbers is " + mCursor.getCount());

            mCursor.moveToFirst();
            Log.d(TAG, "First cursor = " + mCursor.getPosition());

            for (int i = 0; i < 45; i++) {
                temp[i][0] = i + 1;
            }
            while (!mCursor.isAfterLast()) {
                for (int i = 1; i < 7; i++) {
                    int t = mCursor.getInt(mCursor.getColumnIndex("number" + String.valueOf(i)));
                    temp[t - 1][1] = temp[t - 1][1] + 1;
                    temp[t - 1][2] = mCursor.getInt(mCursor.getColumnIndex("weekNum"));
                }
                int t = mCursor.getInt(mCursor.getColumnIndex("bonusNum"));
                temp[t - 1][3] = temp[t - 1][3] + 1;
                temp[t - 1][4] = mCursor.getInt(mCursor.getColumnIndex("weekNum"));

                mCursor.moveToNext();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return temp;
    }


    public long getPrize() {
        long temp = 0;
        try {
            mCursor.moveToFirst();

            while (!mCursor.isAfterLast()) {
                temp = temp + mCursor.getLong(mCursor.getColumnIndex("firstWinnerPrize"));
                mCursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCursor != null && mCursor.getCount() > 0)
            temp = temp / mCursor.getCount();
        mCursor.close();

        return temp;

    }


    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        Cursor cursor = null;

        try {

            cursor = mDB.query(table, columns, selection, selectionArgs,
                    groupBy, having, orderBy);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    public int[][] getNumFrequency(int startWeek, int currentWeek, String[] columns) {

        Log.d(TAG, "From " + startWeek + " to " + currentWeek);

        int[][] temp = new int[45][3];
        String[] mColumns = {"_id", "weekNum", "number1", "number2", "number3", "number4", "number5", "number6", "bonusNum", "firstWinnerPrize"};

        String[] weekStr = {String.valueOf(startWeek - 1), String.valueOf(currentWeek + 1)};

        try {
            mCursor = this.query(tableName, mColumns, "? < weekNum and weekNum < ?", weekStr, null, null, null);
            Log.d(TAG, "Select info cursor numbers is " + mCursor.getCount());

            mCursor.moveToFirst();
            Log.d(TAG, "First cursor = " + mCursor.getPosition());

            for (int i = 0; i < 45; i++) {
                temp[i][0] = i + 1;
            }

            while (!mCursor.isAfterLast()) {

                for (int i = 1; i < 7; i++) {
                    int t = mCursor.getInt(mCursor.getColumnIndex("number" + String.valueOf(i)));
                    temp[t - 1][1] = temp[t - 1][1] + 1;
                }

                int t = mCursor.getInt(mCursor.getColumnIndex("bonusNum"));
                temp[t - 1][2] = temp[t - 1][2] + 1;
                mCursor.moveToNext();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return temp;
    }
}