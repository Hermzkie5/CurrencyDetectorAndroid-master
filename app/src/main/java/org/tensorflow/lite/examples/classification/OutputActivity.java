package org.tensorflow.lite.examples.classification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

//import org.tensorflow.lite.examples.classification.ml.MobilenetV110224Quant;
import org.tensorflow.lite.examples.classification.tflite.Classifier;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;

public class OutputActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = OutputActivity.class.getSimpleName();
    private ImageButton backButton, saveButton;
    private ImageView resutImage;
    private Classifier classifier;
    private Bitmap rgbFrameBitmap = null, bitMap;
    private Integer sensorOrientation;

    public static Intent newIntent(Context context, String path) { Log.d(TAG,"newIntent()");
        return new Intent(context.getApplicationContext(), OutputActivity.class).putExtra(Constants.FILE_PATH, path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_page);
        Log.d(TAG,"File Path" + getIntent().getStringExtra(Constants.FILE_PATH));
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        resutImage = findViewById(R.id.resutImage);
        resutImage.setImageURI(Uri.parse(getIntent().getStringExtra(Constants.FILE_PATH)));
        try {
            bitMap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(getIntent().getStringExtra(Constants.FILE_PATH)));
            Bitmap resized = Bitmap.createScaledBitmap(bitMap, 224, 224, true);

            //Context context = createPackageContext("org.tensorflow.lite.examples.classification", 0);
            //AssetManager assetManager = context.getAssets();
            //MappedByteBuffer model = MappedByteBuffer(assetManager, "mobilenet_v1_1.0_224_quant.tflite");
            Model model = Model.newInstance(this);

            TensorImage tbuffer = TensorImage.fromBitmap(resized);
            ByteBuffer byteBuffer = tbuffer.getBuffer();

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[] {1, 224, 224, 3}, DataType.UINT8);
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();
        } catch (PackageManager.NameNotFoundException | IOException e) {
            e.printStackTrace();
        }
        checkandGetpermissions();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                startActivity(HomePageActivity.newIntent(this));
                break;
            case R.id.saveButton:
                Toast.makeText(this,"Save Button",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        backButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        scan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        backButton.setOnClickListener(null);
        saveButton.setOnClickListener(null);
    }

    private void scan() {
        resutImage.post(new Runnable() {
            @Override
            public void run() { //Log.d(TAG,"scan() " + resutImage.getMeasuredWidth() + " " + resutImage.getMeasuredHeight());
                //rgbFrameBitmap = Bitmap.createBitmap(resutImage.getMeasuredWidth(), resutImage.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                //sensorOrientation = getScreenOrientation();
                //Log.d(TAG,"rgbFrameBitmap " + rgbFrameBitmap + " sensorOrientation " + sensorOrientation);
                //List<Classifier.Recognition> results = classifier.recognizeImage(rgbFrameBitmap, sensorOrientation);
                //Log.d(TAG,"Results " + Arrays.toString(results.toArray()));
            }
        });

    }

    private int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void checkandGetpermissions() {
        if(ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
        }
        else{
            Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}