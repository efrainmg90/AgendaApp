package com.example.efrainmg90.agendaapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.efrainmg90.agendaapp.R;
import com.example.efrainmg90.agendaapp.models.Appointment;

public class UpdateAppointmentActivity extends AppCompatActivity {
    Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_appointment);

        appointment = (Appointment) getIntent().getSerializableExtra("appointment");
        Toast.makeText(this, appointment.toString(), Toast.LENGTH_SHORT).show();
    }
}
