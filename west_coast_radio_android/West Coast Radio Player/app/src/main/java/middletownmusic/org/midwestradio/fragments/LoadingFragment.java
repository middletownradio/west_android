package middletownmusic.org.midwestradio.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import middletownmusic.org.midwestradio.R;
import middletownmusic.org.midwestradio.models.Station;
import middletownmusic.org.midwestradio.models.StationListFactory;
import middletownmusic.org.midwestradio.network.HttpClient;
import middletownmusic.org.midwestradio.network.Requests;


public class LoadingFragment extends Fragment {

    public boolean attemptedLoad = false;
    private TextView loadingText;
    private ImageView leftTriangle;
    private ImageView rightTriangle;
    private ImageView headphones;
    private ImageView headphoneMiddle;
    private Handler loadingHandler;
    private boolean loadSuccessful = false;
    private ArrayList<Station> allStations;
    private LoadingListener listener;
    private boolean shouldAnimate = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.loading_fragment, container, false);
        loadingText = view.findViewById(R.id.loading_text);
        leftTriangle = view.findViewById(R.id.left_triangle);
        rightTriangle = view.findViewById(R.id.right_triangle);
        headphones = view.findViewById(R.id.headphones);
        headphoneMiddle = view.findViewById(R.id.mwm);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(() -> {
            double height = headphones.getHeight();
            double width = headphones.getWidth();
            int space = (int) Math.round(height * ((float)34/(float)51));
            leftTriangle.setPadding(0, space, 0, 0);
            rightTriangle.setPadding(0, space, 0, 0);
            space = (int) Math.round(width - ( ((float)177/(float)636))*2 );
            headphoneMiddle.getLayoutParams().width = space;
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        loadingHandler = new Handler();
        Runnable loadThread = () -> {
            while(shouldAnimate) {
                animateHeadphones();
                animateLoadingText();
            }
        };
        new Thread(loadThread).start();
        attemptLoad();
    }

    public void attemptLoad() {
        Requests.getApplicationData(new HttpClient.Callback() {
            @Override
            public void success(JSONObject result) {
                loadSuccessful = true;
                StationListFactory factory = new StationListFactory();
                try {
                    JSONObject data = result.getJSONObject("data");
                    allStations = factory.makeObjects(data.getJSONArray("active"));
                    attemptedLoad = true;
                    System.out.println("#########################Loading complete");
                } catch (IOException | JSONException e) {
                    attemptedLoad = true;
                    shouldAnimate = false;
                    loadingHandler.postDelayed(() -> loadingText.setText(R.string.error1), 2101);
                    clearAnimation();
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(JSONObject result) {
                loadSuccessful = false;
                attemptedLoad = true;
                shouldAnimate = false;
                loadingHandler.postDelayed(() -> loadingText.setText(R.string.no_internet), 2101);
                clearAnimation();
            }
        });
    }

    private void animateHeadphones(){
        final Animation leftAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_left);
        final Animation rightAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_right);
        final Animation centerAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_center);
        getActivity().runOnUiThread(() -> {
            leftTriangle.startAnimation(leftAnimation);
            rightTriangle.startAnimation(rightAnimation);
            loadingHandler.postDelayed(() -> headphoneMiddle.startAnimation(centerAnimation), 1000);
        });
    }

    private void animateLoadingText(){
        loadingHandler.postDelayed(() -> loadingText.setText(R.string.loading1),500 );
        loadingHandler.postDelayed(() -> loadingText.setText(R.string.loading2),1000 );
        loadingHandler.postDelayed(() -> loadingText.setText(R.string.loading3),1500 );
        loadingHandler.postDelayed(() -> loadingText.setText(R.string.loading),2000 );
        try {
            Thread.sleep(2100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(attemptedLoad && loadSuccessful) {
            shouldAnimate = false;
            getActivity().runOnUiThread(this::clearAnimation);
            listener.loadingComplete(allStations);
        }
    }

    private void clearAnimation(){
        getActivity().runOnUiThread(() -> {
            rightTriangle.clearAnimation();
            leftTriangle.clearAnimation();
            headphoneMiddle.clearAnimation();
        });
    }

    public interface LoadingListener {
        void loadingComplete(ArrayList<Station> list);
    }

    public void setListener(LoadingListener listener) {
        this.listener = listener;
    }
}
