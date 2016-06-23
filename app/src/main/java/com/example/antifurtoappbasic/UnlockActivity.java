package com.example.antifurtoappbasic;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Network;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.Layout;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/*Classe per la gestione dello sblocco. Controlla l'inerimento del codice di sessione, controlla il
codice permanente, avvia la registrazione di audio, lo scatto di foto in maniera nascosta, avvia la
risproduzione di un allarme, avvia un broadcastreceiver per la cancellazione dello storage, invia
mail e sms ai contatti sicuri
 */

public class UnlockActivity extends AppCompatActivity {

    public int lastAttempts = 3;
    private int secondsCounter;
    private boolean attemptsExpired = false;
    private boolean phoneUnlocked = false;
    public BroadcastReceiver incomingSMSDelete = null;
    private MediaPlayer mp = null;
    public AssetFileDescriptor assetFileDescriptor = null;
    private Timer scheduler = null;
    private NetworkAvailability networkAvailability = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        networkAvailability = new NetworkAvailability();
        if (!networkAvailability.isConnected(this) || !networkAvailability.isDataConnected(this) || !networkAvailability.isConnectedMobile(this))
            networkAvailability.enableDataMobileConnection(this);
        //if (!networkAvailability.isConnectedWifi(this) || !networkAvailability.isDataConnected(this)) networkAvailability.enableWifiConnection(this);

        //startLockTask();

        WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        CustomViewGroup view = new CustomViewGroup(this);

        manager.addView(view, localLayoutParams);

        String cdvalue = "";
        try {

            cdvalue = MainActivity.sharedPreferences.getString("COUNTDOWN", "30");
        } catch (NullPointerException e) {

            cdvalue = "30";
        }

        int countdown = (Integer.parseInt(cdvalue) + 1) * 1000;
        secondsCounter = Integer.parseInt(cdvalue) + 1;

        Log.e("PROVA NUMERO", cdvalue);

        CountDownTimer countDownTimer = new CountDownTimer(countdown, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                secondsCounter--;
                TextView countdown = (TextView) findViewById(R.id.countdown);
                countdown.setText(Integer.toString(secondsCounter));
            }

