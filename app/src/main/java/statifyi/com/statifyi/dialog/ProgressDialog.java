package statifyi.com.statifyi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 30/04/16.
 */
public class ProgressDialog extends Dialog {

    private TextView messageText;

    private String message;

    public ProgressDialog(Context context) {
        super(context);
    }

    public ProgressDialog(Context context, String message) {
        super(context);
        this.message = message;
    }

    public ProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0.8f);
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        setContentView(R.layout.progress_dialog);
        ImageView progress = (ImageView) findViewById(R.id.dialog_progress_image);
        messageText = (TextView) findViewById(R.id.dialog_progress_text);
        if (message != null) {
            messageText.setText(message);
        }
        final AnimationDrawable myAnimationDrawable = (AnimationDrawable) progress.getDrawable();

        progress.post(new Runnable() {
            @Override
            public void run() {
                myAnimationDrawable.start();
            }
        });
    }

    public void setMessage(String message) {
        this.messageText.setText(message);
    }
}