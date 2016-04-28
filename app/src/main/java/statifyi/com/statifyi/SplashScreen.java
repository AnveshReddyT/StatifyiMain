package statifyi.com.statifyi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import statifyi.com.statifyi.utils.DataUtils;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int SPLASH_TIME_OUT = 0;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("launched", false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("launched", true);
            editor.apply();
            SPLASH_TIME_OUT = 2000;
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!TextUtils.isEmpty(DataUtils.getMobileNumber(SplashScreen.this))) {
                    if (DataUtils.isActivated(SplashScreen.this)) {
                        if (!TextUtils.isEmpty(DataUtils.getName(SplashScreen.this))) {
                            Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(SplashScreen.this, RegistrationActivity.class);
                            i.putExtra("complete", false);
                            startActivity(i);
                        }
                    } else {
                        Intent i = new Intent(SplashScreen.this, RegistrationActivity.class);
                        i.putExtra("active", false);
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(SplashScreen.this, RegistrationActivity.class);
                    startActivity(i);
                }

                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
