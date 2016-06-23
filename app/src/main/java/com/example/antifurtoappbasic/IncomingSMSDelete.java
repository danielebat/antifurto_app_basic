package com.example.antifurtoappbasic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/*Classe per la cancellazione dello storage esterno tramite ricezione di un sms sicuro*/

public class IncomingSMSDelete extends BroadcastReceiver {

    public IncomingSMSDelete() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            if (extras != null) {

                Object[] smsExtra = (Object[]) extras.get("pdus");
                for (int i = 0; i < smsExtra.length; ++i) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                    String testo = sms.getMessageBody().toString();
                    String numero = sms.getOriginatingAddress();
                    Log.e("NUMERO INCOMING", numero);

                    File deleteStorage = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/AntifurtoApp/externalStorage");
                    boolean result = false;

                    final String number1 = "+39" + MainActivity.sharedPreferences.getString("PHONE NUMBER 1", "");
                    final String number2 = "+39" + MainActivity.sharedPreferences.getString("PHONE NUMBER 2", "");
                    final String number3 = "+39" + MainActivity.sharedPreferences.getString("PHONE NUMBER 3", "");

                    if (numero.equals(number1) || numero.equals(number2) || numero.equals(number3)) {

                        if (testo.equalsIgnoreCase("cancella"))
                            result = deleteExternalStorage(deleteStorage);
                    }

                    if (result)
                        Toast.makeText(context, "External Storage deleted.", Toast.LENGTH_LONG).show();
                    
                    context.unregisterReceiver(this);
                }
            }
        } catch (IllegalArgumentException e) {
            Log.e("PROVA CATCH MESSAGE RER", "ENTRATO");
        } catch (NullPointerException e) {
        }


    }

    private boolean deleteExternalStorage(File path) {

        if (path.exists()) {

            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {

                if (files[i].isDirectory()) {

                    deleteExternalStorage(files[i]);
                } else {

                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }
}