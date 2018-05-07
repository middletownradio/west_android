package middletownmusic.org.midwestradio.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import middletownmusic.org.midwestradio.R;

public class AboutFragment extends DialogFragment {

    TextView purpleCalves;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about_fragment, container);
        layout.setMinimumWidth((int)(displayRectangle.width() * 0.8f));

        return layout;
    }

    @Override
    public void onStart(){
        super.onStart();
        purpleCalves =  getView().findViewById(R.id.purple_calves);
        purpleCalves.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.purplecalves.com"));
            startActivity(browserIntent);
        });
    }
}