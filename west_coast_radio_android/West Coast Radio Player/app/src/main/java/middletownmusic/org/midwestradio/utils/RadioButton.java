package middletownmusic.org.midwestradio.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class RadioButton extends android.support.v7.widget.AppCompatButton {

    public RadioButton(Context context) {
        super(context);
    }

    public RadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(FontCache.getFont(getContext(),"fonts/verdana.ttf"));
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
