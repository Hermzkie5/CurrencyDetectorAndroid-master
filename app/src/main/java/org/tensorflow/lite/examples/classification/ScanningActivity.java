package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ScanningActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = ScanningActivity.class.getSimpleName();

    public static Intent newIntent(Context context, String path) { Log.d(TAG,"newIntent()");
        return new Intent(context.getApplicationContext(), ScanningActivity.class).putExtra(Constants.FILE_PATH, path);
    }

    private ImageButton imageButtonBack;
    private CountDownTimer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) { Log.d(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning_page);
        Log.d(TAG,"File Path" + getIntent().getStringExtra(Constants.FILE_PATH));
        imageButtonBack = findViewById(R.id.imageButtonBack);
        timer = new CountDownTimer(2000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick(" + millisUntilFinished + ")");
            }

            @Override
            public void onFinish() { Log.d(TAG,"onFinish()");
                launchOutput();
            }
        }.start();
    }

    private void launchOutput() { Log.d(TAG,"launchOutput()");
        startActivity(OutputActivity.newIntent(this, getIntent().getStringExtra(Constants.FILE_PATH)));
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageButtonBack.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageButtonBack.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() { Log.d(TAG,"onBackPressed()");
        timer.cancel();
        super.onBackPressed();
    }
}