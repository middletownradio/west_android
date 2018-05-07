package middletownmusic.org.midwestradio.models;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class StationListFactoryTest {

    JSONArray input = new JSONArray();
    JSONArray input2 = new JSONArray();

    @Before
    public void setUp() throws Exception {
        input = new JSONArray();
        String sampleInfoFull = "{\n" +
                "        \"active\": [\n" +
                "            {\n" +
                "                \"id\": \"106\",\n" +
                "                \"frequency\": \"91.3\",\n" +
                "                \"long_name\": \"WCRD Ball State U.\",\n" +
                "                \"short_name\": \"WCRD\",\n" +
                "                \"city\": \"Muncie\",\n" +
                "                \"state\": \"IN\",\n" +
                "                \"slogan\": \"Always Better Radio\",\n" +
                "                \"active\": \"1\",\n" +
                "                \"deleted\": \"0\",\n" +
                "                \"type\": \"College\",\n" +
                "                \"genre\": \"Variety\",\n" +
                "                \"stream\": \"http://dvisweb1.bsu.edu:9000/WCRD128k\",\n" +
                "                \"website\": \"http://wcrd.net/about/\",\n" +
                "                \"user_entered\": \"0\",\n" +
                "                \"first_station\": \"1\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"1\",\n" +
                "                \"frequency\": \"89.3\",\n" +
                "                \"long_name\": \"WIUX Indiana U.\",\n" +
                "                \"short_name\": \"WIUX\",\n" +
                "                \"city\": \"Bloomington\",\n" +
                "                \"state\": \"IN\",\n" +
                "                \"slogan\": \"Pure Student Radio\",\n" +
                "                \"active\": \"1\",\n" +
                "                \"deleted\": \"0\",\n" +
                "                \"type\": \"College radio\",\n" +
                "                \"genre\": \"Other\",\n" +
                "                \"stream\": \"http://hannibal.ucs.indiana.edu:8080/wiuxhigh\",\n" +
                "                \"website\": \"https://wiux.org/\",\n" +
                "                \"user_entered\": \"0\",\n" +
                "                \"first_station\": \"0\"\n" +
                "            }" +
                "]}";

        String sampleInfoEmpty = "{\n" +
                "        \"active\": [\n" +
                "            {\n" +
                "                \"id\": \"\",\n" +
                "                \"frequency\": \"\",\n" +
                "                \"long_name\": \"\",\n" +
                "                \"short_name\": \"\",\n" +
                "                \"city\": \"\",\n" +
                "                \"state\": \"\",\n" +
                "                \"slogan\": \"\",\n" +
                "                \"active\": \"\",\n" +
                "                \"deleted\": \"\",\n" +
                "                \"type\": \"\",\n" +
                "                \"genre\": \"\",\n" +
                "                \"stream\": \"\",\n" +
                "                \"website\": \"\",\n" +
                "                \"user_entered\": \"\",\n" +
                "                \"first_station\": \"\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"null\",\n" +
                "                \"frequency\": \"null\",\n" +
                "                \"long_name\": \"\",\n" +
                "                \"short_name\": \"\",\n" +
                "                \"city\": \"\",\n" +
                "                \"state\": \"\",\n" +
                "                \"slogan\": \"\",\n" +
                "                \"active\": \"\",\n" +
                "                \"deleted\": \"\",\n" +
                "                \"type\": \" \",\n" +
                "                \"genre\": \"\",\n" +
                "                \"stream\": \"\",\n" +
                "                \"website\": \"\",\n" +
                "                \"user_entered\": \"\",\n" +
                "                \"first_station\": \"\"\n" +
                "            }" +
                "]}";

        JSONObject ob = new JSONObject(sampleInfoFull);
        JSONArray arr = ob.getJSONArray("active");
        this.input = arr;

        ob = new JSONObject(sampleInfoEmpty);
        arr = ob.getJSONArray("active");
        this.input2 = arr;
        
    }

    @Test
    public void makeStationListTest() throws Exception {
        StationListFactory test = new StationListFactory();
        for (Station s: test.makeObjects(input)) {
            assertEquals(Station.class, s.getClass() );
        }
    }

    @Test
    public void makeStationListEmptyTest() throws Exception {
        StationListFactory test = new StationListFactory();
        for (Station s: test.makeObjects(input2)) {
            assertEquals(Station.class, s.getClass() );
        }
    }
}