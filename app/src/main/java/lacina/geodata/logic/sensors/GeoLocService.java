package lacina.geodata.logic.sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;

import lacina.geodata.logic.buffers.GeoLocDataBuffer;
import lacina.geodata.logic.Profile;
import lacina.geodata.logic.ServerCommunication;
import lacina.geodata.utils.DAO;

/**
 * Created by tales on 01/04/18.
 */

public class GeoLocService extends Service implements SensorEventListener {

    private int TIME_INTERVAL = 10000;
    private int FASTEST_INTERVAL = 7000;

    private GeoLocTracker geoLocTracker;
    private GeoLocDataBuffer geoLocDataBuffer;
    private Task<LocationSettingsResponse> task;
    private Profile profile;
    private SharedPreferences mPrefs;

    private SensorManager mSensorManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initLocation();
        initSensors();

        return super.onStartCommand(intent, flags, startId);
    }

    private void initSensors() {

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY), SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        //float[] orientation = SensorManager.getOrientation();
    }

    private void initLocation() {
        Log.d("FLOW LOC", "TO AQUIIIIIIIIIIIIIII");
        profile = DAO.loadProfile(getBaseContext(), "MyProfile");
        geoLocTracker = GeoLocTracker.getInstance(TIME_INTERVAL, FASTEST_INTERVAL, this);
        geoLocDataBuffer = GeoLocDataBuffer.getInstance();
        geoLocDataBuffer.setDataBuffer(DAO.loadDataBuffer(this));

        if (profile.getIsEnabled()) {
            startLocationsUpdates();
        }


    }

    public void startLocationsUpdates() {
        geoLocTracker.startLocationUpdates();
    }

    public void stopLocationsUpdates() {
        geoLocTracker.stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DAO.saveDataBuffer(this);
        DAO.saveProfile(getBaseContext(), profile, "MyProfile");
    }

    private void sendProfileData(){
        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        ServerCommunication.checkTickets(queue);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;


        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            
        }/*else if(sensor.getType() == Sensor.TYPE_GRAVITY){

        }else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){

        }else if(sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){

        }else if(sensor.getType() == Sensor.TYPE_LIGHT){

        }else if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){

        }else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){

        }else if(sensor.getType() == Sensor.TYPE_PRESSURE){

        }else if(sensor.getType() == Sensor.TYPE_PROXIMITY){

        }else if(sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY){

        }else if(sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){

        }*/


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
