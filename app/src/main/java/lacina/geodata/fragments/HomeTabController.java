package lacina.geodata.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import lacina.geodata.R;
import lacina.geodata.logic.Profile;
import lacina.geodata.utils.UtilColors;

/**
 * Class to manage HomeTab views.
 * Created by tales on 02/04/18.
 */

public class HomeTabController {

    //    private Profile profile = Profile.getInstance();
    private static HomeTabController instance = null;
    private static Activity activity;

    private HomeTabController() {
    }

    private HomeTabController(Activity actv) {
        activity = actv;
    }

    public static HomeTabController getInstance() {
        if (instance == null) {
            instance = new HomeTabController();
        }
        return instance;
    }

    public static HomeTabController getInstance(Activity activity) {
        if (instance == null) {
            instance = new HomeTabController(activity);
        }
        return instance;
    }

    public static void killInstance() {
        instance = null;
    }

    public void updatePointsView() {
        TextView textPoints = this.activity.findViewById(R.id.textPoints);

        if (textPoints != null && Profile.instanceReady()) {
            textPoints.setText(Profile.getInstance().textPoints());
        }
    }

    public void updateAccFlag(String accString, boolean isAccuracy) {

        Button accButton = this.activity.findViewById(R.id.acc_circle);
        int color;
        String txt;

        if (accString != null && isAccuracy) {
            Double acc = Double.valueOf(accString);
            txt = "Acc: " + Math.round(Double.valueOf(acc));

            if (acc < 85) {
                color = UtilColors.VERY_GOOD_ACC;
            } else if (acc < 200) {
                color = UtilColors.MID_ACC;
            } else if (acc >= 200) {
                color = UtilColors.BAD_ACC;
            } else {
                color = UtilColors.OFF;
            }
        } else if (accString != null && !isAccuracy) {
            color = UtilColors.OFF;
            txt = accString;
        } else {
            color = UtilColors.OFF;
            txt = "OFFLINE";
        }

        if (accButton != null) {
            accButton.setBackgroundColor(color);
            accButton.setText(txt);
        }
    }

    public void updateSignalQualityFlag(Double accuracy) {
        TextView signalQualityText = this.activity.findViewById(R.id.text_signal_quality_value);
        int color;
        String txt;

        if (accuracy == null) {
            txt = "ESPERANDO...";
            color = UtilColors.WHITE;
        } else if (accuracy == -1) {
            txt = "<< DESLIGADO >>";
            color = UtilColors.RED;
        } else if (accuracy < 30) {
            txt = "MUITO BOA";
            color = UtilColors.VERY_GOOD_ACC;
        } else if (accuracy < 60) {
            txt = "BOA";
            color = UtilColors.GOOD_ACC;
        } else if (accuracy < 100) {
            txt = "CONSIDERAVEL";
            color = UtilColors.MID_ACC;
        } else if (accuracy < 250) {
            txt = "PODE MELHORAR";
            color = UtilColors.MID_ACC;
        } else if (accuracy >= 250) {
            txt = "RUIM";
            color = UtilColors.BAD_ACC;
        } else {
            txt = "DESCONHECIDA";
            color = Color.WHITE;
        }

        if (signalQualityText != null){
            signalQualityText.setText(txt);
            signalQualityText.setBackgroundColor(color);

        }
    }

    public static boolean isHomeTabAlive() {
        if (activity == null) {
            return false;
        } else {
            return true;
        }
    }

}
