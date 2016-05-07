package statifyi.com.statifyi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 19/02/16.
 */
public class InfoDialog extends Dialog {

    @InjectView(R.id.info_title)
    TextView infoTitle;

    @InjectView(R.id.info_message)
    TextView infoMessage;

    private Context mContext;

    public InfoDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public InfoDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected InfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_info);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.inject(this);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        int height = Utils.getScreenHeight(mContext);
        int width = Utils.getScreenWidth(mContext);
        lp.width = (int) (width * 0.85);
        lp.height = (int) (height * 0.6);
        getWindow().setAttributes(lp);

    }

    public void setMessage(String message) {
        this.infoMessage.setText(message);
    }

    public void setInfoTitle(String title) {
        this.infoTitle.setText(title);
    }

    @OnClick(R.id.info_close_btn)
    public void close(View view) {
        dismiss();
    }

    @OnClick(R.id.info_invite_btn)
    public void invite(View view) {
        Utils.inviteFriends(mContext);
        dismiss();
    }
}
