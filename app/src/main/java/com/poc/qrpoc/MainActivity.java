package com.poc.qrpoc;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.Manifest;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends Activity {

    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private RectangleView rectangleView;
    private TextView detailTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        rectangleView = findViewById(R.id.rectangleView);
        detailTV = findViewById(R.id.detailTV);
        rectangleView.bringToFront();
        createCameraSource();
    }

    private void createCameraSource() {
        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(640, 480)
                .setRequestedFps(60.0f)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA}, 200);
                    }
                } else {
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                System.out.print("detector has been released");
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                if (isConnected()) {

                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (barcodes.size() > 0) {
                                Barcode codigo = barcodes.valueAt(0);
                                mostrarDetalle(codigo);
                                Point[] points = barcodes.valueAt(0).cornerPoints;
                                for (int i = 0; i < points.length; i++) {
                                    points[i].x = xToDP(points[i].x);
                                    points[i].y = yToDP(points[i].y);
                                }
                                rectangleView.points = barcodes.valueAt(0).cornerPoints;
                                rectangleView.postInvalidate();
                                detailTV.setText(barcodes.valueAt(0).displayValue);
                                detailTV.setY(points[0].y - detailTV.getLayout().getHeight());
                                detailTV.setX(points[0].x);


                            } else {
                                rectangleView.points = null;
                                rectangleView.postInvalidate();
                                detailTV.setText("");
                            }
                        }
                    });
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });
    }

    public Boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void mostrarDetalle(Barcode barcode) {
        System.out.print(barcode.displayValue);
    }

    public int xToDP(int x) {
        return x * surfaceView.getWidth() / 480;
    }

    public int yToDP(int y) {
        return y * surfaceView.getHeight() / 640;
    }
}