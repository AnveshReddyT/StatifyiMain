package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.RegistrationActivity;
import statifyi.com.statifyi.adapter.CountryCodesAdapter;
import statifyi.com.statifyi.api.model.RegisterUserRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.model.CountryCode;
import statifyi.com.statifyi.model.CountryCodes;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.Button;
import statifyi.com.statifyi.widget.EditText;

public class RegisterMobileFragment extends Fragment {

    @InjectView(R.id.register_mobile_code)
    Spinner countruCodesSpinner;
    @InjectView(R.id.register_mobile_text)
    EditText mobileText;
    @InjectView(R.id.register_mobile_btn)
    Button registerBtn;
    private UserAPIService userAPIService;
    private ProgressDialog progressDialog;

    public RegisterMobileFragment() {
        // Required empty public constructor
    }

    public static RegisterMobileFragment newInstance(String param1, String param2) {
        return new RegisterMobileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAPIService = NetworkUtils.provideUserAPIService(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register_mobile, container, false);
        ButterKnife.inject(this, root);
        CountryCodes countryCodes = new Gson().fromJson(assetJSONFile("country_codes.json"), CountryCodes.class);
        TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String CountryID = manager.getSimCountryIso().toUpperCase();
        countruCodesSpinner.setAdapter(new CountryCodesAdapter(getActivity(), R.layout.country_code_row, countryCodes.getCountries()));
        countruCodesSpinner.setSelection(indexOf(countryCodes, CountryID));
        mobileText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    doRegister();
                    return true;
                }
                return false;
            }
        });
        return root;
    }

    private int indexOf(CountryCodes countryCodes, String code) {
        ArrayList<CountryCode> countries = countryCodes.getCountries();
        for (int i = 0; i < countries.size(); i++) {
            CountryCode countryCode = countries.get(i);
            if (code.equalsIgnoreCase(countryCode.getIso())) {
                return i;
            }
        }
        return 0;
    }

    @OnClick(R.id.register_mobile_btn)
    public void onClick(View v) {
        doRegister();
    }

    private void doRegister() {
        RegisterUserRequest request = new RegisterUserRequest();
        final String mobile = mobileText.getText().toString();
        request.setMobile(mobile);

        progressDialog.show();
        userAPIService.registerUser(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    DataUtils.saveMobile(getActivity(), mobile);
                    DataUtils.setActive(getActivity(), false);
                    registerBtn.setText(R.string.join_statifyi);
                    registerBtn.setEnabled(true);
                    ((RegistrationActivity) getActivity()).replaceFragment(OTPFragment.newInstance(null, null));
                } else {
                    Utils.showToast(getActivity(), "Failed to register");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public String assetJSONFile(String filename) {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
