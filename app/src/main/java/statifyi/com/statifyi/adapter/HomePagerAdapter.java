package statifyi.com.statifyi.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import statifyi.com.statifyi.fragment.CallLogFragment;
import statifyi.com.statifyi.fragment.ContactsFragment;
import statifyi.com.statifyi.fragment.DialerFragment;
import statifyi.com.statifyi.fragment.StatusFragment;

/**
 * Created by KT on 21/04/16.
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {

    private String[] TAB_NAMES;

    public HomePagerAdapter(FragmentManager fm, String[] tabs) {
        super(fm);
        this.TAB_NAMES = tabs;
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
