package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.SingleFragmentActivity;
import statifyi.com.statifyi.utils.GAUtils;
import statifyi.com.statifyi.utils.ShowcaseUtils;
import statifyi.com.statifyi.widget.RadioButton;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String[] TAB_NAMES = {"STATUS", "CONTACTS", "CALL LOG", "DIAL"};
    @InjectView(R.id.home_pager)
    ViewPager mPager;
    @InjectView(R.id.tab_layout)
    TabLayout tabLayout;
    @InjectView(R.id.home_radio_group)
    RadioGroup radioGroup;
    @InjectView(R.id.home_radio_status)
    RadioButton statusTabBtn;
    @InjectView(R.id.home_radio_contacts)
    RadioButton contactsTabBtn;
    @InjectView(R.id.home_radio_calllog)
    RadioButton calllogTabBtn;
    @InjectView(R.id.home_radio_dialpad)
    RadioButton dialPadBtn;
    private ShowcaseView showcaseView;
    private int counter;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        GAUtils.sendScreenView(getActivity().getApplicationContext(), HomeFragment.class.getName());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!ShowcaseUtils.getHomePage(getActivity())) {
            showcaseView = new ShowcaseView.Builder(getActivity())
                    .setTarget(new ViewTarget(statusTabBtn))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setOnClickListener(this)
                    .build();
            showcaseView.setContentTitle("Status");
            showcaseView.setContentText("View and manage your status");
            showcaseView.setButtonText(getString(R.string.next));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.inject(this, root);

        ScreenSlidePagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mPagerAdapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.home_radio_status) {
                    mPager.setCurrentItem(0);
                    getActivity().setTitle("STATUS");
                } else if (checkedId == R.id.home_radio_contacts) {
                    mPager.setCurrentItem(1);
                    getActivity().setTitle("CONTACTS");
                } else if (checkedId == R.id.home_radio_calllog) {
                    mPager.setCurrentItem(2);
                    getActivity().setTitle("CALL LOG");
                }
            }
        });

        dialPadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SingleFragmentActivity.class);
                intent.putExtra(SingleFragmentActivity.KEY_SINGLE_FRAGMENT, SingleFragmentActivity.FragmentName.DIALER);
                intent.putExtra("title", "DIAL");
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onClick(View view) {
        switch (counter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(contactsTabBtn), true);
                showcaseView.setContentTitle("Contacts");
                showcaseView.setContentText("View the current status of your saved contacts");
                break;

            case 1:
                showcaseView.setShowcase(new ViewTarget(calllogTabBtn), true);
                showcaseView.setContentTitle("Call Log");
                showcaseView.setContentText("View your call logs & custom call messages");
                break;

            case 2:
                showcaseView.setShowcase(new ViewTarget(dialPadBtn), true);
                showcaseView.setContentTitle("Dialer");
                showcaseView.setContentText("Dial your number and make custom calls");
                break;

            case 3:
                showcaseView.hide();
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(StatusFragment.BROADCAST_ACTION_SHOWCASEVIEW));
                ShowcaseUtils.setHomePage(getActivity(), true);
                break;
        }
        counter++;
    }

    class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return StatusFragment.newInstance(null, null);
            else if (position == 1)
                return ContactsFragment.newInstance(null, null);
            else if (position == 2)
                return CallLogFragment.newInstance(null, null);
            else
                return DialerFragment.newInstance(null, null);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_NAMES[position];
        }
    }

}