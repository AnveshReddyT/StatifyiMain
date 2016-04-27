package statifyi.com.statifyi;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.adapter.HomePagerAdapter;

public class DashboardActivity extends AppCompatActivity {

    private static final String[] TAB_NAMES = {"STATUS", "CONTACTS", "CALL LOG"};

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.fab)
    FloatingActionButton fab;

    @InjectView(R.id.tabs)
    TabLayout tabs;

    @InjectView(R.id.pager)
    ViewPager pager;

    private HomePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        setUpTabs();
    }

    void setUpTabs() {
        mPagerAdapter = new HomePagerAdapter(getFragmentManager(), TAB_NAMES);
        pager.setAdapter(mPagerAdapter);
        tabs.setupWithViewPager(pager);
    }
}
