package middletownmusic.org.midwestradio.utils;

import middletownmusic.org.midwestradio.models.Station;

public interface AdapterCallback {
    void openRadioCallback(Station station, int index);
}