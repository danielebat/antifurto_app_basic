package com.example.antifurtoappbasic;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/*Classe per la gestione dello scatto di foto con entrambe le fotocamere in maniera nascosta*/

public class HiddenCamera {

    private Context context = null;
    public Camera hidCamera = null;
    private Activity parent = null;
    private int cameraId = -1;

    public HiddenCamera(Activity parentActivity) {

        this.context = parentActivity.getApplicationContext();
        this.parent = parentActivity;
    }

    public Camera createCamera(int i) {

        Camera myCamera = Camera.open(i);
        this.cameraId = i;
        return myCamera;
    }

    /*Metodo per lo scatto della foto*/

    public void takeHiddenPicture(final Camera mycamera) {

        this.parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Camera.Parameters parameters = mycamera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                mycamera.setParameters(parameters);
                mycamera.setDisplayOrientation(180);

                SurfaceView mview = new SurfaceView(parent.getApplicationContext());

                try {

                    Log.e("PROVA", "FUNZIONA");
                    mycamera.setPreviewDisplay(mview.getHolder());
                    mycamera.startPreview();
                    mycamera.takePicture(null, null, photoCallback);

                    Thread.sleep(2000);

                    mycamera.release();

                } catch (IOException e) {

                    e.printStackTrace();
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        });
    }

    /*Evento che gestisce il salvataggio della foto dopo lo scatto*/

    Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            String path = Environment.getExternalStorageDirectory().toString() + "/AntifurtoApp/photo/photo" + Integer.toString(cameraId) + ".jpg";
            File photo = new File(path);
            Uri uriTarget = Uri.fromFile(photo);

            Log.e("URI", uriTarget.toString());
            OutputStream imageFileOS;

            try {

                imageFileOS = parent.getApplicationContext().getContentResolver().openOutputStream(uriTarget);
                imageFileOS.write(data);
                imageFileOS.flush();
                imageFileOS.close();

                Log.e("PROVA", "FUNZIONA");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("PROVA", "ECCEZIONE 1");

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("PROVA", "ECCEZIONE 2");

            }
        }
    };
}