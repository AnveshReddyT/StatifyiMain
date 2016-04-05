package statifyi.com.statifyi.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import statifyi.com.statifyi.R;

/**
 * Created by KT on 03/02/16.
 */
public class PrivacySettingsFragment extends PreferenceFragment {

    public PrivacySettingsFragment() {
        // Required empty public constructor
    }

    public static PrivacySettingsFragment newInstance(String param1, String param2) {
        return new PrivacySettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_privacy);
    }
}
