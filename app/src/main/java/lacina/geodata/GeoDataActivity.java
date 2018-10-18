package lacina.geodata;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import lacina.geodata.fragments.ContextTab;
import lacina.geodata.fragments.ContextTabController;
import lacina.geodata.fragments.HomeTab;
import lacina.geodata.fragments.HomeTabController;
import lacina.geodata.fragments.RaffleTab;
import lacina.geodata.fragments.RaffleTabController;
import lacina.geodata.logic.Profile;
import lacina.geodata.logic.ServerCommunication;
import lacina.geodata.logic.sensors.GeoLocService;
import lacina.geodata.utils.DAO;

public class GeoDataActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Profile profile;
    private HomeTabController homeTabController;
    private ContextTabController contextTabController;
    private RaffleTabController raffleTabController;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToHomeTab();
                    mTextMessage.setText("");
                    return true;
                case R.id.navigation_dashboard:
                    switchToContextTab();
                    mTextMessage.setText("");
                    return true;
                case R.id.navigation_notifications:
                    switchToRaffleTab();
                    mTextMessage.setText("");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_data);

        profile = DAO.loadProfile(getBaseContext(), "MyProfile");
        homeTabController = HomeTabController.getInstance(this);
        contextTabController = ContextTabController.getInstance(this);
        raffleTabController = RaffleTabController.getInstance(this);

        ServerCommunication.raffles(Volley.newRequestQueue(getBaseContext()));

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        switchToHomeTab();

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();

        HomeTabController.killInstance();
        ContextTabController.killInstance();
        RaffleTabController.killInstance();
    }

    public void switchToHomeTab() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new HomeTab()).commit();
    }

    public void switchToContextTab() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new ContextTab()).commit();
    }

    public void switchToRaffleTab() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new RaffleTab()).commit();
    }

}
