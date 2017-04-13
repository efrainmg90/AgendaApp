package com.example.efrainmg90.agendaapp.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Efrainmg90 on 01/04/2017.
 */

public class ScheduleDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "schedule.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_APPOINTMENT = "appointment";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_HOUR = "hour";

    public static final String TABLE_APPOINTMENT_CONTACTS = "appointmentDetail";
    public static final String COLUMN_ID_APPOINT = "appointment_id";
    public static final String COLUMN_ID_CONTACT = "contact_id";

    private static final String TABLE_CREATE_APPOINTMENT =
            "CREATE TABLE " + TABLE_APPOINTMENT + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_HOUR + " TEXT " +
                    ")";
    private static final String TABLE_CREATE_CONTACT_APPOINT =
            "CREATE TABLE " + TABLE_APPOINTMENT_CONTACTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ID_APPOINT + " INTEGER, " +
                    COLUMN_ID_CONTACT + " INTEGER " +
                    ")";


    public ScheduleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE_APPOINTMENT);
        sqLiteDatabase.execSQL(TABLE_CREATE_CONTACT_APPOINT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_APPOINTMENT_CONTACTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_APPOINTMENT);

        onCreate(sqLiteDatabase);
    }
}
