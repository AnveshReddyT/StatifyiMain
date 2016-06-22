package statifyi.com.statifyi.widget;

/**
 * Created by KT on 9/27/15.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.StatusUtils;
import statifyi.com.statifyi.utils.Utils;


public class FloatingPopup extends LinearLayout implements OnTouchListener {
    public static final int X_MARGIN = 64;
    public static final int Y_MARGIN = 100;
    private int screenWidth;
    private int screenHeight;
    private WindowManager windowManager; // to hold our image on screen
    private Context ctx; // context so in case i use it somewhere.
    private GestureDetector gestureDetector; // to detect some listener on the image.
    private WindowManager.LayoutParams params; // layoutParams where i set the image height/width and other.
    private WindowManager.LayoutParams paramsF;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private TextView statusMessage;
    private TextView statusTime;
    private ImageView logoIcon;
    private android.widget.TextView closePopup;
    private ImageView statusIcon;
    private CircularImageView avatar;
    private RelativeLayout statusIconLayout;
    private ImageView statusMenu;
    private String mobile;
    private boolean isShowing;

    /**
     * @param context
     */

    public FloatingPopup(Context context) {
        super(context);
        this.ctx = context;
        this.setOnTouchListener(this); // setting touchListener to the imageView
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE); // ini the windowManager

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                PixelFormat.TRANSPARENT);

        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        params.width = screenWidth - 128;
        params.gravity = Gravity.TOP;
        params.format = PixelFormat.TRANSLUCENT;

        params.gravity = Gravity.TOP | Gravity.LEFT; // setting the gravity of the imageView
        params.windowAnimations = android.R.style.Animation_Toast; // adding an animation to it.
        params.x = X_MARGIN; // horizontal location of imageView
        params.y = screenHeight / 2 - 100; // vertical location of imageView
