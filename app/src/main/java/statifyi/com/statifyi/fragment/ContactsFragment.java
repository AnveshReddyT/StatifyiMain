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
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.adapter.ContactsAdapter;
import statifyi.com.statifyi.model.Contact;
import statifyi.com.statifyi.service.GCMIntentService;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.IndexableListView;


public class ContactsFragment extends Fragment implements SearchView.OnQueryTextListener {

    @InjectView(R.id.contactsList)
    IndexableListView contactsListview;

    @InjectView(R.id.contacts_list_progress)
    ProgressBar pBar;

    private List<Contact> contacts;

    private ContactsAdapter contactsAdapter;

    private boolean isLoaded;
    private BroadcastReceiver onStatusChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (getUserVisibleHint()) {
                showContent();
            }
        }
    };

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
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
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.inject(this, root);

        contacts = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(getActivity(), contacts);
        contactsListview.setAdapter(contactsAdapter);
        contactsListview.setFastScrollEnabled(true);
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
        searchView.setOnQueryTextListener(ContactsFragment.this);
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
        contactsAdapter.getFilter().filter(newText);
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

    public void showProgress() {
        pBar.setVisibility(View.VISIBLE);
        contactsListview.setVisibility(View.GONE);
    }

    private void showContent() {
        contactsAdapter.setData(contacts);
        contactsAdapter.notifyDataSetChanged();
        pBar.setVisibility(View.GONE);
        contactsListview.setVisibility(View.VISIBLE);
    }

    public void loadContent() {
        new AsyncTask<Context, Void, List<Contact>>() {

            @Override
            protected void onPreExecute() {
                showProgress();
                super.onPreExecute();
            }

            @Override
            protected List<Contact> doInBackground(Context... params) {
                return Utils.readPhoneContacts(params[0]);
            }

            @Override
            protected void onPostExecute(List<Contact> contactList) {
                contacts = contactList;
                isLoaded = true;
                showContent();
                super.onPostExecute(contactList);
            }
        }.execute(getActivity());
    }

}
