package com.example.antifurtoappbasic;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

/*Classe che gestice l'accelerometro e monitora il movimento*/

public class accelerometerService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float lastAcc = 0.0f;
    private float acceleration = 0.0f;
    private float totAcc = 0.0f;
    private BroadcastReceiver mReceiver = null;

    private int max = 0;

    public accelerometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.i("PROVA ONCREATE", "ServICE CREATED");

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastAcc = SensorManager.GRAVITY_EARTH;
        acceleration = SensorManager.GRAVITY_EARTH;
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("PROVA ONSTARTCOMMAND", "Service Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        //unregisterReceiver(mReceiver);
        Log.e("PROVA ONDESTROY", "Service Destroyed");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        lastAcc = acceleration;
        acceleration = x * x + y * y + z * z;
        float diff = acceleration - lastAcc;
        totAcc = diff * acceleration;

        int threshold = 0;

        try {
            threshold = MainActivity.sharedPreferences.getString("SENSIBILITY", "high").equals("low") ? 50000 : 30000;
        } catch (NullPointerException e) {

            threshold = 30000;
        }

        if ((totAcc > threshold) && (max != 1)) {
            max = 1;
            Log.i("PROVA ACCELEROMETRO", "NUOVA ATTIVITA' LANCIATA");

            unregisterReceiver(mReceiver);
            Intent dialogIntent = new Intent(this, UnlockActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);

            stopSelf();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        this.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        this.onPause();
        mSensorManager.unregisterListener(this);
    }
}
