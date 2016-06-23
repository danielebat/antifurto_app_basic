package com.example.antifurtoappbasic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.regex.Pattern;

/*Classe per il settaggio di mail sicure per l'utente*/

public class SettingsActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
    }

    /*Metodo per il controllo e l'inserimento di mail sicure*/

    public void checkEmailAdresses(View v) {
        boolean nextPage = true;
        Pattern numberPattern = Patterns.EMAIL_ADDRESS;

        EditText mail = (EditText) findViewById(R.id.mail1);
        String address1 = mail.getText().toString().trim();

        if (address1.equals("") || !numberPattern.matcher(address1).matches())
            nextPage = nextPage & false;

        mail = (EditText) findViewById(R.id.mail2);
        String address2 = mail.getText().toString().trim();

        if (address2.equals("") || !numberPattern.matcher(address2).matches())
            nextPage = nextPage & false;

        mail = (EditText) findViewById(R.id.mail3);
        String address3 = mail.getText().toString().trim();

        if (address3.equals("") || !numberPattern.matcher(address3).matches())
            nextPage = nextPage & false;

        MainActivity.editor = MainActivity.sharedPreferences.edit();

        if (!nextPage) {

            MainActivity.editor.putBoolean("emailSet", false);
            MainActivity.editor.commit();
            Toast.makeText(getApplicationContext(), "Some addresses are wrong", Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity.editor.putString("EMAIL ADDRESS 1", address1);
        MainActivity.editor.putString("EMAIL ADDRESS 2", address2);
        MainActivity.editor.putString("EMAIL ADDRESS 3", address3);

        MainActivity.editor.putBoolean("emailSet", true);
        MainActivity.editor.commit();

        Toast.makeText(getApplicationContext(), "Email Adresses Set", Toast.LENGTH_SHORT).show();

        finish();

    }

    /*Metodo per il recupero di mail sicure dalla rubrica*/

    public void setSecureEmailAddresses(View v) {

        Intent intentAddress = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intentAddress, 2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2) {

            if (resultCode == Activity.RESULT_OK) {

                String emailAddress = "";
                Uri contactData = data.getData();
                Cursor c = managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {

                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);

                    while (phones.moveToNext()) {

                        emailAddress = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    phones.close();
                }

                if (!emailAddress.equals("")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity2.this);

                    final CharSequence[] choiceList = {"Email Address 1", "Email Address 2", "Email Address 3"};

                    final String finalEmailAddress = emailAddress;
                    builder.setItems(choiceList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            EditText mail;

                            switch (which) {

                                case 0:
                                    mail = (EditText) findViewById(R.id.mail1);
                                    mail.setText(finalEmailAddress);
                                    return;

                                case 1:
                                    mail = (EditText) findViewById(R.id.mail2);
                                    mail.setText(finalEmailAddress);
                                    return;

                                case 2:
                                    mail = (EditText) findViewById(R.id.mail3);
                                    mail.setText(finalEmailAddress);
                                    return;

                                default:
                                    return;
                            }
                        }
                    });

                    builder.create();
                    builder.show();

                } else {

                    Toast.makeText(this, "Email Address not present", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
