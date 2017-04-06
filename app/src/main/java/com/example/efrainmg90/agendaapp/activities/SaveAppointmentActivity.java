package com.example.efrainmg90.agendaapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.efrainmg90.agendaapp.DAL.AppointmentDAL;
import com.example.efrainmg90.agendaapp.Helpers.ContactsLoader;
import com.example.efrainmg90.agendaapp.Helpers.ScheduleDBHelper;
import com.example.efrainmg90.agendaapp.R;
import com.example.efrainmg90.agendaapp.models.Appointment;
import com.example.efrainmg90.agendaapp.models.Contact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SaveAppointmentActivity extends AppCompatActivity {


    Button buttonSave;
    TextView titleContactsAdd;
    EditText title,description,date;
    ArrayAdapter arrayAdapterContacts ;
    List<Contact> contactList;
    List<String> contactNameList;
    ImageView imageViewContact;
    SparseBooleanArray checkedContacs;

    ScheduleDBHelper scheduleDBHelper;
    AppointmentDAL dalAppointment;


    public class LoadingContactsTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            loadContactNames();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadListView();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_appointment);

        title = (EditText) findViewById(R.id.edt_title);
        description = (EditText) findViewById(R.id.edt_description);
        date = (EditText) findViewById(R.id.edt_date);
        buttonSave = (Button) findViewById(R.id.btn_save_appointment_with_contacs);

        imageViewContact = (ImageView) findViewById(R.id.imvIcont_add_contact);
        titleContactsAdd = (TextView) findViewById(R.id.title_img_contact);
        contactNameList = new ArrayList<>();


        new LoadingContactsTask().execute();
        arrayAdapterContacts = new ArrayAdapter(this, android.R.layout.select_dialog_multichoice,contactNameList);




        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SaveAppointmentActivity.this);
                    final DatePicker picker = new DatePicker(SaveAppointmentActivity.this);
                    builder.setTitle("Fecha: ");
                    builder.setView(picker);
                    builder.setNegativeButton("Cancelar", null);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int year = picker.getYear();
                            int month = picker.getMonth();
                            int day = picker.getDayOfMonth();

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, day);

                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            String strDate = format.format(calendar.getTime());
                            date.setText(strDate);
                        }
                    });
                    builder.show();
                }
            }
        });// end on Focuslistener

        imageViewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ListView listViewContacts = new ListView(SaveAppointmentActivity.this);
                listViewContacts.setAdapter(arrayAdapterContacts);
                listViewContacts.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                AlertDialog.Builder builder  = new AlertDialog.Builder(SaveAppointmentActivity.this);
                builder.setTitle("Contactos: ");
                builder.setView(listViewContacts);
                builder.setNegativeButton("Cancelar", null);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkedContacs = listViewContacts.getCheckedItemPositions();
                        showContactsSelected();

                    }
                });
                builder.show();
            }

        });//end on ClickListener

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAppointmenWithContacts();
                Snackbar.make(view,"Evento Guardado",Snackbar.LENGTH_SHORT);
                Intent intent = new Intent(SaveAppointmentActivity.this,AppointmentListActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }// end Oncreate

    private void loadContactNames(){
        ContactsLoader loader = new ContactsLoader(this);
        contactList = loader.getContacts();
        contactNameList = new ArrayList<String>();
        for (Contact contact:contactList) {
            String name = contact.getName();
            contactNameList.add(name);
        }
    }

    public void loadListView(){
        arrayAdapterContacts.clear();
        arrayAdapterContacts = new ArrayAdapter(this, android.R.layout.select_dialog_multichoice,contactNameList);
        arrayAdapterContacts.notifyDataSetChanged();

    }

    public void showContactsSelected(){
        String strContacts = "Contactos Agregados: ";
        for (int i = 0; i < contactNameList.size(); i++)
            if (checkedContacs.get(i)) {
                Toast.makeText(this, contactNameList.get(i), Toast.LENGTH_SHORT).show();
                String item = contactNameList.get(i);
                strContacts = strContacts +"*"+ item+"  ";
  /* do whatever you want with the checked item */
            }
            titleContactsAdd.setText(strContacts);
    }

    public void saveAppointmenWithContacts(){
        dalAppointment = new AppointmentDAL(this);
        dalAppointment.open();
        Appointment appointment = new Appointment();
        appointment.setTitle(title.getText().toString());
        appointment.setDescription(description.getText().toString());
        appointment.setDate(date.getText().toString());
        appointment = dalAppointment.addAppointment(appointment);
        Toast.makeText(this, "Se guardo el evento: "+appointment.toString(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < contactNameList.size(); i++)
            if (checkedContacs.get(i)) {
                dalAppointment.addContactToEvent(Long.parseLong(contactList.get(i).getId()), appointment.getId());

            }

        dalAppointment.close();
    }

}
