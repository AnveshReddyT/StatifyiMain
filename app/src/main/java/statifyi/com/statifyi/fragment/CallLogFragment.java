package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.adapter.CallLogAdapter;
import statifyi.com.statifyi.model.CallLog;
import statifyi.com.statifyi.service.GCMIntentService;
import statifyi.com.statifyi.utils.Utils;


public class CallLogFragment extends Fragment implements SearchView.OnQueryTextListener {

    @InjectView(R.id.calllogList)
    ListView calllogListview;

    @InjectView(R.id.calllog_list_progress)
    ProgressBar pBar;

    private List<statifyi.com.statifyi.model.CallLog> callLogs;

    private CallLogAdapter callLogAdapter;

    private boolean isLoaded;
    private BroadcastReceiver onStatusChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (getUserVisibleHint()) {
                showContent();
            }
        }
    };

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showProgress();
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

    @Override
    public void onResume() {
        super.onResume();
        loadContent();
        IntentFilter filter = new IntentFilter(GCMIntentService.BROADCAST_ACTION_STATUS_CHANGE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onStatusChangeReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onStatusChangeReceiver);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isLoaded) {
            showContent();
        }
    }

    private void showContent() {
        callLogAdapter.setData(callLogs);
        callLogAdapter.notifyDataSetChanged();
        pBar.setVisibility(View.GONE);
        calllogListview.setVisibility(View.VISIBLE);
    }

    public void showProgress() {
        pBar.setVisibility(View.VISIBLE);
        calllogListview.setVisibility(View.GONE);
    }

    public void loadContent() {
        new AsyncTask<Context, Void, List<CallLog>>() {

            @Override
            protected void onPreExecute() {
                showProgress();
                super.onPreExecute();
            }

            @Override
            protected List<CallLog> doInBackground(Context... params) {
                return Utils.getCallLogs(params[0]);
            }

            @Override
            protected void onPostExecute(List<CallLog> logs) {
                callLogs = logs;
                isLoaded = true;
                showContent();
                super.onPostExecute(logs);
            }
        }.execute(getActivity());
    }
}
