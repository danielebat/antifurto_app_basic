package com.example.antifurtoappbasic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*Classe per la gestione dello schermo, verificando se è spento o acceso*/

public class ScreenReceiver extends BroadcastReceiver {

    private boolean screenOff = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            Log.e("PROVA SCHERMO","SPENTO");
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

            Log.e("PROVA SCHERMO","ACCESO");
            if (screenOff == true) {

                Intent dialogIntent = new Intent(context, UnlockActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialogIntent);

                context.stopService(new Intent(context, accelerometerService.class));
                context.unregisterReceiver(this);
            }
            screenOff = false;
        }
    }
}
