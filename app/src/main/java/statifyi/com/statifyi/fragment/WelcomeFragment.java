package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.RegistrationActivity;

public class WelcomeFragment extends Fragment {

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance(String param1, String param2) {
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.inject(this, root);

        return root;
    }

    @OnClick(R.id.welcome_btn)
    public void onClick(View v) {
        ((RegistrationActivity) getActivity()).replaceFragment(RegisterMobileFragment.newInstance(null, null));
    }

}
