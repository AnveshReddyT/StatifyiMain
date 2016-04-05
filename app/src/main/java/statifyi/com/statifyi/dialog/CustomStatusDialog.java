package statifyi.com.statifyi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.EditText;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 19/02/16.
 */
public class CustomStatusDialog extends Dialog {

    @InjectView(R.id.custom_status_btn)
    TextView setBtn;

    @InjectView(R.id.custom_status_message)
    EditText statusMessage;

    @InjectView(R.id.custom_status_icon)
    ImageView statusIcon;

    private DataUtils dataUtils;

    private UserAPIService userAPIService;

    private String message;

    private String icon;

    private Context mContext;

    private String[] statusMessages;

    private int statusIconIndex;

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
        setContentView(R.layout.custom_status_dialog);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.inject(this);
        if (dataUtils == null || userAPIService == null) {
            dataUtils = new DataUtils(PreferenceManager.getDefaultSharedPreferences(mContext));
            userAPIService = NetworkUtils.provideUserAPIService(mContext);
        }

        statusMessages = mContext.getResources().getStringArray(R.array.default_status_list);
        statusIcon.setImageResource(Utils.getDrawableResByName(mContext, statusMessages[statusIconIndex]));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        int height = Utils.getScreenHeight(mContext);
        int width = Utils.getScreenWidth(mContext);
        lp.width = (int) (width * 0.85);
        lp.height = (int) (height * 0.85);
        getWindow().setAttributes(lp);

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
            setIcon(statusMessages[statusIconIndex]);
            dismiss();
        }
    }

    @OnClick(R.id.custom_status_icon_prev)
    public void setStatusIconPrev(View view) {
        if (statusIconIndex == 0) {
            statusIconIndex = statusMessages.length;
        }
        statusIconIndex = statusIconIndex - 1;
        statusIcon.setImageResource(Utils.getDrawableResByName(mContext, statusMessages[statusIconIndex]));
    }

    @OnClick(R.id.custom_status_icon_next)
    public void setStatusIconNext(View view) {
        statusIconIndex = (statusIconIndex + 1) % statusMessages.length;
        statusIcon.setImageResource(Utils.getDrawableResByName(mContext, statusMessages[statusIconIndex]));
    }
}
