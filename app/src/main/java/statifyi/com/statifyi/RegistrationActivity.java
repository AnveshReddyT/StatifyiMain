package statifyi.com.statifyi;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import statifyi.com.statifyi.fragment.OTPFragment;
import statifyi.com.statifyi.fragment.WelcomeFragment;

public class RegistrationActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        fragmentManager = getFragmentManager();
        if (getIntent().hasExtra("active")) {
            replaceFragment(OTPFragment.newInstance(null, null));
        } else {
            replaceFragment(WelcomeFragment.newInstance(null, null));
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.register_container, fragment);
        fragmentTransaction.commit();
    }
}
