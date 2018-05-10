package middletownmusic.org.westcoastradio.network;

import java.net.MalformedURLException;


public class Requests {

    private static final String BASE_URL = "http://willshare.com/cs495/WestCoastRadioPlayer/";
    private static final String GET_STATIONS = "GetAllApplicationData.php";
    private static final String POST_VOTE = "AddVote.php";

    public static void getApplicationData(HttpClient.Callback callback) {
        try {
            HttpClient request = new HttpClient(BASE_URL,GET_STATIONS , callback);
            request.execute(HttpClient.GET);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void addVote(HttpClient.Callback callback, String id){
        try {
            HttpClient request = new HttpClient(BASE_URL, POST_VOTE, callback);
            request.setParamString(id);
            request.execute(HttpClient.POST);
        } catch (MalformedURLException e) {
            System.out.println("Error, bad URL");
            e.printStackTrace();
        }
    }
}
