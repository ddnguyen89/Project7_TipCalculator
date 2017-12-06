package com.murach.tipcalculator.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.murach.tipcalculator.Tip;
import com.murach.tipcalculator.TipCalculatorActivity;

import java.util.ArrayList;

/**
 * Created by Davis Duy Nguyen on 12/1/2017.
 */

public class Database extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tips.db";
    public static final String TABLE_TIPS = "tips";
    public static final String COLUMN_ID = "id";
    public static final int COLUMN_ID_COL = 0;
    public static final String BILL_DATE = "dateMillis";
    public static final int BILL_DATE_COL = 1;
    public static final String BILL_AMOUNT = "billAmount";
    public static final int BILL_AMOUNT_COL = 2;
    public static final String TIP_PERCENT = "tipPercent";
    public static final int TIP_PERCENT_COL = 3;

    private SQLiteDatabase database;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_TIPS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BILL_DATE + " INTEGER NOT NULL, " +
                BILL_AMOUNT + " REAL NOT NULL," +
                TIP_PERCENT + " REAL NOT NULL);";
        db.execSQL(createTable);

        String insertTable = "INSERT INTO tips VALUES('null', 0, 40.60, .15);";

        try {
            db.execSQL("INSERT INTO tips VALUES('null', 0, 40.60, .15)");
        } catch (SQLiteException e) {
            Log.d("tipcalculator", "ERROR");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE if EXISTS " + TABLE_TIPS);

        onCreate(db);
    }

    public Database open() throws SQLiteException {

        database = getWritableDatabase();   //get reference to the database

        return this;
    }

    public ArrayList<Tip> getTips() {

        ArrayList<Tip> objects = new ArrayList<Tip>();

        this.getReadableDatabase();

        String[] allColumns = new String[] {
                COLUMN_ID, BILL_DATE, BILL_AMOUNT, TIP_PERCENT
        };

        Cursor c = database.query(TABLE_TIPS, null, null, null, null, null, null);

        while(c.moveToNext()) {
            Tip object = new Tip();
            object.setId(c.getInt(COLUMN_ID_COL));
            object.setDateMillis(c.getInt(BILL_DATE_COL));
            object.setBillAmount(c.getInt(BILL_AMOUNT_COL));
            object.setTipPercent(c.getInt(TIP_PERCENT_COL));

            objects.add(object);
        }

        if(c != null) {
            c.close();
        }

        return objects;
    }

    private static Tip getTipFromCursor(Cursor c) {
        if(c == null || c.getCount() == 0) {
            return null;
        } else {
            try {
                Tip tip = new Tip(
                        c.getLong(Integer.parseInt(COLUMN_ID)),
                        c.getLong(Integer.parseInt(BILL_DATE)),
                        c.getFloat(Integer.parseInt(BILL_AMOUNT)),
                        c.getFloat(Integer.parseInt(TIP_PERCENT)));
                return tip;
            } catch(Exception e) {
                return null;
            }
        }
    }
}
