package com.example.antifurtoappbasic;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

import static android.graphics.Color.RED;

/*Classe che gestisce l'attività principale dell'applicazione*/

public class MainActivity extends AppCompatActivity {

    static public SharedPreferences sharedPreferences;
    static public SharedPreferences.Editor editor;
    static public MainActivity main = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("PERSONAL_SETTINGS", Context.MODE_PRIVATE);

        main = this;

        String firstPath = Environment.getExternalStorageDirectory() + "/AntifurtoApp";

        File externalStorage = new File(firstPath + "/externalStorage");
        boolean success = true;
        if (!externalStorage.exists()) success = externalStorage.mkdirs();
    }

    public void startLaunchActivity(View v) {

        if (!sharedPreferences.getBoolean("permanentCodeSet", false) ||
                !sharedPreferences.getBoolean("phoneNumberSet", false) ||
                !sharedPreferences.getBoolean("emailSet", false)) {
            Toast.makeText(getApplicationContext(), "Security Parameters Not Set!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent launchActivity = new Intent(this, LaunchActivity.class);
        launchActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchActivity);
    }

    public void startSettingsActivity(View v) {

        Intent settingsActivity = new Intent(this, SettingsChooser.class);
        settingsActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(settingsActivity);
    }

    public void startInfoActivity(View v) {

        Intent infoActivity = new Intent(this, InfoActivity.class);
        infoActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(infoActivity);
    }

    public void startMapTrackingActivity(View v) {

        Intent mapTrackingActivity = new Intent(this, MapTrackingActivity.class);
        mapTrackingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mapTrackingActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
