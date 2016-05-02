package statifyi.com.statifyi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.Utils;

public class Button extends android.widget.Button {

    private static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public Button(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Button(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            setAllCaps(true);
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyFontView);
            String fontName = a.getString(R.styleable.MyFontView_fontName);
            String fontStyle = a.getString(R.styleable.MyFontView_fontStyle);
            if (fontName == null) {
                fontName = "Oswald";
            }
            if (fontStyle == null) {
                fontStyle = "Regular";
            }
            setTypeface(Utils.selectTypeface(getContext(), fontName, fontStyle));
            a.recycle();
        }
    }
}
