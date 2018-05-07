package middletownmusic.org.midwestradio.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;



public class RadioTextView extends android.support.v7.widget.AppCompatTextView {

    public RadioTextView(Context context) {
        super(context);
    }

    public RadioTextView(Context context, AttributeSet attrs, int defStyleAttr)   {
        super(context, attrs, defStyleAttr);
    }

    public RadioTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(FontCache.getFont(getContext(),"fonts/verdana.ttf"));
    }

}
