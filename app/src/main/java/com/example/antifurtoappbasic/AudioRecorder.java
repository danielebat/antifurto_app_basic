package com.example.antifurtoappbasic;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;

/*Classe che gestisce la registrazione dell'audio per una durata massima di 5 secondi*/

public class AudioRecorder implements MediaRecorder.OnInfoListener {

    private MediaRecorder recorder = null;
    private String path = Environment.getExternalStorageDirectory().toString();

    public AudioRecorder(String specificPath) {

        this.path += specificPath;
        Log.i("PATH", path);
    }

    public void recordAudio() {

        this.recorder = new MediaRecorder();
        this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);

        this.recorder.setOutputFile(path);
        this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        this.recorder.setMaxDuration(5000);
        this.recorder.setOnInfoListener(this);

        try {

            this.recorder.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.recorder.start();
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {

            mr.stop();
            mr.release();
            mr = null;
        }

    }
}
