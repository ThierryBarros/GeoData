package lacina.geodata.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.support.constraint.ConstraintLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import lacina.geodata.R;
import lacina.geodata.logic.Profile;
import lacina.geodata.logic.sensors.GeoLocTracker;
import lacina.geodata.utils.DAO;

public class HomeTab extends Fragment {
    private ConstraintLayout fragmentContainer;

    private GeoLocTracker geoLocTracker;
    private Task<LocationSettingsResponse> task;
    private Switch switchEnableTracking;
    private Button onOffButton;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FLOW HOME", "onCreate");

        geoLocTracker = GeoLocTracker.getInstance();
        task = geoLocTracker.taskLocationSettings();
        checkLocationSettings(true, task);

        getActivity().registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fragmentContainer = (ConstraintLayout) view.findViewById(R.id.fragment_home);

        this.view = view;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        switchEnableTracking = createSwitchEnableTracking(view);
        switchEnableTracking.setChecked(Profile.getInstance().getIsEnabled());
        HomeTabController.getInstance().updatePointsView();

    }


    private Switch createSwitchEnableTracking(View view){
        Switch switchEnableTracking = (Switch) view.findViewById(R.id.switch_enable);
        switchEnableTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    geoLocTracker.startLocationUpdates();
                    geoLocTracker.updateAccFlag("ESPERANDO...", false);
                    HomeTabController.getInstance().updateSignalQualityFlag(null);
                    if (Profile.instanceReady()){
                        Profile.getInstance().setIsEnabled(true);
                    }
                    DAO.saveProfile(getContext(), Profile.getInstance(), "MyProfile");

                } else {
                    geoLocTracker.stopLocationUpdates();
                    geoLocTracker.updateAccFlag("DESLIGADO", false);
                    HomeTabController.getInstance().updateSignalQualityFlag(Double.valueOf(-1));
                    if (Profile.instanceReady()){
                        Profile.getInstance().setIsEnabled(false);
                    }
                    DAO.saveProfile(getContext(), Profile.getInstance(), "MyProfile");
                }
            }
        });

        return switchEnableTracking;
    }

    private void checkLocationSettings(boolean askEnable, Task<LocationSettingsResponse> task) {
        locationSettingsSuccess(task);
        locationSettingsFail(task, askEnable);
    }

    private void locationSettingsSuccess(Task task){
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (Profile.getInstance().getIsEnabled()){
                    geoLocTracker.updateAccFlag("ESPERANDO...", false);
                    HomeTabController.getInstance().updateSignalQualityFlag(null);
                }
                else{
                    geoLocTracker.updateAccFlag("DESLIGADO", false);
                    HomeTabController.getInstance().updateSignalQualityFlag(Double.valueOf(-1));

                }
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
//                geoLocTracker.startLocationUpdates();
            }
        });
    }

    private void locationSettingsFail(Task task, final boolean askEnableLoc){
        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        geoLocTracker.updateAccFlag("OFFLINE", false);
                        if (askEnableLoc){
                            int REQUEST_CHECK_SETTINGS = 10;
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        }
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }



    // Android location option turned on or off
    private BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                checkLocationSettings(false, task);
            }
        }
    };

}
