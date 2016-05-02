package statifyi.com.statifyi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Patterns;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.Utils;


public class EditText extends android.widget.EditText {

    public static final String ALPHA_NUMERIC = "[a-zA-Z0-9 \\./-]*";

    public static final String ALPHABETS = "[a-zA-Z \\./-]*";

    public static final String NAME = "[a-zA-Z \\./-]*";

    public static final String EMAIL = Patterns.EMAIL_ADDRESS.pattern();

    public static final String PHONE = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";

    public static final String USERNAME = "^[a-zA-Z0-9_.@-]{6,50}$";

    public static final String PASSWORD = "^[a-z0-9_-]{6,40}$";

    public static final String EMPTY = "";

    private static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    private static final int[] colorAttrs = new int[]{android.R.attr.textColor, android.R.attr.background, android.R.attr.maxLength};
    private Context mContext;

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EditText(Context context) {
        super(context);
        init(null, null);
    }

    private void init(Context mContext, AttributeSet attrs) {
        this.mContext = mContext;
        if (attrs != null) {
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
        processAndroidAttributes(getContext(), attrs);
        setTextSize(14);
        setCompoundDrawablePadding(16);
        setSingleLine();
        setInputType(getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    private void processAndroidAttributes(final Context context, final AttributeSet attrs) {
        final TypedArray colorA = context.obtainStyledAttributes(attrs, colorAttrs);
        setTextColor(colorA.getColor(0, Color.WHITE));
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(colorA.getResourceId(2, Integer.MAX_VALUE))});
        colorA.recycle();
    }

    public boolean isEmpty() {
        Editable editable = getText();
        if (editable == null) {
            return false;
        }
        String text = editable.toString();
        return text.length() == 0;
    }

    public boolean validate(String pattern, String errorMsg) {
        if (isEmpty()) {
//            Utils.showSnackBar(this.getContext(), getTag() == null ? "" : getTag().toString() + " cannot be blank", null, null);
            setError("cannot be blank");
            return false;
        }
        if (pattern == null) {
            return true;
        }
        boolean isMatching = getText().toString().matches(pattern);
        if (!isMatching) {
            if (errorMsg == null) {
                errorMsg = "Invalid " + getTag().toString();
            }
//            Utils.showSnackBar(this.getContext(), errorMsg, null, null);
            setError(errorMsg);
        }
        return isMatching;
    }
}
