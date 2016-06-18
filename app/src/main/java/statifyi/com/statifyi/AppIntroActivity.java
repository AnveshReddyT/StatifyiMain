package statifyi.com.statifyi;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);

        int accentColor = getResources().getColor(R.color.accentColor);
        addSlide(AppIntroFragment.newInstance("StatiFYI", "Welcome to Statifyi", R.drawable.ic_welcome_statifyi, accentColor));
        addSlide(AppIntroFragment.newInstance("Status", "Manage your status and aware your well wishers about it", R.drawable.ic_manage_status, accentColor));
        addSlide(AppIntroFragment.newInstance("Contacts", "Know the current status of your saved contacts", R.drawable.ic_status_contacts, accentColor));
        addSlide(AppIntroFragment.newInstance("Custom Call", "Make calls with a text messages to convey the message", R.drawable.ic_call_with_message, accentColor));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(accentColor);
        setSeparatorColor(Color.BLACK);

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent i = new Intent(AppIntroActivity.this, RegistrationActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent i = new Intent(AppIntroActivity.this, RegistrationActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}
