package statifyi.com.statifyi.fragment;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by KT on 26/12/15.
 */
public abstract class BaseFragment extends Fragment {

    public abstract void showProgress();

    public abstract void loadContent();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadContent();
    }
}
