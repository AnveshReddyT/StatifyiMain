package statifyi.com.statifyi;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.fragment.AutoStatusSettingsFragment;
import statifyi.com.statifyi.fragment.DialerFragment;
import statifyi.com.statifyi.fragment.PrivacySettingsFragment;
import statifyi.com.statifyi.fragment.TimelyStatusSettingsFragment;
import statifyi.com.statifyi.widget.Toolbar;

public class SingleFragmentActivity extends AppCompatActivity {

    public static final String KEY_SINGLE_FRAGMENT = "fragment";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        ButterKnife.inject(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(KEY_SINGLE_FRAGMENT)) {
            Fragment fragment = getFragmentByName((FragmentName) extras.get(KEY_SINGLE_FRAGMENT));
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.singlefragment_container, fragment);
            fragmentTransaction.commit();
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(extras.getString("title"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingleFragmentActivity.this.finish();
                }
            });
        } else {
            finish();
        }
    }

    public Fragment getFragmentByName(FragmentName name) {
        switch (name) {
            case DIALER:
                return DialerFragment.newInstance(getIntent().getStringExtra(DialerFragment.PARAM_MOBILE_NUM), null);
            case AUTO_STATUS:
                return AutoStatusSettingsFragment.newInstance(null, null);
            case PRIVACY:
                return PrivacySettingsFragment.newInstance(null, null);
            case TIMELY_STATUS:
                return TimelyStatusSettingsFragment.newInstance(null, null);
            default:
                return null;
        }
    }

    public enum FragmentName {
        DIALER,
        AUTO_STATUS,
        PRIVACY,
        TIMELY_STATUS
    }
}
