package statifyi.com.statifyi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.Utils;


/**
 * Created by KT on 29-09-2015.
 */
public class RadioButton extends android.widget.RadioButton {

    private static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public RadioButton(Context context) {
        super(context);
        init(null);
    }

    public RadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyFontView);
            String fontName = a.getString(R.styleable.MyFontView_fontName);
            if (fontName == null) {
                fontName = "Oswald";
            }
            int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", 0);
            setTypeface(Utils.selectTypeface(getContext(), fontName, textStyle));
            a.recycle();
        }
    }

}
