package statifyi.com.statifyi.fragment;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.adapter.CallLogAdapter;
import statifyi.com.statifyi.model.CallLog;
import statifyi.com.statifyi.utils.Utils;


public class CallLogFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    @InjectView(R.id.calllogList)
    ListView calllogListview;

    @InjectView(R.id.calllog_list_progress)
    ProgressBar pBar;

    private List<statifyi.com.statifyi.model.CallLog> callLogs;
    private CallLogAdapter callLogAdapter;

    public CallLogFragment() {
    }

    public static CallLogFragment newInstance(String param1, String param2) {
        CallLogFragment fragment = new CallLogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calllog, container, false);
        ButterKnife.inject(this, root);

        callLogs = new ArrayList<>();
        callLogAdapter = new CallLogAdapter(getActivity(), callLogs);
        calllogListview.setAdapter(callLogAdapter);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_contacts, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

//        int searchImgId = android.support.v7.appcompat.R.id.search_button;
//        ImageView v = (ImageView) searchView.findViewById(searchImgId);
//        v.setImageResource(R.drawable.search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(CallLogFragment.this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        callLogAdapter.getFilter().filter(newText);
        return true;
    }

    private void showContent() {
        callLogAdapter.setData(callLogs);
        callLogAdapter.notifyDataSetChanged();
        pBar.setVisibility(View.GONE);
        calllogListview.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgress() {
        pBar.setVisibility(View.VISIBLE);
        calllogListview.setVisibility(View.GONE);
    }

    private Subscriber<List<CallLog>> contentObserver() {
        return new Subscriber<List<CallLog>>() {

            @Override
            public void onCompleted() {
                showContent();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(List<CallLog> logs) {
                callLogs = logs;
            }
        };
    }

    @Override
    public Subscription subscribeContent() {
        return Observable.create(new Observable.OnSubscribe<List<CallLog>>() {
            @Override
            public void call(Subscriber<? super List<CallLog>> subscriber) {
                subscriber.onNext(Utils.getCallLogs(getActivity()));
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contentObserver());
    }
}
