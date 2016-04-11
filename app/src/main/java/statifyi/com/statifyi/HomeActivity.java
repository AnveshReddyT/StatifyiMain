package statifyi.com.statifyi;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.fragment.HomeFragment;
import statifyi.com.statifyi.service.FloatingService;
import statifyi.com.statifyi.service.GCMRegisterIntentService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.PermissionUtils;
import statifyi.com.statifyi.utils.Utils;

public class HomeActivity extends AppCompatActivity {

    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.navigation_view)
    NavigationView navigationView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private DataUtils dataUtils;

    private ActionBarDrawerToggle drawerToggle;

    private HomeFragment homeFragment;

    public static HomeActivity get(Context context) {
        try {
            return (HomeActivity) context;
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("Given context is not derived from BaseActivity.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        registerGCM();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        ButterKnife.inject(this);
        dataUtils = new DataUtils(PreferenceManager.getDefaultSharedPreferences(this));

        if (toolbar != null) {
            toolbar.setTitle(null);
            setSupportActionBar(toolbar);
        }
        initDrawer();
        setContent(HomeFragment.newInstance(null, null));
        Intent serviceIntent = new Intent(this, FloatingService.class);
        startService(serviceIntent);

        if (Build.VERSION.SDK_INT > 23) {
            PermissionUtils.getPermissionToReadCallLog(this);
            PermissionUtils.getPermissionToReadUserContacts(this);
            PermissionUtils.getPermissionToProcessOutgoingCalls(this);
            PermissionUtils.getPermissionToSystemAlertWindow(this);
        }
    }

    private void setContent(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.home_container, fragment);
        fragmentTransaction.commit();
    }

    private void setContent(SingleFragmentActivity.FragmentName fragmentName, String title) {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.KEY_SINGLE_FRAGMENT, fragmentName);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    private void initDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(false);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        setTitle(getString(R.string.app_name));
                        return true;
                    case R.id.nav_auto_status:
                        setContent(SingleFragmentActivity.FragmentName.AUTO_STATUS, getString(R.string.menu_auto_status));
                        return true;
                    case R.id.nav_timely_status:
                        setContent(SingleFragmentActivity.FragmentName.TIMELY_STATUS, getString(R.string.menu_timely_status));
                        return true;
                    case R.id.nav_privacy:
                        setContent(SingleFragmentActivity.FragmentName.PRIVACY, getString(R.string.menu_privacy));
                        return true;
                    case R.id.nav_invite:
                        Utils.inviteFriends(HomeActivity.this);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public void registerGCM() {
        String regId = GCMUtils.getRegistrationId(this);
        if (TextUtils.isEmpty(regId)) {
            Intent intent = new Intent(this, GCMRegisterIntentService.class);
            startService(intent);
        } else {
//            Toast.makeText(getApplicationContext(), "RegId already available. RegId: " + regId, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (!PermissionUtils.onPermissionResult(requestCode, permissions, grantResults, this)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
}
