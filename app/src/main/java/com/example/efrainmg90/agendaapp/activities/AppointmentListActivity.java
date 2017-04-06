package com.example.efrainmg90.agendaapp.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.efrainmg90.agendaapp.DAL.AppointmentDAL;
import com.example.efrainmg90.agendaapp.Helpers.ScheduleDBHelper;
import com.example.efrainmg90.agendaapp.R;
import com.example.efrainmg90.agendaapp.models.Appointment;
import com.example.efrainmg90.agendaapp.ui.AppointmentListViewAdapter;

import java.util.List;

public class AppointmentListActivity extends AppCompatActivity {
    ListView listView;
    FloatingActionButton actionButtonAdd;
    AppointmentListViewAdapter appointmentListViewAdapter;
    ScheduleDBHelper scheduleDBHelper;
    AppointmentDAL dalAppointment;
    List<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        actionButtonAdd = (FloatingActionButton) findViewById(R.id.action_bar_add);

        dalAppointment = new AppointmentDAL(this);
        dalAppointment.open();
        appointmentList = dalAppointment.getAllAppointments();
        dalAppointment.close();

        bootstrapList();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        dalAppointment.open();
        if (appointmentList!=null){
            appointmentList = dalAppointment.getAllAppointments();
            appointmentListViewAdapter = new AppointmentListViewAdapter(this,appointmentList);
            appointmentListViewAdapter.notifyDataSetChanged();
        }
        dalAppointment.close();
    }

    private void bootstrapList(){
        appointmentListViewAdapter = new AppointmentListViewAdapter(this,appointmentList);
        listView = (ListView) findViewById(R.id.lvAppointmentsList);
        listView.setAdapter(appointmentListViewAdapter);
        actionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentListActivity.this,SaveAppointmentActivity.class);
                startActivity(intent);
                //Toast.makeText(AppointmentListActivity.this, "Agregar nuevo Apointment", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Dialog dialog = new Dialog(AppointmentListActivity.this);
                View options = LayoutInflater.from(AppointmentListActivity.this).inflate(R.layout.layout_options_listview,null);
                AlertDialog.Builder builder  = new AlertDialog.Builder(AppointmentListActivity.this);
                builder.setTitle("Contactos: ");
                builder.setView(options);
                builder.setNegativeButton("Cancelar", null);
                builder.show();
                Vibrator vibrate = (Vibrator)AppointmentListActivity.this.getSystemService(VIBRATOR_SERVICE);
                vibrate.vibrate(200);
                
                Button btnOpEdit = (Button) options.findViewById(R.id.button_edit);
                btnOpEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(AppointmentListActivity.this, "Clic en editar", Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }
        });
    }// end bootstrapList




}
