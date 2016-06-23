package statifyi.com.statifyi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.EditText;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 19/02/16.
 */
public class CustomCallDialog extends Dialog {

    @InjectView(R.id.custom_call_btn)
    TextView setBtn;

    @InjectView(R.id.custom_call_message)
    EditText statusMessage;

    private String message;

    private String icon;

    private Context mContext;

    public CustomCallDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomCallDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected CustomCallDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_call);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.inject(this);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        int height = Utils.getScreenHeight(mContext);
        int width = Utils.getScreenWidth(mContext);
        lp.width = (int) (width * 0.85);
        lp.height = (int) (height * 0.6);
        getWindow().setAttributes(lp);
        statusMessage.setFilters(new InputFilter[] {new InputFilter.LengthFilter(20)});
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @OnClick(R.id.custom_call_btn)
    public void setCustomStatus(View view) {
        if (TextUtils.isEmpty(statusMessage.getText())) {
            statusMessage.setError("Cannot be blank!");
        } else {
            setMessage(statusMessage.getText().toString());
            dismiss();
        }
    }
}
