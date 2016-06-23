package com.example.antifurtoappbasic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

/*Classe per l'inserimento di numeri sicuri per l'utente*/

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    /*Metodo che gestisce il settaggio di numeri sicuri*/

    public void checkPhoneNumbers(View v) {

        boolean nextPage = true;
        Pattern numberPattern = Patterns.PHONE;

        EditText number = (EditText) findViewById(R.id.phone1);
        String phoneNumber1 = number.getText().toString().trim();

        if (phoneNumber1.equals("") || !numberPattern.matcher(phoneNumber1).matches())
            nextPage = nextPage & false;

        number = (EditText) findViewById(R.id.phone2);
        String phoneNumber2 = number.getText().toString().trim();

        if (phoneNumber2.equals("") || !numberPattern.matcher(phoneNumber2).matches())
            nextPage = nextPage & false;

        number = (EditText) findViewById(R.id.phone3);
        String phoneNumber3 = number.getText().toString().trim();

        if (phoneNumber3.equals("") || !numberPattern.matcher(phoneNumber3).matches())
            nextPage = nextPage & false;

        MainActivity.editor = MainActivity.sharedPreferences.edit();

        if (!nextPage) {

            MainActivity.editor.putBoolean("phoneNumberSet", false);
            MainActivity.editor.commit();
            Toast.makeText(getApplicationContext(), "Some numbers are wrong", Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity.editor.putString("PHONE NUMBER 1", phoneNumber1);
        MainActivity.editor.putString("PHONE NUMBER 2", phoneNumber2);
        MainActivity.editor.putString("PHONE NUMBER 3", phoneNumber3);

        MainActivity.editor.putBoolean("phoneNumberSet", true);
        MainActivity.editor.commit();

        Toast.makeText(getApplicationContext(), "Phone Numbers Set", Toast.LENGTH_SHORT).show();
        finish();
    }

    /*Metodo per il recupero di numeri sicuri dalla rubrica*/

    public void setSecurePhoneNumbers(View v) {

        Intent intentAddress = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intentAddress, 2);
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

                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                    while (phones.moveToNext()) {

                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phones.close();

                }

                if (!phoneNumber.equals("")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                    final CharSequence[] choiceList = {"Number 1", "Number 2", "Number 3"};

                    if (phoneNumber.substring(0, 3).equals("+39"))
                        phoneNumber = phoneNumber.substring(3, phoneNumber.length());

                    phoneNumber = phoneNumber.replaceAll("\\s", "");
                    final String finalPhoneNumber = phoneNumber;
                    builder.setItems(choiceList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            EditText mail;

                            switch (which) {

                                case 0:
                                    mail = (EditText) findViewById(R.id.phone1);
                                    mail.setText(finalPhoneNumber);
                                    return;

                                case 1:
                                    mail = (EditText) findViewById(R.id.phone2);
                                    mail.setText(finalPhoneNumber);
                                    return;

                                case 2:
                                    mail = (EditText) findViewById(R.id.phone3);
                                    mail.setText(finalPhoneNumber);
                                    return;

                                default:
                                    return;
                            }
                        }
                    });

                    builder.create();
                    builder.show();

                } else {

                    Toast.makeText(this, "Phone Number not present", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
