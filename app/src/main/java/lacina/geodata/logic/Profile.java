package lacina.geodata.logic;

import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by tales on 31/03/18.
 */

public class Profile {
    private String androidId;
    private int length = 8;
    private Double points;
    private boolean isEnable = true; //if user has enabled the data collection
    private List<Long> currentTickets;
    private List<Long> pastTickets;

    private static Profile instance = null;


    public Profile() {
        points = Double.valueOf(23);
        currentTickets = new ArrayList<Long>();

    }

    public void setPoints(Double points){
        this.points = points;
    }

    public Double getPoints(){
        return this.points;
    }

    public String textPoints(){
        return String.format("%0" + length + "d",  Math.round(getPoints()));
    }



    public void addPoints(Double accuracy) {
        Double increment = 10 / Math.sqrt(accuracy + 1);
        points = points + increment;
    }

    public boolean getIsEnabled(){
        return this.isEnable;
    }

    public void setIsEnabled(boolean isEnable){
        this.isEnable = isEnable;
    }

    public void setId(String id){
        this.androidId = id;
    }

    public String getId(){
        return this.androidId;
    }

    public static Profile getInstance() {
        if(instance == null) {
            Log.d("FLOW", "Profile >>> NEW PROFILE");
            instance = new Profile();
        }
        return instance;
    }

    public static boolean instanceReady(){
        if (instance == null){
            return false;
        }
        return true;
    }

    public static void setInstance(Profile profile){

        instance = profile;
    }

    public List<Long> getCurrentTickets(){
        return this.currentTickets;
    }

    public void setCurrentTickets(List<Long> currentTickets){
        this.currentTickets = currentTickets;
    }

    public void addCurrentTicket(Long ticket){
        this.currentTickets.add(ticket);
    }

}