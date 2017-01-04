package statifyi.com.statifyi.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.GAUtils;

/**
 * Created by KT on 03/02/16.
 */
public class AutoStatusSettingsFragment extends PreferenceFragment {

    public AutoStatusSettingsFragment() {
        // Required empty public constructor
    }

    public static AutoStatusSettingsFragment newInstance(String param1, String param2) {
        return new AutoStatusSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_auto_status);
        GAUtils.sendScreenView(getActivity().getApplicationContext(), AutoStatusSettingsFragment.class.getSimpleName());
    }
}
