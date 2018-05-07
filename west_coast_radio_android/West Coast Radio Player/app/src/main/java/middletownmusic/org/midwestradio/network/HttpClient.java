package middletownmusic.org.midwestradio.network;


import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClient {

    public static final String GET = "GET";
    public static final String POST = "POST";
    private HttpURLConnection connection;
    private URL url;
    private String paramString;
    private int code;
    private JSONObject responseObject = null;
    private Callback callback;

    HttpClient(String url, String endpoint, Callback callback) throws MalformedURLException {
        this.url = new URL(url + endpoint);
        this.callback = callback;
    }

    void execute(final String method) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initializeRequest(method);
                writeRequestBody();
                readRequestResponse();
                if (code == 200) {
                    System.out.println("!!!!!!!!!!!!!  "+code);
                    callback.success(responseObject);
                } else {
                    callback.failure(responseObject);
                }
            }
        }).start();
    }

    private void initializeRequest(String method) {
        Log.wtf("net init", "start");
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            if(method.equals(POST)){
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }else {
                connection.setRequestProperty("Content-Type", "application/json");
            }
            connection.setRequestProperty("charset", "utf-8");
        } catch (Exception e) {
            Log.wtf("stack", "trace");
            e.printStackTrace();
        }
    }

    private void writeRequestBody() {
        if (paramString == null) {
            connection.setDoOutput(false);
        } else {
            try {
                String requestParams = getRequestData();
                byte[] requestAsBytes = requestParams.getBytes("utf-8");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", String.valueOf(requestAsBytes.length));
                connection.setDoOutput(true);
                connection.getOutputStream().write(requestAsBytes);
                connection.getOutputStream().flush();
                connection.getOutputStream().close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.wtf("HTTPRequest", "An IO error occurred when writing request body");
            }
        }
    }

    private void readRequestResponse() {
        String response;
        try {
            code = connection.getResponseCode();
            response = inputStreamToString(connection.getInputStream());
            responseObject =  getResponseObject(response);
        } catch (IOException e) {
            Log.wtf("HTTPRequest", "An IO error occurred when read request response: " + e.toString());
            callback.failure(null);
        }
    }

    private String getRequestData() {
        return "station_id=" + paramString;
    }

    private static String inputStreamToString(InputStream is) {
        int numBytesRead;
        String finalString = "";
        byte[] bytes = new byte[2048];
        try {
            while ((numBytesRead = is.read(bytes, 0, bytes.length)) != -1) {
                String s = new String(bytes,0,numBytesRead);
                finalString+=s;
            }
            return finalString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject getResponseObject(String response) {
        try {
             responseObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.wtf("HTTPRequest", "The response was not formatted as JSON correctly");
        }
        return responseObject;
    }

    void setParamString(String input) {
        this.paramString = input;
    }

    public interface Callback {
        void success(JSONObject result);
        void failure(JSONObject result);
    }
}
