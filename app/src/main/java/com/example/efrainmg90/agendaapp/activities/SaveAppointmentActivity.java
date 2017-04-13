package com.example.efrainmg90.agendaapp.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
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


    Dialog dialog;
    ListView listViewContacts;
    Button buttonSave;
    TextView titleContactsAdd;
    EditText title,description,date,edtHour;
    List<Contact> contactList;
    List<String> contactNameList;
    ImageView imageViewContact;
    SparseBooleanArray checkedContacs;

    AppointmentDAL dalAppointment;

    Appointment appointmentToUpdate;
    List<String> contactsToUpdate;
    boolean methodFlag,contactsChangedFlag=false;


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
        edtHour = (EditText) findViewById(R.id.edt_hour);
        buttonSave = (Button) findViewById(R.id.btn_save_appointment_with_contacs);
        imageViewContact = (ImageView) findViewById(R.id.imvIcont_add_contact);
        titleContactsAdd = (TextView) findViewById(R.id.title_img_contact);
        contactNameList = new ArrayList<>();

        methodFlag = getIntent().getBooleanExtra("flag",false);
        if(methodFlag){
            TextView mainTitle = (TextView) findViewById(R.id.title_main);
            mainTitle.setText("Actualizar Evento");
            loadDataToUpdate();
        }


        new LoadingContactsTask().execute();


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

        edtHour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SaveAppointmentActivity.this);
                    final TimePicker picker = new TimePicker(SaveAppointmentActivity.this);
                    builder.setTitle("Hora: ");
                    builder.setView(picker);
                    builder.setNegativeButton("Cancelar", null);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int hour = picker.getCurrentHour();
                            int min = picker.getCurrentMinute();
                            String format = "";
                            if(picker.is24HourView()){
                                edtHour.setText(new StringBuilder().append(hour).append(" : ").append(min));
                            }else {
                                if (hour == 0) {
                                    hour += 12;
                                    format = "AM";
                                } else if (hour == 12) {
                                    format = "PM";
                                } else if (hour > 12) {
                                    hour -= 12;
                                    format = "PM";
                                } else {
                                    format = "AM";
                                }
                                edtHour.setText(new StringBuilder().append(hour).append(" : ").append((min<10)?"0"+min:min)
                                        .append(" ").append(format));
                            }

                        }
                    });
                    builder.show();
                }
            }
        });// end on Focuslistener

        imageViewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null)
                    dialog.show();
                else
                    Toast.makeText(SaveAppointmentActivity.this, "Espere cargando contactos...", Toast.LENGTH_SHORT).show();
            }

        });//end on ClickListener

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText().toString().equals("")|| date.getText().toString().equals("")||description.getText().equals("")||edtHour.getText().equals("")){
                    Snackbar.make(view,"Por favor ingrese todos los campos",Snackbar.LENGTH_LONG).show();
                }else if(checkedContacs==null && !methodFlag){
                    Snackbar.make(view,"Por favor ingrese algun contacto",Snackbar.LENGTH_LONG).show();
                }
                else{
                    saveAppointmenWithContacts(view);
                    Intent intent = new Intent(SaveAppointmentActivity.this,AppointmentListActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


    }// end

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.op_about){
            LayoutInflater inflater =SaveAppointmentActivity.this.getLayoutInflater();
            View view =inflater.inflate(R.layout.dialog_about, null);

            TextView phone = (TextView) view.findViewById(R.id.about_phone);
            Linkify.addLinks(phone,Linkify.PHONE_NUMBERS);
            TextView code = (TextView) view.findViewById(R.id.about_code);
            Linkify.addLinks(code,Linkify.WEB_URLS);
            TextView email = (TextView) view.findViewById(R.id.about_email);
            Linkify.addLinks(email,Linkify.EMAIL_ADDRESSES);

            AlertDialog.Builder builder  = new AlertDialog.Builder(SaveAppointmentActivity.this);
            builder.setTitle("Acerca del desarrollador: ");
            builder.setView(view);
            builder.setNegativeButton("Aceptar", null);
            Dialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

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
        ArrayAdapter arrayAdapterContacts  = new ArrayAdapter(this, android.R.layout.select_dialog_multichoice,contactNameList);
        arrayAdapterContacts.notifyDataSetChanged();
        listViewContacts = new ListView(SaveAppointmentActivity.this);
        listViewContacts.setAdapter(arrayAdapterContacts);
        listViewContacts.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        if(methodFlag){
           // listViewContacts.setItemChecked(2,true);
            //listViewContacts.setSelection(2);
        }

        AlertDialog.Builder builder  = new AlertDialog.Builder(SaveAppointmentActivity.this);
        builder.setTitle("Contactos: ");
        builder.setView(listViewContacts);
        builder.setNegativeButton("Cancelar", null);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkedContacs = listViewContacts.getCheckedItemPositions();
                showContactsSelected();
                contactsChangedFlag =true;
            }
        });
        dialog= builder.create();
    }

    public void showContactsSelected(){
        String strContacts = "Contactos Agregados: ";
        for (int i = 0; i < contactNameList.size(); i++)
            if (checkedContacs.get(i)) {
                String item = contactNameList.get(i);
                strContacts = strContacts +"*"+ item+"  ";
  /* do whatever you want with the checked item */
            }
            titleContactsAdd.setText(strContacts);
    }

    public void saveAppointmenWithContacts(View view){
        dalAppointment = new AppointmentDAL(this);
        dalAppointment.open();
        Appointment appointment = new Appointment();
        appointment.setTitle(title.getText().toString());
        appointment.setDescription(description.getText().toString());
        appointment.setDate(date.getText().toString());
        appointment.setHour(edtHour.getText().toString());
        if(methodFlag){
            appointment.setId(appointmentToUpdate.getId());
            dalAppointment.updateAppointment(appointment);
            Snackbar.make(view,"Evento Actualizado",Snackbar.LENGTH_LONG).show();
            if(contactsChangedFlag)
                dalAppointment.deleteContactsEvent(appointmentToUpdate.getId());
        }else {
            appointment = dalAppointment.addAppointment(appointment);
            Snackbar.make(view,"Evento Guardado",Snackbar.LENGTH_LONG).show();
        }

        if(checkedContacs!=null){
            for (int i = 0; i < contactNameList.size(); i++)
                if (checkedContacs.get(i)) {
                    dalAppointment.addContactToEvent((contactList.get(i).getId()), appointment.getId());
                }
        }else
            Snackbar.make(view,"Evento sin contactos agregados",Snackbar.LENGTH_LONG).show();

        dalAppointment.close();
    }

    public void loadDataToUpdate(){
        appointmentToUpdate = (Appointment) getIntent().getSerializableExtra("appointment");
        contactsToUpdate = (List<String>) getIntent().getSerializableExtra("contacts");
        title.setText(appointmentToUpdate.getTitle());
        description.setText(appointmentToUpdate.getDescription());
        date.setText(appointmentToUpdate.getDate());
        edtHour.setText(appointmentToUpdate.getHour());
        String strContacts = "Contactos Agregados: ";
        for (int i = 0; i < contactsToUpdate.size(); i++)
             {
                String item = contactsToUpdate.get(i);
                strContacts = strContacts +"*"+ item+"  ";
            }
        titleContactsAdd.setText(strContacts);
    }

}
