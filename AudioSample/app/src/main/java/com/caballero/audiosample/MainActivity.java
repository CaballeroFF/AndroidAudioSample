package com.caballero.audiosample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.caballero.audiosample.audio.calculators.AudioCalculator;
import com.caballero.audiosample.audio.core.Callback;
import com.caballero.audiosample.audio.core.Recorder;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    public static final int AMP_THRESHOLD_MIN = 2000;
    public static final int AMP_THRESHOLD_MAX = 3000;

    private TextView mAmp;
    private TextView mDb;
    private TextView mFreq;

    private Recorder mRecorder;
    private AudioCalculator mAudioCalculator;
    private Handler mHandler;

    private boolean mRecordPermission = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        setupViews();

        mRecorder = new Recorder(callback);
        mAudioCalculator = new AudioCalculator();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecorder.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRecorder.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            mRecordPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!mRecordPermission) finish();
    }

    private void setupViews() {
        mAmp = findViewById(R.id.amplitude_text);
        mDb = findViewById(R.id.decibel_text);
        mFreq = findViewById(R.id.frequency_text);
    }

    private Callback callback = new Callback() {
        @Override
        public void onBufferAvailable(byte[] buffer) {
            mAudioCalculator.setBytes(buffer);
            int amplitude = mAudioCalculator.getAmplitude();
            double decibel = mAudioCalculator.getDecibel();
            double frequency = mAudioCalculator.getFrequency();

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (amplitude >= 2000 && amplitude <= 3000) {
                        if (decibel > -5d && decibel < 2d) {
                            Log.i("TAG", "run: E note at " + frequency + " Hz " + decibel + " Db " + amplitude + " amp");
                        }
//                        if (frequency > 80d && frequency < 90d) {
//                            Log.i("TAG", "run: E note at " + frequency + " Hz " + decibel + " Db " + amplitude + " amp");
//                        }
//                        if (frequency > 105d && frequency < 112d) {
//                            Log.i("TAG", "run: A note at " + frequency + " Hz " + decibel + " Db " + amplitude + " amp");
//                        }
//                        if (frequency > 143d && frequency < 149d) {
//                            Log.i("TAG", "run: D note at " + frequency + " Hz " + decibel + " Db " + amplitude + " amp");
//                        }
//                        if (frequency > 193d && frequency < 199d) {
//                            Log.i("TAG", "run: G note at " + frequency + " Hz " + decibel + " Db " + amplitude + " amp");
//                        }
//                        if (frequency > 243d && frequency < 249d) {
//                            Log.i("TAG", "run: B note at " + frequency + " Hz " + decibel + " Db " + amplitude + " amp");
//                        }
//                        if (frequency > 326d && frequency < 331d) {
//                            Log.i("TAG", "run: e note at " + frequency + " Hz " + decibel + " Db " + amplitude + " amp");
//                        }
                        mAmp.setText(Integer.toString(amplitude));
                        mDb.setText(Double.toString(decibel));
                        mFreq.setText(Double.toString(frequency));
                    }
                }
            });
        }
    };
}