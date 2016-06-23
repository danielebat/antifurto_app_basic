package com.fragment.antifurtoappbasic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.antifurtoappbasic.MainActivity;
import com.example.antifurtoappbasic.R;

/*Classe per il settaggio dei parametri di sicurezza*/

public class AppSettings extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_app_settings, container, false);

        final Spinner spinner1 = (Spinner) rootView.findViewById(R.id.countdown);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.countdown, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        final Spinner spinner2 = (Spinner) rootView.findViewById(R.id.alarms);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.alarms, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        final Spinner spinner3 = (Spinner) rootView.findViewById(R.id.sensibility);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.sensibility, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String text = spinner1.getSelectedItem().toString().trim();

                MainActivity.editor = MainActivity.sharedPreferences.edit();
                if (text.equals("30")) text = "30";
                if (text.equals("45")) text = "45";
                if (text.equals("60")) text = "60";
                MainActivity.editor.putString("COUNTDOWN", text);
                MainActivity.editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                MainActivity.editor = MainActivity.sharedPreferences.edit();
                MainActivity.editor.putString("COUNTDOWN", "30");
                MainActivity.editor.commit();
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String text = spinner2.getSelectedItem().toString().trim();

                MainActivity.editor = MainActivity.sharedPreferences.edit();
                if (text.equals("Alarm1")) text = "allarme1.wma";
                if (text.equals("Alarm2")) text = "allarme2.mp3";
                if (text.equals("Alarm3")) text = "allarme3.mp3";
                MainActivity.editor.putString("ALARM", text);
                MainActivity.editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                MainActivity.editor = MainActivity.sharedPreferences.edit();
                MainActivity.editor.putString("ALARM", "/allarme1.wma");
                MainActivity.editor.commit();
            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String text = spinner3.getSelectedItem().toString().trim();

                MainActivity.editor = MainActivity.sharedPreferences.edit();
                if (text.equals("High Sensibility")) text = "high";
                if (text.equals("Low Sensibility")) text = "low";
                MainActivity.editor.putString("SENSIBILITY", text);
                MainActivity.editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                MainActivity.editor = MainActivity.sharedPreferences.edit();
                MainActivity.editor.putString("SENSIBILITY", "high");
                MainActivity.editor.commit();
            }
        });

        return rootView;
    }
}
