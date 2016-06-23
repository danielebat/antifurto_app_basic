package com.example.antifurtoappbasic;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/*Classe che gestisce la funzionalità di bloccaggio del telefono e settaggio del codice di sessione
e per il lancio del servizio di monitoraggio dell'accelerometro*/

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    public void showDialog(final View v) {

        EditText editCode1 = (EditText) findViewById(R.id.pass1);
        final String code1 = editCode1.getText().toString().trim();
        Log.i("PROVA STRINGA 1: ", code1);

        if (code1.equals("")) {
            Toast.makeText(getApplicationContext(), "No Code Selected", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);

        builder.setMessage("Confirm your Code");
        final EditText editCode2 = new EditText(getApplicationContext());
        editCode2.setId(R.id.editPass2);

        int width = 160; // margin in dips
        int height = 40;
        float d = this.getResources().getDisplayMetrics().density;

        editCode2.setWidth((int) (width * d));
        editCode2.setHeight((int) (height * d));
        editCode2.setBackground(getResources().getDrawable(R.drawable.back));
        editCode2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editCode2.setTextColor(Color.BLACK);
        editCode2.setGravity(Gravity.CENTER);
        editCode2.setTransformationMethod(new PasswordTransformationMethod());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 0, 30, 0);
        params.gravity = Gravity.CENTER;

        layout.addView(editCode2, params);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(4);
        editCode2.setFilters(filters);

        builder.setView(layout);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String code2 = editCode2.getText().toString().trim();
                Log.i("PROVA STRINGA 2: ", code2);

                if (code2.equals("")) {
                    Toast.makeText(getApplicationContext(), "No Code Typed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!code1.equals(code2)) {
                    Toast.makeText(getApplicationContext(), "Code different from previous", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    MainActivity.editor = MainActivity.sharedPreferences.edit();
                    MainActivity.editor.putString("SESSION CODE", code1);
                    MainActivity.editor.commit();

                    startService(v);
                    //boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                    // if (isAdmin) mDevicePolicyManager.lockNow();
                    MainActivity.main.finish();
                    finish();
                }
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

    public void setPasswordVisibility(View v) {

        CheckBox checkBox;
        EditText edit;

        checkBox = (CheckBox) findViewById(R.id.passVisible);
        edit = (EditText) findViewById(R.id.pass1);

        boolean checked = checkBox.isChecked();

        if (checked) edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void startService(View v) {
        startService(new Intent(this, accelerometerService.class));
    }
}
