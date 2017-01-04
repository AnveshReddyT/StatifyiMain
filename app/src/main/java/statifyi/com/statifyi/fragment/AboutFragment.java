package statifyi.com.statifyi.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.GAUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends PreferenceFragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance(String param1, String param2) {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);
        GAUtils.sendScreenView(getActivity().getApplicationContext(), AboutFragment.class.getSimpleName());
    }
}
