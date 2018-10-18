package lacina.geodata;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import lacina.geodata.logic.Constants;
import lacina.geodata.logic.sensors.GeoLocService;

import java.util.HashMap;
import java.util.Map;


public class InitScreen extends AppCompatActivity {

    private Map<String, Boolean> permissions_map;

    private String[] permission_names;
    /*
        Método responsável por instanciar os atrivutos das permissões
     */
    public InitScreen(){
        this.permissions_map = new HashMap<String, Boolean>();
        this.permission_names = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    /*
        Método que cria a activity e pede as permissões aos usuários
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_screen);

        for (String permission_name : permission_names){
            permissions_map.put(permission_name, false);
        }

        requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                Constants.MY_PERMISSIONS_REQUEST_COARSE_LOCATION);

        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

    }

    /*
        Método auxiliar que pede as permissões aos usuários e checa se as permissões foram aceitas, iniciando o service e a GeoDataActivity
     */
    private void requestPermission(String manifestPermission, int permissionCode){

        if (ContextCompat.checkSelfPermission(this, manifestPermission)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (checkPermissions()){
                mainActivity();
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, manifestPermission)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{manifestPermission},
                        permissionCode);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{manifestPermission},
                        permissionCode);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            permissions_map.put(manifestPermission, true);
            if (checkPermissions()){
                mainActivity();
            }
        }
    }

    /*
        Método que inicializa o service GeoLocService e a activity GeoDataActivity e termina essa activity com o método finish()
     */
    private void mainActivity(){

        Intent serviceIntent = new Intent(this, GeoLocService.class);
        startService(serviceIntent);

        Intent myIntent = new Intent(InitScreen.this, GeoDataActivity.class);
        InitScreen.this.startActivity(myIntent);
        finish();
    }


    /*
        Método que checa se as permissões foram aceitas
     */
    private boolean checkPermissions(){
        for (String permission_name : permission_names){
            if (permissions_map.get(permission_name).equals(false)){
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permissions_map.put(Manifest.permission.ACCESS_COARSE_LOCATION, true);
                    if (checkPermissions()){
                        mainActivity();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("FLOW COUNTING", "permission denied: " + Constants.MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
                }
                return;
            }

            case Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permissions_map.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                    if (checkPermissions()){
                        mainActivity();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("FLOW COUNTING", "permission denied: " + Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions_map this app might request.
        }
    }
}
