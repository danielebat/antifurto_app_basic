package com.example.antifurtoappbasic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*Classe per l'inserimento e la configurazione del Codice permanente*/

public class SettingsActivityCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_activity_code);
    }

    /*Metodo per la configurazione del codice permamenente*/

    public void savePermanentCode(View v) {

        EditText editPermanentCode = (EditText) findViewById(R.id.permanentCodeDefine);
        final String permCode1 = editPermanentCode.getText().toString().trim();

        if (permCode1.equals("")) {
            Toast.makeText(getApplicationContext(), "No Code Typed", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivityCode.this);

        builder.setMessage("Confirm your Permament code:");

        final EditText editPermanentCode2 = new EditText(getApplicationContext());

        int width = 160;// margin in dips
        int height = 40;
        float d = this.getResources().getDisplayMetrics().density;

        editPermanentCode2.setWidth((int) (width * d));
        editPermanentCode2.setHeight((int) (height * d));
        editPermanentCode2.setBackground(getResources().getDrawable(R.drawable.back));
        editPermanentCode2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editPermanentCode2.setTextColor(Color.BLACK);
        editPermanentCode2.setGravity(Gravity.CENTER);
        editPermanentCode2.setTransformationMethod(new PasswordTransformationMethod());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 0, 30, 0);
        params.gravity = Gravity.CENTER;

        layout.addView(editPermanentCode2, params);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(4);
        editPermanentCode2.setFilters(filters);

        builder.setView(layout);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String code = editPermanentCode2.getText().toString().trim();
                Log.i("PROVA STRINGA 2: ", code);

                if (code.equals("")) {
                    Toast.makeText(getApplicationContext(), "No Code Typed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!code.equals(permCode1)) {
                    Toast.makeText(getApplicationContext(), "Code different from previous", Toast.LENGTH_SHORT).show();
                    return;
                }

                MainActivity.sharedPreferences.edit();
                MainActivity.editor.putBoolean("permanentCodeSet", true);
                MainActivity.editor.putString("PERMANENT CODE", permCode1);
                MainActivity.editor.commit();

                Toast.makeText(getApplicationContext(), "Permanent Code Set", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        builder.create();
        AlertDialog alert = builder.show();

        TextView message = (TextView) alert.findViewById(android.R.id.message);
        message.setGravity(Gravity.CENTER);
    }

    /*Metodo per la visualizzazione della password*/

    public void setPasswordVisibility(View v) {

        CheckBox checkBox;
        EditText edit;

        checkBox = (CheckBox) findViewById(R.id.passVisibleCode);
        edit = (EditText) findViewById(R.id.permanentCodeDefine);


        boolean checked = checkBox.isChecked();

        if (checked) edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
