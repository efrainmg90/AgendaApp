package com.example.efrainmg90.agendaapp.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.efrainmg90.agendaapp.Helpers.ScheduleDBHelper;
import com.example.efrainmg90.agendaapp.models.Appointment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Efrainmg90 on 01/04/2017.
 */

public class AppointmentDAL {
    public static final String LOGTAG = "APPOINTMENT_DAL";

    SQLiteOpenHelper dbScheduleHelper;
    SQLiteDatabase database;
    Context context;

    private static final String[] allColumns = {
            ScheduleDBHelper.COLUMN_ID,
            ScheduleDBHelper.COLUMN_TITLE,
            ScheduleDBHelper.COLUMN_DESCRIPTION,
            ScheduleDBHelper.COLUMN_DATE,
            ScheduleDBHelper.COLUMN_HOUR
    };


    public AppointmentDAL(Context context) {
        dbScheduleHelper = new ScheduleDBHelper(context);
        this.context = context;
    }

    public void open() {
        Log.i(LOGTAG, "Database Opened");
        database = dbScheduleHelper.getWritableDatabase();
    }

    public void close() {
        Log.i(LOGTAG, "Database Closed");
        dbScheduleHelper.close();
    }

    public Appointment addAppointment(Appointment appointment) {
        ContentValues values = new ContentValues();
        values.put(ScheduleDBHelper.COLUMN_TITLE, appointment.getTitle());
        values.put(ScheduleDBHelper.COLUMN_DESCRIPTION, appointment.getDescription());
        values.put(ScheduleDBHelper.COLUMN_DATE, appointment.getDate());
        values.put(ScheduleDBHelper.COLUMN_HOUR, appointment.getHour());
        long id = database.insert(ScheduleDBHelper.TABLE_APPOINTMENT, null, values);
        appointment.setId(id);
        return appointment;

    }// end addApointment


    public Appointment getAppointment(long id) {
        Appointment appointment = new Appointment();
        Cursor cursor = database.query(ScheduleDBHelper.TABLE_APPOINTMENT, allColumns, ScheduleDBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            appointment.setId(Long.parseLong(cursor.getString(0)));
            appointment.setTitle(cursor.getString(1));
            appointment.setDescription(cursor.getString(2));
            appointment.setDate(cursor.getString(3));
            appointment.setDate(cursor.getString(4));
        }

        return appointment;
    }//end get Appointment

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointmentList = new ArrayList<Appointment>();
        Cursor cursor = database.query(ScheduleDBHelper.TABLE_APPOINTMENT, allColumns, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Appointment appointment = new Appointment();
                appointment.setId(cursor.getLong(cursor.getColumnIndex(ScheduleDBHelper.COLUMN_ID)));
                appointment.setTitle(cursor.getString(cursor.getColumnIndex(ScheduleDBHelper.COLUMN_TITLE)));
                appointment.setDescription(cursor.getString(cursor.getColumnIndex(ScheduleDBHelper.COLUMN_DESCRIPTION)));
                appointment.setDate(cursor.getString(cursor.getColumnIndex(ScheduleDBHelper.COLUMN_DATE)));
                appointment.setHour(cursor.getString(cursor.getColumnIndex(ScheduleDBHelper.COLUMN_HOUR)));

                appointmentList.add(appointment);
            }
        }
        return appointmentList;
    }// end getAllAppointments

    public int updateAppointment(Appointment appointment) {
        ContentValues values = new ContentValues();
        values.put(ScheduleDBHelper.COLUMN_TITLE, appointment.getTitle());
        values.put(ScheduleDBHelper.COLUMN_DESCRIPTION, appointment.getDescription());
        values.put(ScheduleDBHelper.COLUMN_DATE, appointment.getDate());
        values.put(ScheduleDBHelper.COLUMN_HOUR, appointment.getHour());

        return database.update(ScheduleDBHelper.TABLE_APPOINTMENT, values, ScheduleDBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(appointment.getId())});
    }//end getupdateAppointment


    public int deleteAppointment(Appointment appointment) {
        return database.delete(ScheduleDBHelper.TABLE_APPOINTMENT, ScheduleDBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(appointment.getId())});
    }//end deleteAppointment

    public List<Long> getContactsItemsSelected(long idAppointment) {
        List<Long> contactsList = new ArrayList<Long>();
        Cursor cursor = database.query(ScheduleDBHelper.TABLE_APPOINTMENT_CONTACTS, null, ScheduleDBHelper.COLUMN_ID_APPOINT + "=?",
                new String[]{String.valueOf(idAppointment)}, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Long idContact = cursor.getLong(cursor.getColumnIndex(ScheduleDBHelper.COLUMN_ID_CONTACT));
                contactsList.add(idContact);
            }
        }
        return contactsList;
    }

    public void addContactToEvent(long id_contact, long id_appointment) {
        ContentValues values = new ContentValues();
        values.put(ScheduleDBHelper.COLUMN_ID_CONTACT, id_contact);
        values.put(ScheduleDBHelper.COLUMN_ID_APPOINT, id_appointment);
        long id = database.insert(ScheduleDBHelper.TABLE_APPOINTMENT_CONTACTS, null, values);
        Log.d("AppointmentDAL:", "Se genero el id: " + id);
    }

    public int deleteContactsEvent(long id_appointment) {
        return database.delete(ScheduleDBHelper.TABLE_APPOINTMENT_CONTACTS, ScheduleDBHelper.COLUMN_ID_APPOINT + "=?",
                new String[]{String.valueOf(id_appointment)});
    }


}
