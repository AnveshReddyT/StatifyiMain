package statifyi.com.statifyi;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.fragment.ProfileFragment;
import statifyi.com.statifyi.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_profile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleColor(R.color.white);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.this.finish();
            }
        });
        toolbar.setBackgroundResource(android.R.color.transparent);
        replaceFragment(ProfileFragment.newInstance(null, null));
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }
}
