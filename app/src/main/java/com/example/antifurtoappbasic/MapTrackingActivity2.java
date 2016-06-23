package com.example.antifurtoappbasic;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*Classe per la creazione della mappa e della visione dei punti in cui sono state invitae segnalazioni*/

public class MapTrackingActivity2 extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private int mapType = 0;
    private ArrayList<String> stringCoordinates = new ArrayList<String>();
    private ArrayList<Double> latitudes = new ArrayList<Double>();
    private ArrayList<Double> longitudines = new ArrayList<Double>();
    private ArrayList<LatLng> positions = new ArrayList<LatLng>();
    private boolean firstExecution = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking2);

        refreshPositions(this.getCurrentFocus());
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstExecution = true;
        refreshPositions(getCurrentFocus());
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    /*Metodo per la creazione della mappa*/

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    private void setUpMap() {

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.geodesic(true);

        for (int i = 0; i < this.positions.size(); i++) {

            polylineOptions.add(this.positions.get(i));
            mMap.addMarker(new MarkerOptions().position(this.positions.get(i)).title("Posizione " + Integer.toString(this.positions.size() - i)));
        }


        mMap.addPolyline(polylineOptions);

        LatLng mean = computeMeanValue(this.positions);
        float radius = computeRadius(this.positions, mean);

        if (firstExecution) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mean, 10.0f));

        else Toast.makeText(this, "Positions Updated", Toast.LENGTH_SHORT).show();
    }

    /*Metodo per il cambiamento della mappa*/

    public void changeMapType(View v){

        if (this.mapType == 0) this.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        else this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        this.mapType = (this.mapType + 1) % 2;
    }

    /*Metodo per il refresh delle posizioni visibili sulla mappa*/

    public void refreshPositions(View v){

        extractMessagesContent();
        if (firstExecution) {

            setUpMapIfNeeded();
            firstExecution = false;
        }
        else setUpMap();
    }

    /*Metodo per centrare la visualizzazione dei punti sulla mappa*/

    private LatLng computeMeanValue(ArrayList<LatLng> points){

        double meanLat = 0.0, meanLng = 0.0;

        for (int i = 0; i < points.size(); i++){

            meanLat += points.get(i).latitude;
            meanLng += points.get(i).longitude;
        }

        meanLat = meanLat / points.size();
        meanLng = meanLng / points.size();

        return new LatLng(meanLat, meanLng);
    }

    private float computeRadius(ArrayList<LatLng> points, LatLng mean){

        float radius = 0.0f;

        for(int i = 0; i < points.size(); i++){

            Location l1 = new Location("A");
            l1.setLatitude(mean.latitude);
            l1.setLongitude(mean.longitude);
            Location l2 = new Location("B");
            l2.setLatitude(points.get(i).latitude);
            l2.setLongitude(points.get(i).longitude);
            radius = l1.distanceTo(l2) > radius ? l1.distanceTo(l2) : radius;
        }

        return radius;
    }

    /*Metodo per l'estrazione di messaggi importanti dalla casella dei messaggi*/

    private void extractMessagesContent(){

        this.positions.clear();
        this.longitudines.clear();
        this.latitudes.clear();
        this.stringCoordinates.clear();

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

        Date yesterday = new Date(System.currentTimeMillis()-24*60*60*1000);

        Intent intent = getIntent();
        String targetNumber = intent.getStringExtra("phoneNumber");
        while (cursor.moveToNext()) {

            String address = cursor.getString(cursor.getColumnIndex("address"));
            if (address.equals(targetNumber)) {

                String body = cursor.getString(cursor.getColumnIndex("body"));
                //Log.e("PROVA BODY", body);
                if (body.length() >= 27) {

                    String recognizer = body.substring(0, 27);
                    if (recognizer.equals("ANTIFURTO APP - RILEVAMENTO")) {

                        String smsDate = cursor.getString(cursor.getColumnIndex("date"));
                        Long timestamp = Long.parseLong(smsDate);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timestamp);
                        Date finaldate = calendar.getTime();
                        if (finaldate.after(yesterday)) {

                            int index = this.stringCoordinates.size();
                            this.stringCoordinates.add(index, body);
                        }
                    }
                }
            }
        }

        Log.e("PROVA SIZE", Integer.toString(this.stringCoordinates.size()));

        if (this.stringCoordinates.size() == 0){

            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED,returnIntent);
            finish();
        }

        for (int i = 0; i < this.stringCoordinates.size(); i++){

            String lat = this.stringCoordinates.get(i).substring(this.stringCoordinates.get(i).length() - 21, this.stringCoordinates.get(i).length() - 11);
            String lon = this.stringCoordinates.get(i).substring(this.stringCoordinates.get(i).length() - 10, this.stringCoordinates.get(i).length());
            double doubleLat = Double.parseDouble(lat);
            double doubleLon = Double.parseDouble(lon);
            this.latitudes.add(i, doubleLat);
            this.longitudines.add(i, doubleLon);
        }



        for (int i = 0; i < this.longitudines.size(); i++)
            this.positions.add(i, new LatLng(this.latitudes.get(i), this.longitudines.get(i)));
    }
}
