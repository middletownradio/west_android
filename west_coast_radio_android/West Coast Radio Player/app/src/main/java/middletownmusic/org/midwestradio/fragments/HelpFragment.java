package middletownmusic.org.midwestradio.fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import middletownmusic.org.midwestradio.R;

public class HelpFragment extends DialogFragment {

    private String page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.help_fragment, container);
        page = getArguments().getString("page");
        TextView helpText =view.findViewById(R.id.help_message);
        switch (page){
            case "radio":
                helpText.setText(R.string.radio_help);
                break;
            case "station":
                helpText.setText(R.string.list_help);
                break;
        }
        return view;
    }
}
