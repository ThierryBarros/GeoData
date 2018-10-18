package lacina.geodata.logic.sensors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lacina.geodata.fragments.HomeTabController;
import lacina.geodata.logic.DummyWriter;
import lacina.geodata.logic.buffers.GeoLocDataBuffer;
import lacina.geodata.logic.Profile;
import lacina.geodata.logic.ServerCommunication;
import lacina.geodata.utils.DAO;
import android.provider.Settings.Secure;

/**
 * Created by Tales Pimentel on 11/03/18.
 */

public final class GeoLocTracker{

    private static GeoLocTracker instance = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest.Builder builder;
    private Context context;
    private RequestQueue queue;


    private Map<String, String> dataPoint;
    private int timeIterval;
    private int fastestTimeIterval;
    private long lastDataTime = 0;

    public GeoLocTracker() {
    }

    public GeoLocTracker(int timeIterval, int fastestTimeIterval, Context context) {
        this.timeIterval = timeIterval;
        this.fastestTimeIterval = fastestTimeIterval;
        this.context = context;
        queue = Volley.newRequestQueue(context);
        dataPoint = new HashMap<String, String>();

        Profile.getInstance().setId(Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
        build();
    }

    public static GeoLocTracker getInstance() {
        if(instance == null) {
            instance = new GeoLocTracker();
        }
        return instance;
    }

    public static GeoLocTracker getInstance(int timeIterval, int fastestTimeIterval, Context context) {
        if(instance == null) {
            instance = new GeoLocTracker(timeIterval, fastestTimeIterval, context);
        }
        return instance;
    }

    private void build(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
        mLocationCallback = locationCallback();
        locationRequest = createLocationRequest(this.timeIterval, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, this.fastestTimeIterval);

        builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);


    }

    private LocationCallback locationCallback(){
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    updatePointsView(); // Update UI with location dataPoint
                    Log.d("FLOW GEOLOC", "TO aqui piranha");
                    dataPoint.put("DeviceId", String.valueOf(Profile.getInstance().getId()));
                    dataPoint.put("Latitude", String.valueOf(location.getLatitude()));
                    dataPoint.put("Longitude", String.valueOf(location.getLongitude()));
                    dataPoint.put("Altitude", String.valueOf(location.getAltitude()));
                    dataPoint.put("Accuracy", String.valueOf(location.getAccuracy()));
                    dataPoint.put("Provider", String.valueOf(location.getProvider()));
                    dataPoint.put("Speed", String.valueOf(location.getSpeed()));
                    dataPoint.put("Timestamp", String.valueOf(location.getTime()));
                    dataPoint.put("Date", String.valueOf(new Date().toString()));


                    try {
                        DummyWriter.write(new JSONObject(dataPoint).toString());
                    } catch (IOException e) {
                        Log.d("FLOW GEOLOC","AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    }

                    long now = new Date().getTime();


                    if (now - lastDataTime > fastestTimeIterval) {
                        addPoints(Double.valueOf(dataPoint.get("Accuracy")));

                        ServerCommunication.sendData(queue, dataPoint);
                        DAO.saveDataBuffer(context);
                        DAO.saveProfile(context, Profile.getInstance(), "MyProfile");

                        if (isHomeTabActive()){
                            updatePointsView();
                            updateAccFlag(dataPoint.get("Accuracy"), true);
                            updateSignalQualityFlag(dataPoint.get("Accuracy"));
                        }
                        lastDataTime = new Date().getTime();
                    }


                }
            };
        };
    }

    public void addPoints(Double acc){
        Profile.getInstance().addPoints(Double.valueOf(acc));
    }

    private LocationRequest createLocationRequest(long time_interval, int priority, long fastest_interval){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(time_interval);
        locationRequest.setPriority(priority);
        locationRequest.setFastestInterval(fastest_interval);
        //  locationRequest.setSmallestDisplacement();

        return locationRequest;
    }

    public Task<LocationSettingsResponse> taskLocationSettings() {
        SettingsClient client = LocationServices.getSettingsClient(this.context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        return task;
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {

        mFusedLocationClient.requestLocationUpdates(locationRequest,
                this.mLocationCallback,
                null /* Looper */);
    }

    public void stopLocationUpdates() {
        //TODO - stop it when low battery?
        mFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
    }

    private void updatePointsView(){
        HomeTabController.getInstance().updatePointsView();
    }

    public void updateAccFlag(String accString, boolean isAccuracy){
        HomeTabController.getInstance().updateAccFlag(accString, isAccuracy);
    }

    private void updateSignalQualityFlag(String accuracy){
        HomeTabController.getInstance().updateSignalQualityFlag(Double.valueOf(accuracy));
    }

    private boolean isHomeTabActive(){
        return HomeTabController.isHomeTabAlive();
    }

}