//        params.height = 120; // given it a fixed height in case of large image
//        params.width = 120; // given it a fixed width in case of large image
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;

        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout ll = (LinearLayout) inflate(context, R.layout.popup, this);
        statusMessage = (TextView) findViewById(R.id.popup_status_message);
        statusIcon = (ImageView) findViewById(R.id.popup_status_icon);
        statusMenu = (ImageView) findViewById(R.id.popup_status_menu);
        statusTime = (TextView) findViewById(R.id.popup_status_time);
        avatar = (CircularImageView) findViewById(R.id.popup_status_avatar);
        logoIcon = (ImageView) findViewById(R.id.popup_status_logo);
        statusIconLayout = (RelativeLayout) findViewById(R.id.popup_status_icon_layout);
        closePopup = (android.widget.TextView) findViewById(R.id.popup_status_close);
        statusMenu.getDrawable().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        closePopup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                destroy();
            }
        });
    }

    /**
     * @param context
     * @param attrs
     */
    public FloatingPopup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public FloatingPopup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void show() {
        if (!isShowing()) {
            this.setAlpha(1f);
            params.x = X_MARGIN;
            windowManager.addView(this, params); // adding the imageView & the  params to the WindowsManger.
            setIsShowing(true);
        }
    }

    public void setMessage(String message) {
        this.statusMessage.setText(message);
    }

    public void setTime(String time) {
        this.statusTime.setText(time);
    }

    public void setStatusIcon(int icon) {
        Drawable d = Utils.changeColor(ctx, icon, R.color.white);
        this.statusIcon.setImageDrawable(d);
    }

    /**
     * @param v
     * @param event
     * @return true/false
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        gestureDetector.onTouchEvent(event);
        paramsF = params; // getting the layout params from the current one and assigning new one.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                paramsF = params;
                initialX = paramsF.x; // Horizontal location of the ImageView
                initialY = paramsF.y; // Vertical location of the ImageView
                initialTouchX = event.getRawX(); //X coordinate  location of the ImageView
                initialTouchY = event.getRawY(); //Y coordinate  location of the ImageView
                break;
            case MotionEvent.ACTION_UP: // this called when we actually leave the Imageview
                this.setAlpha(1f);
                break;
            case MotionEvent.ACTION_MOVE:
                this.setAlpha(0.6f);
                int xMove = initialX + (int) (event.getRawX() - initialTouchX);
                if (xMove < -X_MARGIN) {
                    xMove = -X_MARGIN;
                } else if (xMove > 3 * X_MARGIN) {
                    xMove = 3 * X_MARGIN;
                }
                float diffX = initialTouchX - event.getRawX();
                float diffY = initialTouchY - event.getRawY();
                if (xMove > 0 || xMove < 2 * X_MARGIN) {
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > 100) {
                            destroy();
                        }
                    }
                }
                paramsF.x = xMove;
                int yMove = initialY + (int) (event.getRawY() - initialTouchY);
                if (yMove < Y_MARGIN) {
                    yMove = Y_MARGIN;
                } else if (yMove + Y_MARGIN + this.getHeight() > screenHeight) {
                    yMove = screenHeight - this.getHeight() - Y_MARGIN;
                }
                paramsF.y = yMove;
                windowManager.updateViewLayout(this, paramsF);
                break;
        }
        return false; // returning false otherwise any touch event on the imageView wont work.
    }

    /**
     * return [Remove the FloatingImage from the windowManager]
     */
    public void destroy() {
        if (windowManager != null) { // if the image still exists on the WindowManager release it.
            try {
                if (isShowing) {
                    windowManager.removeView(this); // remove the ImageView
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                resetPopup();
                setIsShowing(false);
            }
        }
    }

    public void resetPopup() {
        setMessage(getResources().getString(R.string.please_wait));
        setStatusLayoutColor(R.color.accentColor);
        setStatusIcon(R.drawable.ic_status);
        avatar.setImageResource(R.drawable.avatar);
        setTime(null);
    }

    public void setStatusLayoutColor(int res) {
        statusIconLayout.setBackgroundResource(res);
    }

    public void setPopupMenu(final boolean newUser) {
        statusMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(findViewById(R.id.popup_status_menu_icon), newUser);
            }
        });
    }

    public void showPopup(View v, boolean newUser) {
        PopupMenu popup = new PopupMenu(ctx.getApplicationContext(), v);
        if (newUser) {
            MenuItem inviteOption = popup.getMenu().add("Invite");
            TextView tv = new TextView(ctx);
            tv.setBackgroundColor(Color.RED);
            tv.setPadding(3, 3, 3, 3);
//            inviteOption.setActionView(tv);
            inviteOption.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Utils.inviteFriends(ctx);
                    return false;
                }
            });
        } else {
            MenuItem notifyOption = popup.getMenu().add("Notify on status change");
            notifyOption.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (mobile != null && !TextUtils.isEmpty(mobile) && mobile.length() == 10) {
                        StatusUtils.addNotifyStatus(ctx, mobile);
                        Toast.makeText(ctx, "Reminder added!", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }
            });
        }
        popup.show();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
        NetworkUtils.providePicasso(ctx).load(NetworkUtils.provideAvatarUrl(Utils.getLastTenDigits(mobile)))
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(avatar);
        loadImageFromNetwork(mobile);
    }

    public void loadImageFromNetwork(String mobile) {
        NetworkUtils.providePicasso(ctx).load(NetworkUtils.provideAvatarUrl(Utils.getLastTenDigits(mobile)))
                .placeholder(avatar.getDrawable())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(avatar);
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setIsShowing(boolean isShowing) {
        this.isShowing = isShowing;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {// When there is a touch event on the imageView
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
//                            onSwipeRight();
                        } else {
//                            onSwipeLeft();
                        }
                        destroy();
                    }
                    result = true;
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
//                        onSwipeBottom();
                    } else {
//                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) { // perform Double tap on the ImageView
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) { // perform single tap on the ImageView
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) { // perform long press on the ImageView
        }
    }
}
