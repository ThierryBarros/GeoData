package lacina.geodata.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lacina.geodata.logic.buffers.GeoLocDataBuffer;
import lacina.geodata.logic.Profile;

/**
 * Created by tales on 31/03/18.
 */

public class DAO {

    public static Profile loadProfile(Context context, String profileName){

        //if Profile is already built
        if (Profile.instanceReady()){
            return Profile.getInstance();
        }

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = mPrefs.getString(profileName, "");
        Profile profile = gson.fromJson(json, Profile.class);

        //if there is no saved Profile
        if (profile == null){
            profile = Profile.getInstance();
            Log.d("FLOW PROFILE","CREATING NEW PROFILE");
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            profile.setId(androidId);
        }
        else{
            Profile.setInstance(profile);
        }


        return profile;
    }



     public static void saveProfile(Context context, Profile profile, String profileName){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(profile);
        prefsEditor.putString(profileName, json);
        prefsEditor.commit();
    }

    public static List<Map<String, String>> loadDataBuffer(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = mPrefs.getString("MyDataBuffer", "");
        List<Map<String, String>> myDataBuffer = gson.fromJson(json, List.class);

        if (myDataBuffer == null){
            myDataBuffer = new ArrayList<Map<String, String>>();
        }


        return myDataBuffer;

    }

    public static void saveDataBuffer(Context context){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Gson gson = new Gson();

        List<Map<String, String>> dataBuffer = GeoLocDataBuffer.getInstance().getDataBuffer();

        String json = gson.toJson(dataBuffer);


        prefsEditor.putString("MyDataBuffer", json);
        prefsEditor.commit();
    }

}
