package com.example.gmahn.myapplication;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapView;

public class MainActivity extends AppCompatActivity {
    private static final String APP_KEY = "5467b06c-6ee4-4afc-a6f4-f5998493465f";

    private TMapView tMapView;
    private LocationManager locationManager;

    private LinearLayout linTMapView;
    private CameraPreviewView cpvBlackBox;

    private ImageButton ibVideoCapture;
    private Button btnCheckPairedBT;

    private boolean isRecording;

    private SensorManager sensorManager;
    private Sensor sensorAccel;
    private NotiAlert notiAlert;

    boolean using = false;
    boolean isUp=false;
    boolean isDown=false;

    double accel=0.0;
    double gravity=9.8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
        initSensor();
        initTMapView();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener , sensorAccel, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void initView() {
        linTMapView = findViewById(R.id.linTMapView);
        cpvBlackBox = findViewById(R.id.cpvBlackBox);
        ibVideoCapture = findViewById(R.id.ibVideoCapture);
        btnCheckPairedBT = findViewById(R.id.btnCheckPairedBT);
    }

    private void initEvent() {
        ibVideoCapture.setOnClickListener(ibVideoCaptureClickListener);
        btnCheckPairedBT.setOnClickListener(btnCheckPairedBTClickListener);
    }

    private void initSensor() {
        sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @SuppressWarnings("MissingPermission")
    private void initTMapView() {
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey(APP_KEY);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

        linTMapView.addView(tMapView);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            tMapView.setCenterPoint(lon, lat);
            tMapView.setLocationPoint(lon, lat);
            tMapView.setIconVisibility(true);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            accel = Math.sqrt( (x*x) + (y*y) + (z*z));

            if(accel - gravity > 7)
                isUp = true;

            if(isUp && gravity - accel > 3)
                isDown = true;

            if(isDown){
                notiAlert = new NotiAlert(getApplicationContext()) ;
                notiAlert.createNotificationChannel("충격감지", "캡쳐완료");
                cpvBlackBox.captureCamera(MainActivity.this);
                using =false;
                isUp=false;
                isDown=false;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private View.OnClickListener ibVideoCaptureClickListener = v -> {
        cpvBlackBox.recordVideo(isRecording, MainActivity.this);
        if (isRecording) {
            Toast.makeText(this, "촬영을 종료합니다.", Toast.LENGTH_SHORT).show();
            ibVideoCapture.setImageResource(R.drawable.switch_default_2);
            isRecording = false;
        }
        else {
            Toast.makeText(this, "촬영을 시작합니다.", Toast.LENGTH_SHORT).show();
            ibVideoCapture.setImageResource(R.drawable.switch_capture_2);
            isRecording = true;
        }
    };

    private View.OnClickListener btnCheckPairedBTClickListener = v ->
            startActivity(new Intent(getApplicationContext(), BTPairedListActivity.class));
}
