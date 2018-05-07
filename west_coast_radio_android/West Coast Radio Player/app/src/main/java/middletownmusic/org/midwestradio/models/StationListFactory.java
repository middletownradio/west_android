package middletownmusic.org.midwestradio.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;

public class StationListFactory {

    public ArrayList<Station> makeObjects(JSONArray jsonArray) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Station> contactList = new ArrayList<>();
        try {
            for(int i = 0; i< jsonArray.length(); i++){
                contactList.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), Station.class));
            }
            return contactList;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contactList;
    }
}
