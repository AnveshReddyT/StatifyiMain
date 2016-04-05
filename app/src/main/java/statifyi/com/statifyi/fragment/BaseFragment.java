package statifyi.com.statifyi.fragment;

import android.app.Fragment;
import android.os.Bundle;

import rx.Subscription;

/**
 * Created by KT on 26/12/15.
 */
public abstract class BaseFragment extends Fragment {

    protected Subscription subscription;

    public abstract void showProgress();

    public abstract Subscription subscribeContent();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribeContent();
    }

    @Override
    public void onStop() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
        }
        super.onDestroy();
    }
}
