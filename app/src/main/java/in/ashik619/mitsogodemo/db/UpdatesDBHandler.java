package in.ashik619.mitsogodemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import in.ashik619.mitsogodemo.data.UpdateData;


/**
 * Created by dilip on 3/1/17.
 */

public class UpdatesDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "updates.db";
    private static final String TABLE_UPDATES = "updates";
    private static final String COLUMN_WEATHER = "_weather";
    private static final String COLUMN_BATTERY = "_battery";
    private static final String COLUMN_STORAGE = "_storage";
    private static final String COLUMN_NETWORK = "_network";
    private static final String COLUMN_DEVICE = "_device";

    private static UpdatesDBHandler instance;

    public static synchronized UpdatesDBHandler getInstance(Context context)
    {
        if (instance == null)
            instance = new UpdatesDBHandler(context,DATABASE_NAME,null,DATABASE_VERSION);

        return instance;
    }

    private UpdatesDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_UPDATES + "(" +
                COLUMN_WEATHER + " TEXT, " +
                COLUMN_BATTERY + " TEXT, " +
                COLUMN_STORAGE + " TEXT, " +
                COLUMN_DEVICE + " TEXT, " +
                COLUMN_NETWORK + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_UPDATES);
        onCreate(db);
    }

    public void putUpdateDataData(UpdateData data) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEATHER, data.getWeatherResponse());
        values.put(COLUMN_BATTERY, data.getBattery());
        values.put(COLUMN_NETWORK, data.getNetworkType());
        values.put(COLUMN_STORAGE, data.getDeviceStorage());
        values.put(COLUMN_DEVICE, data.getDeviceName());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_UPDATES,null,values);
        db.close();
    }


    public void clearTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_UPDATES+" WHERE 1;");
        db.close();
    }

    public List<UpdateData> fetchUpdateData() {
        List<UpdateData> updateDataList = new ArrayList<>();
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.query(
                TABLE_UPDATES,
                new String[]{COLUMN_WEATHER, COLUMN_BATTERY, COLUMN_NETWORK,COLUMN_DEVICE,COLUMN_STORAGE},
                null,
                null,
                null,
                null,
                null
        );
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                while(cursor.isAfterLast() == false){
                    UpdateData data = new UpdateData();
                    if(!cursor.isNull(cursor.getColumnIndex(COLUMN_WEATHER))) {
                        data.setWeatherResponse(cursor.getString(cursor.getColumnIndex(COLUMN_WEATHER)));
                    }
                    if(!cursor.isNull(cursor.getColumnIndex(COLUMN_BATTERY))) {
                        data.setBattery(cursor.getString(cursor.getColumnIndex(COLUMN_BATTERY)));
                    }
                    if(!cursor.isNull(cursor.getColumnIndex(COLUMN_NETWORK))) {
                        data.setNetworkType(cursor.getString(cursor.getColumnIndex(COLUMN_NETWORK)));
                    }
                    if(!cursor.isNull(cursor.getColumnIndex(COLUMN_STORAGE))) {
                        data.setDeviceStorage(cursor.getString(cursor.getColumnIndex(COLUMN_STORAGE)));
                    }
                    if(!cursor.isNull(cursor.getColumnIndex(COLUMN_DEVICE))) {
                        data.setDeviceName(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE)));
                    }
                    updateDataList.add(data);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
               // Crashlytics.logException(e);
                e.printStackTrace();
            }finally {
                cursor.close();
            }
        }
        return updateDataList;
    }
}
