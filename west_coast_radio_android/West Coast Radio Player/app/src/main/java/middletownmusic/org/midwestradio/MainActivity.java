package middletownmusic.org.midwestradio;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import middletownmusic.org.midwestradio.fragments.AboutFragment;
import middletownmusic.org.midwestradio.fragments.HelpFragment;
import middletownmusic.org.midwestradio.fragments.LoadingFragment;
import middletownmusic.org.midwestradio.fragments.RadioFragment;
import middletownmusic.org.midwestradio.fragments.StationInfoFragment;
import middletownmusic.org.midwestradio.fragments.StationListFragment;
import middletownmusic.org.midwestradio.models.Station;
import middletownmusic.org.midwestradio.utils.DrawerUtils;


public class MainActivity extends AppCompatActivity implements DrawerUtils.DrawerItemClickListener {

    private boolean listAdded = false;
    ArrayList<Station> allStations = new ArrayList<>();
    private ViewGroup root;
    private View mainView;
    private DrawerLayout drawerLayout;
    private RelativeLayout drawer;
    private ListView drawerItems;
    private DrawerUtils.DrawerAdapter drawerAdapter;
    private LoadingFragment.LoadingListener loadingListener;
    private ImageButton menuButton;
    private ImageButton helpButton;
    private Bundle stationBundle;
    private Bundle helpBundle;
    private Bundle fragments;
    private int currentStation = 0;
    private String currentPage = "radio";
    private RadioFragment radio;
    private StationListFragment station = new StationListFragment();
    private LoadingFragment loadFragment = new LoadingFragment();
    private StationInfoFragment stationInfoFragment;

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            allStations = savedInstanceState.getParcelableArrayList("stations");
            listAdded = savedInstanceState.getBoolean("listAdded");
            currentPage = savedInstanceState.getString("currentPage");
            fragments = savedInstanceState.getBundle("fragmentBundle");
            stationBundle = new Bundle();
            stationBundle.putParcelableArrayList("data", allStations);
            mainView = LayoutInflater.from(this).inflate(R.layout.activity_main, root, false);
            setContentView(mainView);
            if(getSupportFragmentManager().findFragmentByTag("station") != null){
                station = (StationListFragment) getSupportFragmentManager().getFragment(fragments, "station");
                station.setCallback(this::openStationInfo);
            }else if(currentPage.equals("station")){
                station.setCallback(this::openStationInfo);
                station.setArguments(stationBundle);
                getSupportFragmentManager().popBackStackImmediate();
                changeFragment(station);
            }
            radio = (RadioFragment) getSupportFragmentManager().getFragment(fragments, "radio");
            radio.setArguments(stationBundle);
            initializeButtons();
        }
        else{
            radio = new RadioFragment();
            mainView = LayoutInflater.from(this).inflate(R.layout.activity_main, root, false);
            setContentView(mainView);
            initializeButtons();
            menuButton.setVisibility(View.GONE);
            currentPage = "loading";
            changeFragment(loadFragment);
            loadingListener = list -> {
                allStations = list;
                runOnUiThread(this::setUpRadio);
            };
            loadFragment.setListener(loadingListener);
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        currentStation = radio.getCurrentStation();
        outState.putParcelableArrayList("stations", allStations);
        outState.putInt("currentStation", currentStation);
        outState.putBoolean("listAdded", listAdded);
        outState.putString("currentPage", currentPage);
        Bundle fragments = new Bundle();
        if(getSupportFragmentManager().findFragmentByTag("station") != null ){
            //check for station fragment
            getSupportFragmentManager().putFragment(fragments, "station", station);
        }
        getSupportFragmentManager().putFragment(fragments, "radio", radio);
        outState.putBundle("fragmentBundle", fragments);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpRadio(){
        station.setCallback(this::openStationInfo);
        prepareDrawer();
        stationBundle = new Bundle(1);
        stationBundle.putParcelableArrayList("data", allStations);
        stationBundle.putInt("currentStation", currentStation);
        radio.setArguments(stationBundle);
        menuButton.setVisibility(View.VISIBLE);
        helpButton.setVisibility(View.VISIBLE);
        currentPage = "loading";
        changeFragment(radio);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        currentPage = "radio";
    }

    private void initializeButtons(){
        menuButton = mainView.findViewById(R.id.menuButton);
        helpButton = mainView.findViewById(R.id.helpButton);
        helpButton.setVisibility(View.VISIBLE);
        menuButton.setOnClickListener(v -> toggleMenu());
        prepareDrawer();
        helpButton.setOnClickListener(v -> showHelp());
    }

    @Override
    public void drawerItemClicked(DrawerUtils.DrawerItem item) {
        switch(item.title){
            case "All Stations":
                if(currentPage.equals("station")){
                    drawerLayout.closeDrawer(drawer);
                    break;
                }
                listAdded = true;
                station.setCallback(this::openStationInfo);
                station.setArguments(stationBundle);
                currentPage = "station";
                int orientation = getResources().getConfiguration().orientation;
                if(orientation == 1){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else{
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }
                changeFragment(station);
                drawerLayout.closeDrawer(drawer);
                break;
            case "Purple Calves":
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://purplecalves.com"));
                startActivity(browserIntent);
                break;
            case "Submit a Station":
                Intent browserIntentSubmit = new Intent(Intent.ACTION_VIEW, Uri.parse("http://willshare.com/cs495/MidwestRadioPlayer/frontend/#/submit"));
                startActivity(browserIntentSubmit);
                break;
            case "Report a Station":
                Intent browserIntentReport = new Intent(Intent.ACTION_VIEW, Uri.parse("http://willshare.com/cs495/MidwestRadioPlayer/frontend/#/report"));
                startActivity(browserIntentReport);
                break;
            case "About":
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                AboutFragment aboutFragment = new AboutFragment();
                transaction.add(aboutFragment,"About").commit();
                break;
        }
    }

    private void prepareDrawer(){
        drawerLayout = mainView.findViewById(R.id.drawer_layout);
        drawer = mainView.findViewById(R.id.drawer);
        drawerItems = mainView.findViewById(R.id.drawer_items);
        drawerAdapter = new DrawerUtils.DrawerAdapter(this);
        drawerItems.setAdapter(drawerAdapter);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayViewWidth_ = size.x;
        ViewGroup.LayoutParams params = drawer.getLayoutParams();
        params.width = 3 *(displayViewWidth_ / 4);
        drawer.setLayoutParams(params);
    }

    private void toggleMenu(){
        drawerLayout.openDrawer(drawer);
    }

    private void openStationInfo(Station station, int index){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        stationInfoFragment = new StationInfoFragment();
        stationInfoFragment.setStationChangeCallback(this::changeRadio);
        stationBundle.putInt("index", index);
        stationInfoFragment.setArguments(stationBundle);
        transaction.add(stationInfoFragment,"StationInfo").commit();
    }

    private void changeRadio(Station station, int index){
        getSupportFragmentManager().beginTransaction().remove(stationInfoFragment).commit();
        stationBundle.putBoolean("isPlaying", true);
        stationBundle.putInt("currentStation", index);
        radio.setArguments(stationBundle);
        getSupportFragmentManager().popBackStackImmediate("station", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        changeFragment(radio);
        currentPage = "radio";
    }

    private void changeFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(currentPage.equals("radio")) {
            getSupportFragmentManager().popBackStackImmediate("radio", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if(fragment == station){
            getSupportFragmentManager().popBackStack("station", 0);
            transaction.addToBackStack("radio");
            currentPage = "station";
        }else if(fragment == radio && !currentPage.equals("loading")){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            getSupportFragmentManager().popBackStack("radio", 0);
            transaction.addToBackStack("station");
            currentPage = "radio";
        }

        transaction.replace(R.id.fragment_container,(fragment));
        transaction.commit();
    }

    @Override
    public void onBackPressed(){
        if(currentPage.equals("radio")){
            finish();
        }else{
            currentStation = radio.getCurrentStation();
            currentPage = "radio";
            stationBundle.putInt("currentStation", currentStation);
            radio.setArguments(stationBundle);
            changeFragment(radio);
        }
    }

    private void showHelp(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        HelpFragment helpFragment = new HelpFragment ();
        helpBundle = new Bundle();
        helpBundle.putString("page", currentPage);
        helpFragment.setArguments(helpBundle);
        transaction.add(helpFragment,"Help Dialog").commit();
    }

}
