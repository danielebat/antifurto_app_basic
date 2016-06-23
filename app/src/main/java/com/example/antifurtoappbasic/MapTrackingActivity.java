package com.example.antifurtoappbasic;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/*Classe per la gestione dell'attività di monitoraggio della posizione*/

public class MapTrackingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking);
    }

    public void startTrackingActivity(View v) {

        EditText phoneNumber = (EditText) findViewById(R.id.numberToTrack);
        String number = phoneNumber.getText().toString().trim();

        if (number.equals("")) {

            Toast.makeText(this, "Phone Number unpespecified", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkSMSAvailability(number)) {

            Toast.makeText(this, "No Messages Found", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent mapTrackingActivity2 = new Intent(this, MapTrackingActivity2.class);
        mapTrackingActivity2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mapTrackingActivity2.putExtra("phoneNumber", number);
        startActivity(mapTrackingActivity2);
    }

    /*Metodo per il recupero di informazioni dalla rubrica dello smartphone*/

    public void retrieveContactFromAddressBook(View v) {

        Intent intentAddress = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intentAddress, 2);
    }

    /*Metodo per il controllo della presenza di messaggi di segnalazione*/

    public boolean checkSMSAvailability(String number) {

        int counter = 0;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

        Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

        String targetNumber = number;
        while (cursor.moveToNext()) {

            String address = cursor.getString(cursor.getColumnIndex("address"));
            if (address.equals(targetNumber)) {

                String body = cursor.getString(cursor.getColumnIndex("body"));
                if (body.length() >= 27) {

                    String recognizer = body.substring(0, 27);
                    if (recognizer.equals("ANTIFURTO APP - RILEVAMENTO")) {

                        String smsDate = cursor.getString(cursor.getColumnIndex("date"));
                        Long timestamp = Long.parseLong(smsDate);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timestamp);
                        Date finaldate = calendar.getTime();
                        if (finaldate.after(yesterday)) {

                            counter++;
                        }
                    }
                }
            }
        }

        if (counter == 0) return false;
        else return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2) {

            if (resultCode == Activity.RESULT_OK) {

                String phoneNumber = "";
                Uri contactData = data.getData();
                Cursor c = managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {

                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null, null);

                    while (phones.moveToNext()) {

                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }

                    phones.close();
                }

                EditText trackNumber = (EditText) findViewById(R.id.numberToTrack);
                if (!phoneNumber.equals("")) {

                    if (!phoneNumber.substring(0, 3).equals("+39"))
                        phoneNumber = "+39" + phoneNumber;
                    phoneNumber = phoneNumber.replaceAll("\\s", "");
                    trackNumber.setText(phoneNumber);

                } else {

                    trackNumber.setText("");
                    Toast.makeText(this, "Phone Number not present", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
