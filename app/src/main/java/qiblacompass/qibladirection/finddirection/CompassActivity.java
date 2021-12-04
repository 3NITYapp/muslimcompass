package qiblacompass.qibladirection.finddirection;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import qiblacompass.qibladirection.finddirection.custom_sensor.AccelerometerView;
import qiblacompass.qibladirection.finddirection.pref.ConfigPreferences;

public class CompassActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {
    private TextView countryName, Quibladegree, ifnosensortxt, notetxt;
    public static int quiblaDegree;
    private RelativeLayout compass, compassMapContainer, innerPosition;
    private ImageView indicator, redCircle, baseimg, pointerIndicatorInner;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private boolean mLastAccelerometerSet = false, switchView = false,
            pointerPosition = true, mLastMagnetometerSet = false, start = false, mapReady = false;
    private double previousAzimuthInDegrees = 0f;
    private long SensorSendTime;
    private double lastRoll, lastPitch, lastTime;
    private GoogleMap googleMap;
    private double fusedLatitude = 0.0;
    private double fusedLongitude = 0.0;
    private RelativeLayout compassMain;
    private AccelerometerView mAccelerometerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_compass);

        ConfigPreferences.setQuibla(this, -140);
        quiblaDegree = ConfigPreferences.getQuibla(this);
        init();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            // Success! There's a magnetometer.
            ifnosensortxt.setVisibility(View.GONE);
            indicator.setVisibility(View.VISIBLE);
            baseimg.setVisibility(View.VISIBLE);
            notetxt.setVisibility(View.VISIBLE);
        }
        else {
            // Failure! No magnetometer.
            ifnosensortxt.setVisibility(View.VISIBLE);
            indicator.setVisibility(View.GONE);
            baseimg.setVisibility(View.GONE);
            notetxt.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    private void init() {
        mAccelerometerView = (AccelerometerView) findViewById(R.id.accelerometer_view);
        countryName = (TextView) findViewById(R.id.textView11);

        //init compass activity views
        Quibladegree = (TextView) findViewById(R.id.textView12);
        Quibladegree.setText(getString(R.string.qibla_direction) + " " + ConfigPreferences.getQuibla(this));
        indicator = (ImageView) findViewById(R.id.imageView2);
        compass = (RelativeLayout) findViewById(R.id.compassContainer);
        baseimg = (ImageView) findViewById(R.id.baseimg);
        notetxt = (TextView) findViewById(R.id.notetxt);
        compassMapContainer = (RelativeLayout) findViewById(R.id.compassMapContainer);
        compassMain = (RelativeLayout) findViewById(R.id.compassMain);
        ifnosensortxt = (TextView) findViewById(R.id.ifnosensortxt);
        innerPosition = (RelativeLayout) findViewById(R.id.innerplace);
        pointerIndicatorInner = (ImageView) findViewById(R.id.poinerInner);
        redCircle = (ImageView) findViewById(R.id.red_circle);

        //init sensor services
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //animate compass pointer
        RotateAnimation ra = new RotateAnimation(currentDegree, quiblaDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(400);
        ra.setFillAfter(true);
        indicator.startAnimation(ra);
        pointerIndicatorInner.startAnimation(ra);
    }

    public double lowPass(double input, double lastOutput, double dt) {
        double elapsedTime = dt - SensorSendTime;
        Log.d("TIMESEND", elapsedTime + "");
        SensorSendTime = (long) dt;
        elapsedTime = elapsedTime / 1000;
        final double lagConstant = 1;
        double alpha = elapsedTime / (lagConstant + elapsedTime);
        return alpha * input + (1 - alpha) * lastOutput;
    }

    public double lowPassPointerLevel(double input, double lastOutput, double dt) {
        final double lagConstant = 0.25;
        double alpha = dt / (lagConstant + dt);
        return alpha * input + (1 - alpha) * lastOutput;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        double startTime = System.currentTimeMillis();

        if (event.sensor == mAccelerometer) {
            mLastAccelerometer = event.values;
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            mLastMagnetometer = event.values;
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            boolean success = SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            double azimuthInDegress = -(float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            if (Math.abs(azimuthInDegress - previousAzimuthInDegrees) > 300) {
                previousAzimuthInDegrees = azimuthInDegress;
            }

            azimuthInDegress = lowPass(azimuthInDegress, previousAzimuthInDegrees, startTime);

            if (mapReady) updateCamera((float) azimuthInDegress);

            RotateAnimation ra = new RotateAnimation(
                    (float) previousAzimuthInDegrees,
                    (float) azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(500);
            ra.setFillAfter(true);
            compass.startAnimation(ra);
            innerPosition.startAnimation(ra);

            previousAzimuthInDegrees = azimuthInDegress;

            if (pointerPosition == true) {
                pointerPosition = false;
            }

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(mR, orientation);
                Log.d("newxy", orientation[0]+"");
                double yaw = orientation[0] * 57.2957795f;
                double pitch = orientation[1] * 57.2957795f;
                double roll = orientation[2] * 57.2957795f;
                if (pitch > 90) pitch -= 180;
                if (pitch < -90) pitch += 180;
                if (roll > 90) roll -= 180;
                if (roll < -90) roll += 180;

                double time = System.currentTimeMillis();

                if (!start) {
                    lastTime = time;
                    lastRoll = roll;
                    lastPitch = pitch;
                }
                start = true;


                double dt = (time - lastTime) / 1000.0;
                roll = lowPassPointerLevel(roll, lastRoll, dt);
                pitch = lowPassPointerLevel(pitch, lastPitch, dt);
                lastTime = time;
                lastRoll = roll;
                lastPitch = pitch;

                mAccelerometerView.getSensorValue().setRotation((float)azimuthInRadians, (float)roll, (float)pitch);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        mapReady = true;

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("msg", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("msg", "Can't find style. Error: ", e);
        }
    }

    private void updateCamera(float bearing) {

        CameraPosition oldPos = googleMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos)
                .target(new LatLng(getFusedLatitude(), getFusedLongitude()))
                .zoom(17)
                .bearing(360-bearing)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        1).show();
            }
            return false;
        }

        return true;
    }

    public double getFusedLatitude() {
        return fusedLatitude;
    }

    public double getFusedLongitude() {
        return fusedLongitude;
    }

    public static void startMe(Context context){
        Intent intent = new Intent(context, CompassActivity.class);
        context.startActivity(intent);
//        ((Activity)context).finish();
    }
}

