package statifyi.com.statifyi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import statifyi.com.statifyi.utils.DataUtils;

public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (DataUtils.getMobileNumber(SplashScreen.this) != null) {
                    if (DataUtils.isActivated(SplashScreen.this)) {
                        Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                        startActivity(i);
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
