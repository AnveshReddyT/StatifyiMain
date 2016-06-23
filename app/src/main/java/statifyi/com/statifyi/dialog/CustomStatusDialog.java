package statifyi.com.statifyi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.adapter.StatusIconAdapter;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.EditText;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 19/02/16.
 */
public class CustomStatusDialog extends Dialog {

    public static final String customStatusIcons[] = {
            "allday", "board", "chilling", "classroom", "dontknow", "dropmessage", "formaldress",
            "friends", "music", "out", "pc", "smiley", "star", "tools", "travel"
    };

    @InjectView(R.id.custom_status_btn)
    TextView setBtn;

    @InjectView(R.id.custom_status_message)
    EditText statusMessage;

    @InjectView(R.id.custom_status_icon)
    AppCompatSpinner statusIcon;

    private UserAPIService userAPIService;

    private String message;

    private String icon;

    private Context mContext;

    public CustomStatusDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomStatusDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected CustomStatusDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_status);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.inject(this);
        if (userAPIService == null) {
            userAPIService = NetworkUtils.provideUserAPIService(mContext);
        }

        statusIcon.setAdapter(new StatusIconAdapter(mContext, R.layout.custom_status_icon_item, customStatusIcons));
        statusIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        int height = Utils.getScreenHeight(mContext);
        int width = Utils.getScreenWidth(mContext);
        lp.width = (int) (width * 0.85);
        lp.height = (int) (height * 0.85);
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

    @OnClick(R.id.custom_status_btn)
    public void setCustomStatus(View view) {
        if (TextUtils.isEmpty(statusMessage.getText())) {
            statusMessage.setError("Cannot be blank!");
        } else {
            setMessage(statusMessage.getText().toString());
            setIcon(customStatusIcons[statusIcon.getSelectedItemPosition()]);
            dismiss();
        }
    }
}
