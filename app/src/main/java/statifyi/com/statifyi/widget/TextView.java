package statifyi.com.statifyi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.Utils;


public class TextView extends android.widget.TextView {

    private static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    private static final int[] colorAttrs = new int[]{android.R.attr.textColor, android.R.attr.background, android.R.attr.textStyle};

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyFontView);
            String fontName = a.getString(R.styleable.MyFontView_fontName);
            String fontStyle = a.getString(R.styleable.MyFontView_fontStyle);
            if (fontName == null) {
                fontName = "RobotoCondensed";
            }
            if (fontStyle == null) {
                fontStyle = "Regular";
            }
            setTypeface(Utils.selectTypeface(getContext(), fontName, fontStyle));
            a.recycle();
        }
    }

    private void processAndroidAttributes(final Context context, final AttributeSet attrs) {
        final TypedArray colorA = context.obtainStyledAttributes(attrs, colorAttrs);
        setTextColor(colorA.getColor(0, Color.WHITE));
        colorA.recycle();
    }
}
