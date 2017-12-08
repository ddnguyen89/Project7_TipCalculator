package nguyen.tipcalculator.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import nguyen.tipcalculator.Tip;

import java.util.ArrayList;

/**
 * Created by Davis Duy Nguyen on 12/1/2017.
 */

public class Database extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tips.db";
    public static final String TABLE_TIPS = "tips";
    public static final String COLUMN_ID = "id";
    public static final String BILL_DATE = "dateMillis";
    public static final String BILL_AMOUNT = "billAmount";
    public static final String TIP_PERCENT = "tipPercent";

    SQLiteDatabase database;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_TIPS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BILL_DATE + " INTEGER, " +
                BILL_AMOUNT + " REAL, " +
                TIP_PERCENT + " REAL);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE if EXISTS " + TABLE_TIPS);

        onCreate(db);
    }

    public void addTip(Tip tip) {
        open();

        ContentValues values = new ContentValues();
        values.put(BILL_DATE, tip.getDateMillis());
        values.put(BILL_AMOUNT, tip.getBillAmount());
        values.put(TIP_PERCENT, tip.getTipPercent());

        database.insert(TABLE_TIPS, null, values);

        database.close();

    }

    public Database open() throws SQLiteException {

        database = getWritableDatabase();

        return this;
    }

    public ArrayList<Tip> getTips() {

        ArrayList<Tip> objects = new ArrayList<Tip>();

        open();

        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_TIPS, null);

        if(c.moveToFirst()) {
            do {
                Tip object = new Tip();

                object.setId(Integer.parseInt(c.getString(0)));
                object.setDateMillis(Long.parseLong((c.getString(1))));
                object.setBillAmount(Float.parseFloat(c.getString(2)));
                object.setTipPercent(Float.parseFloat(c.getString(3)));

                objects.add(object);
            } while(c.moveToNext());
        }

        return objects;
    }

    public Cursor getTip() {
        String[] allColumns = new String[] {
                COLUMN_ID, BILL_DATE, BILL_AMOUNT, TIP_PERCENT
        };

        Cursor c = database.query(TABLE_TIPS, allColumns, null, null, null, null, null);

        if(c != null) {
            c.moveToFirst();
        }

        return c;
    }

    public float getAverage() {
        String[] columns = {"AVG(" + TIP_PERCENT + ")"};

        Cursor c = database.rawQuery("SELECT AVG(" + TIP_PERCENT + ") FROM " + TABLE_TIPS, null);

        c.moveToFirst();


        float avgTipPercent = Float.parseFloat(c.getString(0));

        return avgTipPercent;
    }

    public void deleteAllTips() {
        open();

        database.execSQL("DELETE FROM " + TABLE_TIPS + ";");
    }

    public int getCount() {
        open();

        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_TIPS, null);

        return c.getCount();
    }
}
