package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.adapter.StatusAdapter;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.dialog.CustomStatusDialog;
import statifyi.com.statifyi.dialog.ProgressDialog;
import statifyi.com.statifyi.model.Status;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.TextView;


public class StatusFragment extends Fragment implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String BROADCAST_ACTION_STATUS_UPDATE = "statifyi.broadcast.status_update";

    @InjectView(R.id.status_add_text_layout)
    RelativeLayout addStatusLayout;

    @InjectView(R.id.status_current_text_layout)
    LinearLayout currentStatusLayout;

    @InjectView(R.id.status_auto_text_layout)
    RelativeLayout autoStatusLayout;

    @InjectView(R.id.status_add_text)
    TextView addStatusText;

    @InjectView(R.id.status_current_text)
    TextView currentStatusText;

    @InjectView(R.id.status_auto_text)
    TextView autoStatusText;

    @InjectView(R.id.status_current_icon)
    ImageView currentStatusIcon;

    @InjectView(R.id.status_auto_text_checkbox)
    CheckBox autoStatusCheckbox;

    @InjectView(R.id.status_grid)
    GridView statusGrid;

    private UserAPIService userAPIService;

    private ProgressDialog progressDialog;

    private DBHelper dbHelper;

    private StatusAdapter statusAdapter;

    private boolean isLoaded;

    private BroadcastReceiver onStatusChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (getUserVisibleHint()) {
                updateStatus();
            }
        }
    };

    public StatusFragment() {
        // Required empty public constructor
    }

    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAPIService = NetworkUtils.provideUserAPIService(getActivity());
        dbHelper = DBHelper.getInstance(getActivity());
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_status, container, false);
        ButterKnife.inject(this, root);

        statusAdapter = new StatusAdapter(getActivity());
        statusGrid.setAdapter(statusAdapter);
        statusGrid.setOnItemClickListener(this);

        int width = Utils.getScreenWidth(getActivity()) - 32;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) addStatusLayout.getLayoutParams();
        layoutParams.width = width * 2 / 9;
        layoutParams.height = width * 2 / 9;
        addStatusLayout.setLayoutParams(layoutParams);

        layoutParams = (RelativeLayout.LayoutParams) currentStatusLayout.getLayoutParams();
        layoutParams.width = width * 6 / 18;
        layoutParams.height = width * 6 / 18;
        layoutParams.leftMargin = width / 18 + width / 36;
        currentStatusLayout.setLayoutParams(layoutParams);

        layoutParams = (RelativeLayout.LayoutParams) autoStatusLayout.getLayoutParams();
        layoutParams.width = width * 2 / 9;
        layoutParams.height = width * 2 / 9;
        layoutParams.leftMargin = width / 18 + width / 36;
        autoStatusLayout.setLayoutParams(layoutParams);
        addStatusLayout.setOnClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean autoStatus = preferences.getBoolean(getString(R.string.key_auto_status), false);
        autoStatusText.setText(autoStatus ? "ON" : "OFF");
        autoStatusCheckbox.setChecked(autoStatus);
        autoStatusCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoStatusText.setText(isChecked ? "ON" : "OFF");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(getString(R.string.key_auto_status), isChecked);
                editor.apply();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLoaded) {
            updateStatus();
            isLoaded = true;
        }
        IntentFilter filter = new IntentFilter(BROADCAST_ACTION_STATUS_UPDATE);
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
            updateStatus();
        }
    }

    private void updateStatus() {
        String autoStatus = DataUtils.getAutoStatus(getActivity().getApplicationContext());
        String status = autoStatus == null ? DataUtils.getStatus(getActivity()) : autoStatus;
        currentStatusText.setText(status == null || status.isEmpty() ? "status not set" : status);
        currentStatusIcon.setImageResource(status == null || status.isEmpty() ? R.drawable.ic_launcher : autoStatus == null ? DataUtils.getStatusIcon(getActivity()) : DataUtils.getAutoStatusIcon(getActivity()));
        Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);
        currentStatusIcon.startAnimation(scaleAnim);
        statusAdapter.loadData();
        statusAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_contacts, menu);

        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        String[] statusMessages = getResources().getStringArray(R.array.default_status_list);
        ArrayList<String> itemArrayList = new ArrayList<String>();
        Collections.addAll(itemArrayList, statusMessages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, itemArrayList);
        searchAutoComplete.setAdapter(adapter);
        searchAutoComplete.setDropDownHeight(Utils.getScreenHeight(getActivity()) * 4 / 10);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String statusString = (String) parent.getItemAtPosition(position);
                Status status = new Status();
                status.setStatus(statusString);
                status.setIcon(statusString);
                executeUpdateStatus(status);
                searchAutoComplete.setText(null);
                searchMenuItem.collapseActionView();
                searchView.clearFocus();
            }
        });

        int searchImgId = android.support.v7.appcompat.R.id.search_button; // I used the explicit layout ID of searchview's ImageView
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.getDrawable().setColorFilter(getResources().getColor(R.color.accentColor), PorterDuff.Mode.SRC_ATOP);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(StatusFragment.this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        BaseAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Status status = (Status) parent.getItemAtPosition(position);
        executeUpdateStatus(status);
    }

    private void executeUpdateStatus(final Status status) {
        String mStatus = DataUtils.getStatus(getActivity());
        if (mStatus.equals(status.getStatus())) {
            Utils.showToast(getActivity(), "Status already set!");
        } else {
            StatusRequest request = new StatusRequest();
            request.setMobile(DataUtils.getMobileNumber(getActivity()));
            request.setStatus(status.getStatus());
            request.setIcon(status.getIcon());
            if (NetworkUtils.isOnline()) {
                progressDialog.show();
                userAPIService.setUserStatus(request).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            DataUtils.saveStatus(getActivity(), status.getStatus());
                            int ico = Utils.getDrawableResByName(getActivity(), status.getIcon());
                            DataUtils.saveIcon(getActivity(), ico);

                            updateStatus();
                        } else {
                            Utils.showToast(getActivity(), "Failed to change status");
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        progressDialog.dismiss();
                        Utils.showToast(getActivity(), "Failed to change status");
                    }
                });
            } else {
                Utils.showToast(getActivity(), "No Internet!");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.status_add_text_layout:
                final CustomStatusDialog statusDialog = new CustomStatusDialog(getActivity());
                statusDialog.show();
                statusDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!TextUtils.isEmpty(statusDialog.getMessage())) {
//                            updateCustomStatus(statusDialog.getMessage(), statusDialog.getIcon());
                            Status status = new Status();
                            status.setStatus(statusDialog.getMessage());
                            status.setIcon(statusDialog.getIcon());
                            status.setDate(System.currentTimeMillis());
                            dbHelper.insertOrUpdateCustomStatus(status);
                            updateStatus();
                        }
                    }
                });
                break;
        }
    }

    private void updateCustomStatus(final String status, final String icon) {
        StatusRequest request = new StatusRequest();
        request.setMobile(DataUtils.getMobileNumber(getActivity()));
        request.setStatus(status);
        request.setIcon(icon);
        if (NetworkUtils.isOnline()) {
            progressDialog.show();
            userAPIService.setUserStatus(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        DataUtils.saveStatus(getActivity(), status);
                        int ico = Utils.getDrawableResByName(getActivity(), icon);
                        DataUtils.saveIcon(getActivity(), ico == 0 ? R.drawable.ic_launcher : ico);

                        updateStatus();
                    } else {
                        Utils.showToast(getActivity(), "Failed to change status");
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    progressDialog.dismiss();
                    Utils.showToast(getActivity(), "Failed to change status");
                }
            });
        } else {
            Utils.showToast(getActivity(), "No Internet!");
        }
    }
}
