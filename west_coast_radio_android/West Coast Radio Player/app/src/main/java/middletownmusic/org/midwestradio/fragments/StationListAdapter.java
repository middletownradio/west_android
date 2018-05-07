package middletownmusic.org.midwestradio.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import middletownmusic.org.midwestradio.R;
import middletownmusic.org.midwestradio.models.Station;
import middletownmusic.org.midwestradio.utils.AdapterCallback;

public class StationListAdapter extends ArrayAdapter<Station>{

    private AdapterCallback callback;

    public StationListAdapter(Context context, ArrayList<Station> contacts, AdapterCallback callback) {
        super(context, 0, contacts);
        this.callback = callback;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.station_row, parent, false);
        }
        LinearLayout listRow = convertView.findViewById(R.id.listRow);
        TextView stationName = convertView.findViewById(R.id.listrow_station_name);
        Station station = getItem(position);
        if (station != null) {
            stationName.setText(station.getLong_name());
        }

        listRow.setTag(position);
        listRow.setOnClickListener(view -> {
            int index = (Integer) view.getTag();
            callback.openRadioCallback(station, index);
        });
        return convertView;
    }
}
