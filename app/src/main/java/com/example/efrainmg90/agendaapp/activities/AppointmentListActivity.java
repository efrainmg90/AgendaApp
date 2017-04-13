package com.example.efrainmg90.agendaapp.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.efrainmg90.agendaapp.DAL.AppointmentDAL;
import com.example.efrainmg90.agendaapp.Helpers.ContactsLoader;
import com.example.efrainmg90.agendaapp.Helpers.ScheduleDBHelper;
import com.example.efrainmg90.agendaapp.R;
import com.example.efrainmg90.agendaapp.models.Appointment;
import com.example.efrainmg90.agendaapp.models.Contact;
import com.example.efrainmg90.agendaapp.ui.AppointmentListViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppointmentListActivity extends AppCompatActivity {
    TextView textEmptyList;
    ListView listView;
    FloatingActionButton actionButtonAdd;
    AppointmentListViewAdapter appointmentListViewAdapter;
    ScheduleDBHelper scheduleDBHelper;
    AppointmentDAL dalAppointment;
    List<Appointment> appointmentList;
    List<Long> contactsIntoAppointment;
    ContactsLoader contactsLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        actionButtonAdd = (FloatingActionButton) findViewById(R.id.action_bar_add);
        textEmptyList = (TextView) findViewById(R.id.title_empty_list);

        contactsLoader = new ContactsLoader(this);
        dalAppointment = new AppointmentDAL(this);
        dalAppointment.open();
        appointmentList = dalAppointment.getAllAppointments();
        dalAppointment.close();

        bootstrapList();


    }//fin oncreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.op_about) {
            LayoutInflater inflater = AppointmentListActivity.this.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_about, null);

            TextView phone = (TextView) view.findViewById(R.id.about_phone);
            Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
            TextView code = (TextView) view.findViewById(R.id.about_code);
            Linkify.addLinks(code, Linkify.WEB_URLS);
            TextView email = (TextView) view.findViewById(R.id.about_email);
            Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);

            AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentListActivity.this);
            builder.setTitle("Acerca del desarrollador: ");
            builder.setView(view);
            builder.setNegativeButton("Aceptar", null);
            Dialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void reloadAppointmentList() {
        dalAppointment.open();
        if (appointmentList != null) {
            appointmentList = dalAppointment.getAllAppointments();
            appointmentListViewAdapter = new AppointmentListViewAdapter(this, appointmentList);
            appointmentListViewAdapter.notifyDataSetChanged();
            listView.setAdapter(appointmentListViewAdapter);
        }
        dalAppointment.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        reloadAppointmentList();
    }


    private void bootstrapList() {
        appointmentListViewAdapter = new AppointmentListViewAdapter(this, appointmentList);
        listView = (ListView) findViewById(R.id.lvAppointmentsList);
        listView.setEmptyView(textEmptyList);
        listView.setAdapter(appointmentListViewAdapter);
        actionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentListActivity.this, SaveAppointmentActivity.class);
                startActivity(intent);

            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                int btn_initPosY=actionButtonAdd.getScrollY();
                if (i == SCROLL_STATE_TOUCH_SCROLL) {
                    actionButtonAdd.animate().cancel();
                    actionButtonAdd.animate().translationYBy(150);
                    actionButtonAdd.animate();
                    actionButtonAdd.setVisibility(View.INVISIBLE);
                } else {
                    actionButtonAdd.setVisibility(View.VISIBLE);
                    actionButtonAdd.animate().cancel();
                    actionButtonAdd.animate().translationY(btn_initPosY);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                final Dialog dialog;
                final Appointment appointmentSelected = appointmentList.get(i);
                // Dialog dialog = new Dialog(AppointmentListActivity.this);
                View options = LayoutInflater.from(AppointmentListActivity.this).inflate(R.layout.layout_options_listview, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentListActivity.this);
                builder.setTitle("¿Que operación desea realizar?: ");
                builder.setView(options);
                builder.setNegativeButton("Cancelar", null);
                dialog = builder.create();
                dialog.show();
                Vibrator vibrate = (Vibrator) AppointmentListActivity.this.getSystemService(VIBRATOR_SERVICE);
                vibrate.vibrate(200);

                Button btnOpEdit = (Button) options.findViewById(R.id.button_edit);
                Button btnOpDelete = (Button) options.findViewById(R.id.button_delete);
                Button btnOpCall = (Button) options.findViewById(R.id.button_call);
                Button btnOpSendMSN = (Button) options.findViewById(R.id.button_send_msn);

                btnOpEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewChild) {
                        List<String> contactNames = new ArrayList<String>();
                        dalAppointment.open();
                        contactsIntoAppointment = dalAppointment.getContactsItemsSelected(appointmentSelected.getId());
                        dalAppointment.close();
                        for (long contactId : contactsIntoAppointment) {
                            Contact contact = contactsLoader.findContactById(contactId);
                            contactNames.add(contact.getName());
                        }
                        Intent intent = new Intent(AppointmentListActivity.this, SaveAppointmentActivity.class);
                        intent.putExtra("appointment", (Serializable) appointmentSelected);
                        intent.putExtra("contacts", (Serializable) contactNames);
                        intent.putExtra("flag", true);
                        startActivity(intent);
                        finish();
                    }
                });//end Onclick Edit

                btnOpDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewChild) {
                        dalAppointment.open();
                        int rows = dalAppointment.deleteAppointment(appointmentSelected);
                        int row2 = dalAppointment.deleteContactsEvent(appointmentSelected.getId());
                        dalAppointment.close();
                        if (rows > 0) {
                            Snackbar.make(view, "Evento eliminado", Snackbar.LENGTH_LONG).show();
                            reloadAppointmentList();
                        } else
                            Snackbar.make(view, "No se pudo eliminar evento", Snackbar.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });// end Onclick Delete

                btnOpCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewChild) {
                        List<String> contactNames = new ArrayList<String>();
                        final List<Contact> contacts = new ArrayList<Contact>();
                        dalAppointment.open();
                        contactsIntoAppointment = dalAppointment.getContactsItemsSelected(appointmentSelected.getId());
                        dalAppointment.close();
                        for (long contactId : contactsIntoAppointment) {
                            Contact contact = contactsLoader.findContactById(contactId);
                            contactNames.add(contact.getName());
                            contacts.add(contact);
                        }
                        dialog.dismiss();

                        final CharSequence[] options = contactNames.toArray(new CharSequence[contactNames.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentListActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Selecciona el contacto a llamar:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // the user clicked on options[which]
                                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                                callIntent.setData(Uri.parse("tel:" + contacts.get(which).getPhone()));
                                try {
                                    startActivity(callIntent);
                                } catch (Exception e) {
                                }

                            }
                        });
                        builder.setNegativeButton("Cancelar", null);
                        builder.show();
                    }
                });// end Onclick Call

                btnOpSendMSN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<String> contactNames = new ArrayList<String>();
                        final List<Contact> contacts = new ArrayList<Contact>();
                        dalAppointment.open();
                        contactsIntoAppointment = dalAppointment.getContactsItemsSelected(appointmentSelected.getId());
                        dalAppointment.close();
                        for (long contactId : contactsIntoAppointment) {
                            Contact contact = contactsLoader.findContactById(contactId);
                            contactNames.add(contact.getName());
                            contacts.add(contact);
                        }
                        dialog.dismiss();

                        final CharSequence[] options = contactNames.toArray(new CharSequence[contactNames.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentListActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Selecciona el contacto a enviar SMS:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // the user clicked on options[which]
                                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                                callIntent.setData(Uri.parse("sms:" + contacts.get(which).getPhone()));
                                try {
                                    startActivity(callIntent);
                                } catch (Exception e) {
                                }

                            }
                        });
                        builder.setNegativeButton("Cancelar", null);
                        builder.show();
                    }
                }); // end Onclick SendMSN

                return false;
            }
        });
    }// end bootstrapList

}
