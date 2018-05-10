package middletownmusic.org.westcoastradio.utils;

import middletownmusic.org.westcoastradio.models.Station;

public interface AdapterCallback {
    void openRadioCallback(Station station, int index);
}