package middletownmusic.org.midwestradio.fragments;


import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import middletownmusic.org.midwestradio.R;
import middletownmusic.org.midwestradio.models.Station;
import middletownmusic.org.midwestradio.utils.AdapterCallback;

public class StationListFragment extends ListFragment{

    ArrayList<Station> currentStations = new ArrayList<>();
    private AdapterCallback callback;
    StationListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            currentStations = savedInstanceState.getParcelableArrayList("data");
        }else{
            currentStations = getArguments().getParcelableArrayList("data");
        }
        View view = inflater.inflate(R.layout.station_list_fragment, container, false);
        if(adapter == null){
            makeAdapter(currentStations);
        }
        return view;
    }

    protected void makeAdapter(ArrayList<Station> list){
        Collections.sort(list, new Comparator<Station>() {
            public int compare(Station s1, Station s2) {
                return s1.getLong_name().toLowerCase().compareTo(s2.getLong_name().toLowerCase());
            }
        });
        // sorting list into alpha order
        adapter = new StationListAdapter(getActivity(), list, callback );
        setListAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("data", currentStations);
        super.onSaveInstanceState(outState);
    }

    public void setCallback(AdapterCallback callback) {
        this.callback = callback;
    }
}

