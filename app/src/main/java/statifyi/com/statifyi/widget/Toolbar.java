package statifyi.com.statifyi.widget;

import android.content.Context;
import android.util.AttributeSet;

import statifyi.com.statifyi.R;

/**
 * Created by KT on 29-09-2015.
 */
public class Toolbar extends android.support.v7.widget.Toolbar {

    public Toolbar(Context context) {
        super(context);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Toolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView titleText = (TextView) findViewById(R.id.toolbar_title);
        titleText.setText(title);
    }

    @Override
    public void setLogo(int resId) {
//        ImageView icon = (ImageView) findViewById(R.id.toolbar_icon);
//        icon.setImageResource(resId);
//        super.setLogo(resId);
    }
}
