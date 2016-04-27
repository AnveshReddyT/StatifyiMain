package statifyi.com.statifyi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.EditText;

/**
 * Created by KT on 19/02/16.
 */
public class ProfileDialog extends Dialog {

    @InjectView(R.id.profile_avatar)
    CircularImageView avatar;

    @InjectView(R.id.profile_name)
    EditText nameText;

    private String name;

    private Context mContext;

    public ProfileDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProfileDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected ProfileDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.inject(this);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        int height = Utils.getScreenHeight(mContext);
        int width = Utils.getScreenWidth(mContext);
        lp.width = (int) (width * 0.85);
        lp.height = (int) (height * 0.85);
        getWindow().setAttributes(lp);

        String name = DataUtils.getName(mContext);
        if (name != null) {
            nameText.setText(name);
        }
        DataUtils.setUserImage(mContext, avatar);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OnClick(R.id.profile_update_btn)
    public void updateProfile(View view) {
        if (TextUtils.isEmpty(nameText.getText())) {
            nameText.setError("Cannot be blank!");
        } else {
            setName(nameText.getText().toString());
            dismiss();
        }
    }
}
