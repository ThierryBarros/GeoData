package lacina.geodata.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.util.ArrayList;

import lacina.geodata.R;
import lacina.geodata.utils.UtilColors;

/**
 * Class to manage HomeTab views.
 * Created by tales on 02/04/18.
 */

public class ContextTabController {

    private static ContextTabController instance = null;
    private static Activity activity;

    private ArrayList<Button> contextButtons;
    private Button okButton;
    private Button selectedButton;
    private boolean isOkButtonEnabled = false;
    private boolean isContextButtonsEnabled = false;

    private ContextTabController() {  }

    private ContextTabController(Activity actv) {
        activity = actv;
    }

    public static ContextTabController getInstance() {
        if(instance == null) {
            instance = new ContextTabController();
        }
        return instance;
    }

    public static ContextTabController getInstance(Activity activity) {
        if(instance == null) {
            instance = new ContextTabController(activity);
        }
        return instance;
    }

    public static void killInstance() {
        instance = null;
    }

    public void launchContextQuestion(){
        enableContextButtons();
        selectedButton = null;
    }

    public void createContextButtons(){
        ArrayList<Button> buttons = new ArrayList<Button>();
        Button button;

        button = activity.findViewById(R.id.button_casa);
        buttons.add(button);

        button = activity.findViewById(R.id.button_trabalho);
        buttons.add(button);

        button = activity.findViewById(R.id.button_lazer);
        buttons.add(button);

        button = activity.findViewById(R.id.button_esportes);
        buttons.add(button);

        button = activity.findViewById(R.id.button_educacao);
        buttons.add(button);

        button = activity.findViewById(R.id.button_outros);
        buttons.add(button);

        this.contextButtons = buttons;
        disableContextButtons(buttons);
        setContextClickEvent(buttons);
    }

    public void enableContextButton(Button button){
        final Animation animShake = AnimationUtils.loadAnimation(activity, R.anim.shake);

        button.setBackgroundColor(Color.BLUE);
        button.setTextColor(Color.WHITE);
//        button.startAnimation(animShake);
    }

    public void enableContextButtons(){
        for (Button contextButton : contextButtons) {
            enableContextButton(contextButton);
        }

        isContextButtonsEnabled = true;
    }

    public void disableContextButtons(ArrayList<Button> buttons){
        lowLightButtons(buttons);
        isContextButtonsEnabled = false;
    }

    public void lowLightButtons(ArrayList<Button> buttons){
        for (Button button : buttons){
            button.setBackgroundColor(Color.LTGRAY);
            button.setTextColor(Color.WHITE);
        }
    }

    public void setContextClickEvent(ArrayList<Button> buttons){
        final ArrayList<Button> contextButtons = buttons;

        for (Button button : buttons){

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (isContextButtonsEnabled){
                        lowLightButtons(contextButtons);
                        enableContextButton((Button) v);
                        enableOkButton();
                        selectedButton = (Button) v;

                    }

                }
            });
        }
    }

    public void createOkButton(){
        okButton = activity.findViewById(R.id.button_ok);
        disableOkButton();
        setOkClickEvent();
    }

    public void enableOkButton(){
        okButton.setBackgroundColor(UtilColors.GREEN);
        okButton.setTextColor(Color.WHITE);
        isOkButtonEnabled = true;
    }

    public void disableOkButton(){
        okButton.setBackgroundColor(Color.LTGRAY);
        okButton.setTextColor(Color.WHITE);
        isOkButtonEnabled = false;
    }

    public void setOkClickEvent(){
        final Animation animShake = AnimationUtils.loadAnimation(activity, R.anim.shake);

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isOkButtonEnabled){
                    okButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //after the button animation ends the following are executed
                            disableContextButtons(contextButtons);
                            disableOkButton();
                        }
                    }, 100);
//                    okButton.startAnimation(animShake);
                }
            }
        });
    }



}

