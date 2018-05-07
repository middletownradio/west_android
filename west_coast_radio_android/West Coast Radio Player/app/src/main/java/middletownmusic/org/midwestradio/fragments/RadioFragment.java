package middletownmusic.org.midwestradio.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import middletownmusic.org.midwestradio.R;
import middletownmusic.org.midwestradio.audio.AudioService;
import middletownmusic.org.midwestradio.models.Station;
import middletownmusic.org.midwestradio.network.HttpClient;
import middletownmusic.org.midwestradio.network.Requests;

public class RadioFragment extends Fragment{

    private Button playButton;
    private Button stopButton;
    private Button nextButton;
    private Button previousButton;
    private Button oneButton;
    private Button twoButton;
    private Button threeButton;
    private Button fourButton;
    private Button fiveButton;
    private Button sixButton;
    private Button nextScan;
    private Button previousScan;
    private ImageButton webButton;
    private TextView shortName;
    private TextView frequency;
    private TextView description;
    private TextView location;
    private TextView genre;
    private TextView type;
    private int currentStation = 0;
    private int currentPlayingStation;
    ArrayList<Station> currentStations;
    AudioService audioService;
    boolean audioBound = false;
    private boolean isPlaying = false;
    private boolean isScanning = false;
    private boolean isRotating = false;
    private boolean isNextScan = false;
    private boolean shouldPlay = false;
    private Handler myHandler;
    private Scanner scanner;
    private String[] preset = {"-1", "-1", "-1", "-1", "-1", "-1"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("########### creating ");
        currentStation = getArguments().getInt("currentStation");
        shouldPlay = getArguments().getBoolean("isPlaying");
        if(savedInstanceState != null){
            //gets saved bundle
            currentStation = savedInstanceState.getInt("currentStation");
            currentStations = savedInstanceState.getParcelableArrayList("data");
            isScanning = savedInstanceState.getBoolean("isScanning");
            isNextScan = savedInstanceState.getBoolean("isNextScan");
        }else{
            currentStations = getArguments().getParcelableArrayList("data");
        }
        return inflater.inflate(R.layout.radio_fragment, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        scanner = new Scanner();
        playButton = getView().findViewById(R.id.playButton);
        stopButton = getView().findViewById(R.id.stopButton);
        nextButton = getView().findViewById(R.id.next);
        previousButton = getView().findViewById(R.id.previous);
        shortName = getView().findViewById(R.id.short_name);
        frequency = getView().findViewById(R.id.frequency);
        description = getView().findViewById(R.id.description);
        location = getView().findViewById(R.id.location);
        genre = getView().findViewById(R.id.genre);
        type = getView().findViewById(R.id.type);
        oneButton = getView().findViewById(R.id.button1);
        twoButton = getView().findViewById(R.id.button2);
        threeButton = getView().findViewById(R.id.button3);
        fourButton = getView().findViewById(R.id.button4);
        fiveButton = getView().findViewById(R.id.button5);
        sixButton = getView().findViewById(R.id.button6);
        nextScan = getView().findViewById(R.id.next_scan);
        previousScan = getView().findViewById(R.id.previous_scan);
        setStationInformation(currentStation);
        setButtonClicks();
        setRadioButtons();
        myHandler = new Handler();
        if(isPlaying){
            playButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.greenIndicator, null));
        }
    }

    @Override
    public void onPause(){
        //writes preset array to file called presets
        String filename = "presets";
        FileOutputStream outputStream;
        try {
            outputStream = getContext().openFileOutput(filename, 0);
            for (String s : preset) {
                outputStream.write(s.getBytes());
                outputStream.write(System.getProperty("line.separator").getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // cancel scanner async task if app is exiting and not just changing orientation
        if (!getActivity().isChangingConfigurations()) {
            scanner.cancel(true);
        }
        super.onPause();
    }

    @Override
    public void onResume(){
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        Intent intent = new Intent(getActivity(), AudioService.class);
        getActivity().bindService(intent, audioServiceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
        // if AudioService is connected we can resume playing
        if(audioBound){
            isPlaying = audioService.isPlaying();
            if(shouldPlay) {
                play(false);
            }
        }
        if(isPlaying){
            playButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.greenIndicator, null));
        }else{
            playButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        }
        readPresets();
    }

    private void readPresets(){
        // reads presets from file and sets them to the appropriate button
        File myFile = new File( getContext().getFileStreamPath("presets").getAbsolutePath() );
        if(!myFile.exists()){
            System.out.println("************* file not found");
        }else{
            try {
                BufferedReader br = new BufferedReader(new FileReader(myFile));
                String line = br.readLine();
                int index = 0;
                while(line != null){
                    if(index < 6 && !line.equals("-1")){
                        preset[index] = line;
                        setSavedPreset(index, line);
                    }
                    line = br.readLine();
                    index++;
                }
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSavedPreset(int index, String id){
        Button button = findButton(index);
        for (int i = 0; i < currentStations.size(); i++){
            Station station = currentStations.get(i);
            if(station.getId().equals(id) && button != null){
                button.setText(station.getShort_name());
                int finalI = i;
                button.setOnClickListener(v -> changeToStation(finalI));
            }
        }
    }

    private Button findButton(int index){
        switch(index){
            case 0: return oneButton;
            case 1: return twoButton;
            case 2: return threeButton;
            case 3: return fourButton;
            case 4: return fiveButton;
            case 5: return sixButton;
            default: return null;
        }
    }

    private void setStationInformation(int index){
        shortName.setText(currentStations.get(index).getShort_name());
        frequency.setText(currentStations.get(index).getFrequency());
        description.setText(String.format("%s%s\"", '"', currentStations.get(index).getSlogan()));
        location.setText((currentStations.get(index).getCity() + ", " + currentStations.get(index).getState()));
        genre.setText(("Genre: " + currentStations.get(index).getGenre()));
        type.setText(("Type: " + currentStations.get(index).getType()));
        webButton = getView().findViewById(R.id.websiteImageButton);
        if(currentStations.get(index).getWebsite().length() > 3){
            webButton.setOnClickListener(view -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentStations.get(index).getWebsite()));
                startActivity(browserIntent);
            });
        }else{
            webButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        isRotating = getActivity().isChangingConfigurations();
        outState.putParcelableArrayList("data", currentStations);
        outState.putInt("currentStation", currentStation);
        outState.putBoolean("isPlaying", isPlaying);
        outState.putBoolean("isScanning", isScanning);
        outState.putBoolean("isNextScan", isNextScan);
        super.onSaveInstanceState(outState);
    }

    private ServiceConnection audioServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("################################### service connecting "+ isScanning);
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            audioService = binder.getService();
            audioBound = true;
            isPlaying = audioService.isPlaying();
            if(shouldPlay || isPlaying ){
                playButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.greenIndicator, null));
                currentPlayingStation = currentStation;
            }
            if(isScanning && isNextScan){
                nextScan();
            }else if (isScanning){
                previousScan();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("################################### service disconnecting");
            audioBound = false;
        }
    };

    private void setButtonClicks(){
        playButton.setOnClickListener(v -> play(false));
        stopButton.setOnClickListener(v -> stop());
        nextButton.setOnClickListener(v -> nextStation(false));
        previousButton.setOnClickListener(v -> previousStation(false));
        nextScan.setOnClickListener(v -> nextScan());
        previousScan.setOnClickListener(v -> previousScan());
    }
    
    private void setRadioButtons(){
        oneButton.setOnLongClickListener(v -> changePreset(oneButton, currentStation, 1));
        twoButton.setOnLongClickListener(v -> changePreset(twoButton, currentStation, 2));
        threeButton.setOnLongClickListener(v -> changePreset(threeButton, currentStation, 3));
        fourButton.setOnLongClickListener(v -> changePreset(fourButton, currentStation, 4));
        fiveButton.setOnLongClickListener(v -> changePreset(fiveButton, currentStation, 5));
        sixButton.setOnLongClickListener(v -> changePreset(sixButton, currentStation, 6));
    }

    private void changeToStation(int stationId){
        isScanning = false;
        setStationInformation(stationId);
        currentStation = stationId;
        play(false);
    }

    private boolean changePreset(Button button, int currentStationIndex, int index){
        String stationId = currentStations.get(currentStationIndex).getId();
        button.setText(currentStations.get(currentStationIndex).getShort_name());
        button.setOnClickListener(v -> changeToStation(currentStationIndex));
        preset[index-1] = stationId;

        if(isVoteUnique(stationId)){
            String filename = "votes";
            FileOutputStream outputStream;
            try {
                // write id string and a new line to file
                outputStream = getContext().openFileOutput(filename, 0);
                outputStream.write(stationId.getBytes());
                outputStream.write(System.getProperty("line.separator").getBytes());
                outputStream.close();
                sendVote(stationId);
                // add that vote
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("******************** vote not unique");
        }
        return true;
    }

    private void sendVote(String id) {
        Requests.addVote(new HttpClient.Callback() {
            @Override
            public void success(JSONObject result) {
                System.out.println("******************** vote Success!");
            }

            @Override
            public void failure(JSONObject result) {
                System.out.println("******************** vote Failed");
            }
        }, id);
    }

    private boolean isVoteUnique(String id){
        File myFile = new File( getContext().getFileStreamPath("votes").getAbsolutePath() );
        if(!myFile.exists()){
            System.out.println("************* file not found for votes");
        }else{
            try {
                BufferedReader br = new BufferedReader(new FileReader(myFile));
                String line = br.readLine();
                while(line != null){
                    if(line.equals(id)){
                        return false;
                    }
                    line = br.readLine();
                }
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void play(boolean isCalledFromScan){
        currentPlayingStation = currentStation;
        stopScanning();
        audioService.startPlaying(currentStations.get(currentStation).getStream());
        playButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.greenIndicator, null));
        isPlaying = true;
        if(!isCalledFromScan){
            isScanning = false;
        }
    }

    private void stop(){
        isScanning = false;
        audioService.stopPlaying();
        flashRed(stopButton);
        stopScanning();
        isPlaying = false;
    }

    private void nextStation(boolean isCalledFromScan){
        if(currentStation != currentStations.size()-1){
            currentStation++;
        }else{
            currentStation = 0;
        }
        setStationInformation(currentStation);
        if(!isCalledFromScan){
            stopScanning();
            flashGreen(nextButton);
        }
    }

    private void nextScan(){
        isNextScan = true;
        scan(true);
        nextScan.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        isScanning = true;
        Integer in = 0;
        scanner.cancel(true);
        scanner = new Scanner();
        scanner.execute(in);
    }

    private void previousScan(){
        scan(false);
        isNextScan = false;
        previousScan.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        isScanning = true;
        scanner.cancel(true);
        scanner = new Scanner();
        Integer in = 1;
        scanner.execute(in);
    }

    private void scan(boolean isNext){
        if(isNext){
            nextStation(true);
        }else{
            previousStation(true);
        }
        play(true);
    }

    private void stopScanning(){
        scanner.cancel(true);
        isScanning = false;
        nextScan.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
        previousScan.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
    }

    private void previousStation(boolean isCalledFromScan){
        if(currentStation != 0){
            currentStation--;
        }else{
            currentStation = currentStations.size()-1;
        }
        setStationInformation(currentStation);
        if(!isCalledFromScan){
            stopScanning();
            flashGreen(previousButton);
        }
    }

    private void flashGreen(Button btn){
        btn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.greenIndicator, null));
        myHandler.postDelayed(() -> btn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null)), 500);
    }

    private void flashRed(Button btn){
        btn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
        myHandler.postDelayed(() -> btn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null)), 500);
        playButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
    }

    public int getCurrentStation(){
        if(isPlaying){
            return currentPlayingStation;
        }
        return currentStation;
    }

    private class Scanner extends AsyncTask<Integer, Void, Integer > {

        @Override
        protected Integer doInBackground(Integer ... params) {
            //wait 5 seconds on different thread before onPostExecute is called
            try {
                Thread.sleep(5100);
            } catch (InterruptedException e) {
                stopScanning();
                e.printStackTrace();
            }
            return params[0];
            //return integer value to retain state of next vs previous scanning.
        }

        @Override
        protected void onPostExecute(Integer scanState) {
            super.onPostExecute(null);
            if (getView() != null) {
                if(isScanning && !isRotating){
                    if(scanState == 0){
                        nextScan();
                    }else{
                        previousScan();
                    }
                }else if(!isRotating){
                    stopScanning();
                }
            }
        }
    }
}
