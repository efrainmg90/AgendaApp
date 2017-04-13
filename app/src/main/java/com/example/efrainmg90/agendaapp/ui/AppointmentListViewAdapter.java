package com.example.efrainmg90.agendaapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.efrainmg90.agendaapp.R;
import com.example.efrainmg90.agendaapp.models.Appointment;

import java.util.List;

/**
 * Created by Efrainmg90 on 02/04/2017.
 */

public class AppointmentListViewAdapter extends BaseAdapter {
    private List<Appointment> appointmentList;
    Context context;
    private LayoutInflater inflater = null;

    public AppointmentListViewAdapter(Context context,List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @Override
    public int getCount() {
        return appointmentList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView title, description, date,hour;
        if(view==null)
            view = LayoutInflater.from(context).inflate(R.layout.layout_appointment_list,null);

        title = (TextView) view.findViewById(R.id.txvTitleList);
        description = (TextView) view.findViewById(R.id.txvDescriptionList);
        date = (TextView) view.findViewById(R.id.txvDateList);
        hour = (TextView) view.findViewById(R.id.txvHourList);

        Appointment appointment = appointmentList.get(i);
        title.setText(appointment.getTitle());
        description.setText(appointment.getDescription());
        date.setText(appointment.getDate());
        hour.setText(appointment.getHour());

        return view;
    }
}
