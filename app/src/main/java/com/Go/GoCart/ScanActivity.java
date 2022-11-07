package com.Go.GoCart;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.vision.barcode.Barcode.CODE_128;
import static com.google.android.gms.vision.barcode.Barcode.CODE_39;
import static com.google.android.gms.vision.barcode.Barcode.ITF;
import static com.google.android.gms.vision.barcode.Barcode.UPC_E;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_EAN_13;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_EAN_8;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_UPC_A;


public class ScanActivity extends AppCompatActivity {

    String url = null, url2 = null, url1 = null, url3 = null, json, urltext, json1, itemlist, s, category;
    Toast error, toast, noResult;
    private static final String TAG = "MyActivity";
    int scrapeusing, results, maximum = 0, minimum = 0, errorcode = 0;
    boolean imagescraper;
    AppCompatActivity activity;
    private PreviewView previewView;
    private int rotation = 0;
    int count=0;

    public static final String CAMERA_BACK = "0";

    private String cameraId = CAMERA_BACK;
    Camera camera;
    CameraControl cameraControl;

    private boolean isTorchOn = false;

    FloatingActionButton flash;
    ConstraintLayout constraintLayout;

    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);
        activity = this;
        constraintLayout = findViewById(R.id.constrained);
        category = "catto";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        results = Integer.parseInt(sharedPreferences.getString("no_of_results", "50"));
        scrapeusing = Integer.parseInt(sharedPreferences.getString("scrape_using", "2"));
        flash = findViewById(R.id.flash);

        int theme = Integer.parseInt(sharedPreferences.getString("theme", "0"));
        switch (theme) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        previewView = findViewById(R.id.previewView);
        setCameraProviderListener();
    }

    public void setCameraProviderListener() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Preview preview = new Preview.Builder()
                .build();
        CameraSelector cameraSelector =
                new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        previewView.setOnTouchListener(new MyScaleGestures(this));


        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(width, height))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @SuppressLint("UnsafeExperimentalUsageError")
            public void analyze(@NonNull ImageProxy image) {
                int rotationDegrees = image.getImageInfo().getRotationDegrees();
                Log.d(TAG, String.valueOf(rotationDegrees));
                if (image == null || image.getImage() == null) {
                    return;
                }
                Image mediaImage = image.getImage();
                switch (rotationDegrees) {
                    case 0:
                        rotation = FirebaseVisionImageMetadata.ROTATION_0;
                        break;
                    case 90:
                        rotation = FirebaseVisionImageMetadata.ROTATION_90;
                        break;
                    case 180:
                        rotation = FirebaseVisionImageMetadata.ROTATION_180;
                        break;
                    case 270:
                        rotation = FirebaseVisionImageMetadata.ROTATION_270;
                        break;
                }
                FirebaseVisionBarcodeDetectorOptions options =
                        new FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(FORMAT_EAN_13, FORMAT_UPC_A, FORMAT_EAN_8,ITF,UPC_E,CODE_128,CODE_39).build();
                FirebaseVisionImage fireimage =
                        FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
                FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                        .getVisionBarcodeDetector(options);
                @SuppressLint("RestrictedApi") Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(fireimage)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                                for (FirebaseVisionBarcode barcode : barcodes) {
                                    urltext = barcode.getRawValue();
                                }
                            }
                        })
                        .addOnCompleteListener(task -> {
                            try {
                                detector.close();
                            } catch (IOException e) {
                            }
                            url = "https://shopscrape.herokuapp.com/api?id=" + urltext;
                            url1 = "https://shopscrape1.herokuapp.com/api?id=" + urltext;
                            url2 = "https://shopscrape2.herokuapp.com/api?id=" + urltext;
                            url3 = "https://shopscrapestage.herokuapp.com/api?id=" + urltext;
                            if (urltext != null&urltext!="") {
                                s = urltext;
                                cameraProvider.unbindAll();
                                cameraProvider.shutdown();
                                cameraProvider.unbind();
                                image.close();
                                try {
                                    detector.close();
                                } catch (IOException e) {
                                }
                                if (s != null) {
                                    while(count<1){
                                    Intent intent = new Intent(ScanActivity.this, FetchActivity.class);
                                    intent.putExtra("scrapeusing", scrapeusing);
                                    intent.putExtra("url", url);
                                    intent.putExtra("url1", url1);
                                    intent.putExtra("url2", url2);
                                    intent.putExtra("url3", url3);
                                    intent.putExtra("urltext", urltext);
                                    intent.putExtra("resultcount", results);
                                    intent.putExtra("min", minimum);
                                    intent.putExtra("max", maximum);
                                    intent.putExtra("cat", category);
                                    intent.putExtra("query", s);
                                    s=null;
                                    activity.startActivity(intent);
                                    finish();
                                    break;}count++;
                                }
                            }
                        });

                image.close();
            }

        });
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        cameraControl = camera.getCameraControl();

        flash.setOnClickListener(view -> {
            cameraControl.enableTorch(!isTorchOn);
            isTorchOn = !isTorchOn;
        });
        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview);
    }


    public class MyScaleGestures implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
        private View view;
        private ScaleGestureDetector gestureScale;
        private float scaleFactor = 1;
        private boolean inScale = false;

        public MyScaleGestures(Context c) {
            gestureScale = new ScaleGestureDetector(c, this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            this.view = view;
            gestureScale.onTouchEvent(event);
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = (scaleFactor < 1 ? 1 : scaleFactor);
            scaleFactor = ((float) ((int) (scaleFactor * 100))) / 100;
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            inScale = true;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            inScale = false;
        }
    }

    public void onBackPressed() {
        ScanActivity.this.startActivityForResult(new Intent(ScanActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), 2);
        android.os.Process.killProcess(android.os.Process.myPid());
        this.finish();
    }


}