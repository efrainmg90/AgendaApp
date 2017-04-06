package com.example.efrainmg90.agendaapp.Helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.efrainmg90.agendaapp.models.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Efrainmg90 on 01/04/2017.
 */

public class ContactsLoader {


    List<Contact> contactList;
    Context context;

    public ContactsLoader(Context context) {
        this.context = context;

    }

    public List<Contact> getContacts() {
        contactList = new ArrayList<Contact>();
        Contact contact;

        ContentResolver cr =context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                contact = new Contact();
                String id = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contact.setId(id);
                contact.setName(name);
                // Query phone here. Covered next
                if (Integer
                        .parseInt(cur.getString(cur
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                Cursor pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?", new String[]{id}, null);
                while (pCur.moveToNext()) {
                    // Do something with phones
                    String phoneNo = pCur
                            .getString(pCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contact.setPhone(phoneNo);


                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        contact.setEmail(email);

                    }
                    emailCur.close();
                }
                pCur.close();
            }

                contactList.add(contact);
                Log.d("Contacto:",contact.toString());
                Log.e("--------------------","-------------------");
            }
            cur.close();
        }

        return contactList; // here you can return whatever you want.
    }

}
