package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Response;
import rx.functions.Action1;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.adapter.StatusAdapter;
import statifyi.com.statifyi.api.model.StatusRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.dialog.CustomStatusDialog;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.TextView;


public class StatusFragment extends Fragment implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, View.OnClickListener {

    @InjectView(R.id.status_add_text_layout)
    RelativeLayout addStatusLayout;

    @InjectView(R.id.status_current_text_layout)
    RelativeLayout currentStatusLayout;

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

    private DataUtils dataUtils;

    private ProgressDialog progressDialog;

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
        dataUtils = new DataUtils(PreferenceManager.getDefaultSharedPreferences(getActivity()));
        userAPIService = NetworkUtils.provideUserAPIService(getActivity());
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_status, container, false);
        ButterKnife.inject(this, root);

        statusGrid.setAdapter(new StatusAdapter(getActivity()));
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
        updateStatus();
    }

    private void updateStatus() {
        String status = dataUtils.getAutoStatus() == null ? dataUtils.getStatus() : dataUtils.getAutoStatus();
        currentStatusText.setText(status == null || status.isEmpty() ? "status not set" : status);
        currentStatusIcon.setImageResource(status == null || status.isEmpty() ? R.drawable.ic_launcher : dataUtils.getAutoStatus() == null ? dataUtils.getStatusIcon() : dataUtils.getAutoStatusIcon());
        Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);
        currentStatusIcon.startAnimation(scaleAnim);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_contacts, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        String[] statusMessages = getResources().getStringArray(R.array.default_status_list);
        ArrayList<String> itemArrayList = new ArrayList<String>();
        for (String msg : statusMessages) {
            itemArrayList.add(msg);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, itemArrayList);
        searchAutoComplete.setAdapter(adapter);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String status = parent.getItemAtPosition(position).toString();
                executeUpdateStatus(status);
                searchAutoComplete.setText(null);
                searchMenuItem.collapseActionView();
            }
        });

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(StatusFragment.this);
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
        final String status = parent.getItemAtPosition(position).toString();
        executeUpdateStatus(status);
    }

    private void executeUpdateStatus(final String status) {
        StatusRequest request = new StatusRequest();
        request.setMobile(dataUtils.getMobileNumber());
        request.setStatus(status);
        request.setIcon(status);
        progressDialog.show();
        userAPIService.setUserStatus(request).subscribe(new Action1<Response>() {
            @Override
            public void call(Response s) {
                if (s.code() == 200) {
                    dataUtils.saveStatus(status);
                    int ico = Utils.getDrawableResByName(getActivity(), status);
                    dataUtils.saveIcon(ico);

                    updateStatus();
                } else {
                    Utils.showToast(getActivity(), "Failed to change status");
                }
                progressDialog.dismiss();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                progressDialog.dismiss();
                Utils.showToast(getActivity(), "Failed to change status");
            }
        });
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
                            updateCustomStatus(statusDialog.getMessage(), statusDialog.getIcon());
                        }
                    }
                });
                break;
        }
    }

    private void updateCustomStatus(final String status, final String icon) {
        progressDialog.show();
        StatusRequest request = new StatusRequest();
        request.setMobile(dataUtils.getMobileNumber());
        request.setStatus(status);
        request.setIcon(icon);
        userAPIService.setUserStatus(request).subscribe(new Action1<Response>() {
            @Override
            public void call(Response s) {
                if (s.code() == 200) {
                    dataUtils.saveStatus(status);
                    int ico = Utils.getDrawableResByName(getActivity(), icon);
                    dataUtils.saveIcon(ico == 0 ? R.drawable.ic_launcher : ico);

                    updateStatus();
                } else {
                    Utils.showToast(getActivity(), "Failed to change status");
                }
                progressDialog.dismiss();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                progressDialog.dismiss();
                Utils.showToast(getActivity(), "Failed to change status");
            }
        });
    }
}
