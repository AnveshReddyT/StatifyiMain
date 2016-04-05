package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Response;
import rx.functions.Action1;
import statifyi.com.statifyi.HomeActivity;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.RegistrationActivity;
import statifyi.com.statifyi.api.model.ActivateUserRequest;
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.Button;
import statifyi.com.statifyi.widget.EditText;

public class OTPFragment extends Fragment {

    @InjectView(R.id.register_otp_text)
    EditText otpText;
    @InjectView(R.id.register_otp_btn)
    Button otpVerifyBtn;
    private DataUtils dataUtils;
    private UserAPIService userAPIService;
    private ProgressDialog progressDialog;

    public OTPFragment() {
        // Required empty public constructor
    }

    public static OTPFragment newInstance(String param1, String param2) {
        return new OTPFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataUtils = new DataUtils(PreferenceManager.getDefaultSharedPreferences(getActivity()));
        userAPIService = NetworkUtils.provideUserAPIService(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_otp, container, false);
        ButterKnife.inject(this, root);
        return root;
    }

    @OnClick(R.id.register_otp_btn)
    public void onClick(View v) {
        ActivateUserRequest request = new ActivateUserRequest();
        request.setCode(otpText.getText().toString());
        request.setMobile(dataUtils.getMobileNumber());

        progressDialog.show();
        userAPIService.activateUser(request).subscribe(new Action1<Response<StatusResponse>>() {
            @Override
            public void call(Response<StatusResponse> response) {
                if (response.code() == 200) {
                    StatusResponse s = response.body();
                    dataUtils.setActive(true);
                    dataUtils.saveStatus(s.getStatus());
                    dataUtils.saveIcon(Utils.getDrawableResByName(getActivity(), s.getIcon()));
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                    getActivity().finish();
                } else {
                    Utils.showToast(getActivity(), "Failed to activate");
                }
                progressDialog.dismiss();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.register_otp_change_number)
    public void onClickChangeNumber(View v) {
        ((RegistrationActivity) getActivity()).replaceFragment(RegisterMobileFragment.newInstance(null, null));
    }

}
