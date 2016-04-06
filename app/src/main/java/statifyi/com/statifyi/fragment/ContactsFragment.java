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
import statifyi.com.statifyi.adapter.ContactsAdapter;
import statifyi.com.statifyi.model.Contact;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.IndexableListView;


public class ContactsFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    @InjectView(R.id.contactsList)
    IndexableListView contactsListview;

    @InjectView(R.id.contacts_list_progress)
    ProgressBar pBar;

    private List<Contact> contacts;
    private ContactsAdapter contactsAdapter;

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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contactsAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            showContent();
        }
    }

    private Subscriber<List<Contact>> contentObserver() {

        return new Subscriber<List<Contact>>() {
            @Override
            public void onCompleted() {
                showContent();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(List<Contact> list) {
                contacts = list;
            }
        };
    }

    @Override
    public Subscription subscribeContent() {
        return Observable.create(new Observable.OnSubscribe<List<Contact>>() {
            @Override
            public void call(Subscriber<? super List<Contact>> subscriber) {
                subscriber.onNext(Utils.readPhoneContacts(getActivity()));
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contentObserver());
    }

}