            @Override
            public void onFinish() {

                TextView countdown = (TextView) findViewById(R.id.countdown);
                countdown.setText("");
                countdown.setVisibility(View.INVISIBLE);

                TextView textCode = (TextView) findViewById(R.id.textViewCode);
                textCode.setVisibility(View.INVISIBLE);

                TextView newTextCode = (TextView) findViewById(R.id.newTextViewCode);
                newTextCode.setVisibility(View.VISIBLE);

                CheckBox check = (CheckBox) findViewById(R.id.codeVisible);
                check.setTextColor(getResources().getColor(R.color.white));

                Button button = (Button) findViewById(R.id.unlockButton);
                button.setBackground(getResources().getDrawable(R.drawable.back_button));
                button.setTextColor(Color.BLACK);

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setVisibility(View.VISIBLE);

                attemptsExpired = true;

                createAppFolder();

                incomingSMSDelete = new IncomingSMSDelete();
                IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
                registerReceiver(incomingSMSDelete, smsFilter);

                startAlarm();

                startEmailPhoneService();
            }
        }.start();
    }

    /*Metodo per la gestione dello sblocco del telefono*/

    public void unlockPhone(View v) {

        EditText unlockCodeEditText = (EditText) findViewById(R.id.unlockCode);
        String unlockCode = unlockCodeEditText.getText().toString().trim();

        String code1 = MainActivity.sharedPreferences.getString("SESSION CODE", "");

        if (!attemptsExpired) {

            if (unlockCode.equals(code1)) {

                Log.i("PROVA SBLOCCO", "SBLOCCATO");
                this.phoneUnlocked = true;
                finish();
            }

            if (!unlockCode.equals(code1)) {
                Toast.makeText(getApplicationContext(), "Incorrect Code", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {

            String permanentCode = MainActivity.sharedPreferences.getString("PERMANENT CODE", "");
            if (!unlockCode.equals(permanentCode) && (lastAttempts != 0)) {
                lastAttempts--;
                Toast.makeText(getApplicationContext(), "Incorrect Permanent Code", Toast.LENGTH_SHORT).show();
                ;
                return;
            }

            if (lastAttempts == 0) {
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.unlockLayout);
                relativeLayout.setVisibility(View.INVISIBLE);

                setContentView(R.layout.block_layout);
                return;
            }

            if (unlockCode.equals(permanentCode)) {
                this.phoneUnlocked = true;

                try {

                    unregisterReceiver(incomingSMSDelete);
                } catch (IllegalArgumentException e) {}

                if (this.scheduler != null) this.scheduler.cancel();
                this.mp.stop();
                this.mp.release();
                this.mp = null;
                finish();
            }
        }
    }

    /*Metodo per l'avvio della rispoduzione dell'allarme*/

    public void startAlarm() {

        final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        String filename = "";
        try {

            filename = MainActivity.sharedPreferences.getString("ALARM", "allarme1.wma");
        } catch (NullPointerException e) {

            filename = "allarme1.wma";
        }

        this.mp = new MediaPlayer();
        try {

            this.assetFileDescriptor = getAssets().openFd(filename);
            this.mp.setDataSource(this.assetFileDescriptor.getFileDescriptor(), this.assetFileDescriptor.getStartOffset(), this.assetFileDescriptor.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mp.prepareAsync();
        this.mp.start();


        this.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    /*Metodo per l'invio di mail e sms ogni 5 minuti*/

    public void startEmailPhoneService() {

        final String mail1 = MainActivity.sharedPreferences.getString("EMAIL ADDRESS 1", "");
        final String mail2 = MainActivity.sharedPreferences.getString("EMAIL ADDRESS 2", "");
        final String mail3 = MainActivity.sharedPreferences.getString("EMAIL ADDRESS 3", "");

        final MailService mailService = new MailService("antifurto.app", getApplicationContext());

        final String number1 = MainActivity.sharedPreferences.getString("PHONE NUMBER 1", "");
        final String number2 = MainActivity.sharedPreferences.getString("PHONE NUMBER 2", "");
        final String number3 = MainActivity.sharedPreferences.getString("PHONE NUMBER 3", "");

        final SmsManager smsManager = SmsManager.getDefault();

        TimerTask emailPhoneService = new TimerTask() {
            @Override
            public void run() {

                recordAudio();
                takeHiddenPhotos();
                ArrayList<String> localityInfo = geoLocateDevice();

                String subject = "SEGNALAZIONE";
                String messageBody = "SERVIZIO DI MONITORAGGIO DELLA POSIZIONE - ANTIFURTO APP\n\n\n" +
                        "Address: " + localityInfo.get(0) + "\n" +
                        "City: " + localityInfo.get(1) + "\n" +
                        "Country: " + localityInfo.get(2) + "\n\n" +
                        "Google Maps Link: " + "https://maps.google.com/maps?q=loc:" + localityInfo.get(3) + "," + localityInfo.get(4);

                if (networkAvailability.isConnectedWifi(getApplicationContext()) || networkAvailability.isDataConnected(getApplicationContext())) {

                    mailService.sendMail(mail1, subject, messageBody);
                    mailService.sendMail(mail2, subject, messageBody);
                    mailService.sendMail(mail3, subject, messageBody);
                }

                String SMSText = "ANTIFURTO APP - RILEVAMENTO\n" +
                        "Location: \n" +
                        localityInfo.get(0) + "\n" +
                        localityInfo.get(1) + "\n" +
                        localityInfo.get(2) + "\n\n" +
                        "Google Maps Link: " + "https://maps.google.com/maps?q=loc:" + localityInfo.get(3) + "," + localityInfo.get(4);

                if (SMSText.length() > 160) SMSText = SMSText.replace("Google Maps Link: ", "");

                try {

                    smsManager.sendTextMessage(number1, null, SMSText, null, null);
                    smsManager.sendTextMessage(number2, null, SMSText, null, null);
                    smsManager.sendTextMessage(number3, null, SMSText, null, null);
                } catch (NullPointerException e) {
                }
            }
        };

        scheduler = new Timer("EmailPhoneService", true);
        Date now = new Date();
        scheduler.scheduleAtFixedRate(emailPhoneService, now, 300000);
    }

    /*Metodo per la creazione di cartelle utili a contenere foto scattate e audio*/

    public void createAppFolder() {

        String firstPath = Environment.getExternalStorageDirectory() + "/AntifurtoApp";
        boolean success;

        File audioFolder = new File(firstPath + "/audio");
        success = true;
        if (!audioFolder.exists()) success = audioFolder.mkdirs();

        File photoFolder = new File(firstPath + "/photo");
        success = true;
        if (!photoFolder.exists()) success = photoFolder.mkdirs();
    }

    /*Metodo per l'avvio della registrazione dell'audio*/

    public void recordAudio() {

        if (this.mp.isPlaying()) this.mp.pause();

        AudioRecorder audioRecorder = new AudioRecorder("/AntifurtoApp/audio/registrazione.aac");
        audioRecorder.recordAudio();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.mp.start();
    }

    /*Metodo per lo scatto di foto in maniera nascosta*/

    public void takeHiddenPhotos() {

        HiddenCamera hiddenCameraBack = new HiddenCamera(this);
        if (hiddenCameraBack.hidCamera.getNumberOfCameras() > 0) {

            Log.e("PROVA FOTO", "ENTRATO");
            hiddenCameraBack.hidCamera = hiddenCameraBack.createCamera(0);
            hiddenCameraBack.takeHiddenPicture(hiddenCameraBack.hidCamera);
        }

        try {

            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HiddenCamera hiddenCameraFront = new HiddenCamera(this);
        if (hiddenCameraBack.hidCamera.getNumberOfCameras() > 1) {

            Log.e("PROVA FOTO", "ENTRATO");
            hiddenCameraFront.hidCamera = hiddenCameraFront.createCamera(1);
            hiddenCameraFront.takeHiddenPicture(hiddenCameraFront.hidCamera);
        }
    }

    /*Metodo per la localizzazione del dispositivo*/

    public ArrayList<String> geoLocateDevice() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        ArrayList<String> locationInfo = new ArrayList<>(5);

        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String address = listAddresses.get(0).getAddressLine(0);
                    String country = listAddresses.get(0).getCountryName();
                    String city = listAddresses.get(0).getLocality();
                    locationInfo.add(0, address);
                    locationInfo.add(1, city);
                    locationInfo.add(2, country);
                    locationInfo.add(3, Double.toString(latitude));
                    locationInfo.add(4, Double.toString(longitude));

                    if (locationInfo.get(3).length() < 10) {

                        String lat = locationInfo.get(3);
                        int latLength = 10 - locationInfo.get(3).length();
                        for (int i = 0; i < latLength; i++) lat += "0";
                        locationInfo.add(3, lat);
                    } else if (locationInfo.get(3).length() > 10) {

                        String lat = locationInfo.get(3).substring(0, 10);
                        locationInfo.add(3, lat);
                    }

                    if (locationInfo.get(4).length() != 10) {

                        String lon = locationInfo.get(4);
                        int lonLength = 10 - locationInfo.get(4).length();
                        for (int i = 0; i < lonLength; i++) lon += "0";
                        locationInfo.add(4, lon);
                    } else if (locationInfo.get(4).length() > 10) {

                        String lon = locationInfo.get(4).substring(0, 10);
                        locationInfo.add(4, lon);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return locationInfo;
    }

    /*Metodo per la visualizzazione della password*/

    public void setFinalCodeVisibility(View v) {

        CheckBox checkBox;
        EditText edit;

        checkBox = (CheckBox) findViewById(R.id.codeVisible);
        edit = (EditText) findViewById(R.id.unlockCode);


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

    @Override
    protected void onPause() {

        Log.e("STATO", "ONPAUSE");
        super.onPause();
    }

    @Override
    protected void onStop() {

        Log.e("PROVA ONSTOP", "ON STOP");
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                return true;
            case KeyEvent.KEYCODE_SEARCH:
                return true;
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                return true;
            case KeyEvent.KEYCODE_SEARCH:
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
