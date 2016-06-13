package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.RegistrationActivity;
import statifyi.com.statifyi.api.model.ActivateUserRequest;
import statifyi.com.statifyi.api.model.RegisterUserRequest;
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.dialog.ProgressDialog;
import statifyi.com.statifyi.service.GCMRegisterIntentService;
import statifyi.com.statifyi.service.SyncAllStatusService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.Button;
import statifyi.com.statifyi.widget.EditText;

public class OTPFragment extends Fragment {

    @InjectView(R.id.register_otp_text)
    EditText otpText;

    @InjectView(R.id.register_otp_btn)
    Button otpVerifyBtn;

    private UserAPIService userAPIService;

    private ProgressDialog progressDialog;
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(getActivity().getFilesDir(), "user.jpg");
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.close();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                launchHomeScreen();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    launchHomeScreen();
                }
            });
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
    private BroadcastReceiver onGCMRegisterReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtils.isOnline()) {
                userAPIService.getUserStatus(GCMUtils.getRegistrationId(getActivity())).enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Response<StatusResponse> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            StatusResponse s = response.body();
                            DataUtils.setActive(getActivity(), true);
                            DataUtils.saveName(getActivity(), s.getName());
                            DataUtils.saveStatus(getActivity(), s.getStatus());
                            DataUtils.saveIcon(getActivity(), Utils.getDrawableResByName(getActivity(), s.getIcon()));
                            if (!TextUtils.isEmpty(s.getName())) {
                                String mobileNumber = DataUtils.getMobileNumber(getActivity());
                                NetworkUtils.providePicasso(getActivity()).load(NetworkUtils.provideAvatarUrl(mobileNumber)).into(target);
                            } else {
                                progressDialog.dismiss();
                                launchHomeScreen();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        progressDialog.dismiss();
                        t.printStackTrace();
                    }
                });
            } else {
                Utils.showToast(getActivity(), "No Internet!");
            }
        }
    };

    public OTPFragment() {
        // Required empty public constructor
    }

    public static OTPFragment newInstance(String param1, String param2) {
        return new OTPFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAPIService = NetworkUtils.provideUserAPIService(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_otp, container, false);
        ButterKnife.inject(this, root);
        otpText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    doActivateUser();
                    return true;
                }
                return false;
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(GCMRegisterIntentService.BROADCAST_ACTION_GCM_REGISTER);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onGCMRegisterReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onGCMRegisterReceiver);
    }

    @OnClick(R.id.register_otp_btn)
    public void onClick(View v) {
        doActivateUser();
    }

    private void doActivateUser() {
        final ActivateUserRequest request = new ActivateUserRequest();
        request.setCode(otpText.getText().toString());
        final String mobileNumber = DataUtils.getMobileNumber(getActivity());
        request.setMobile(mobileNumber);

        if (NetworkUtils.isOnline()) {
            progressDialog.show();
            userAPIService.activateUser(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        getActivity().startService(new Intent(getActivity(), GCMRegisterIntentService.class));
                        getActivity().startService(new Intent(getActivity(), SyncAllStatusService.class));
                    } else {
                        progressDialog.dismiss();
                        Utils.showToast(getActivity(), "Failed to activate");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progressDialog.dismiss();
                    Utils.showToast(getActivity(), "Failed to activate");
                }
            });
        } else {
            Utils.showToast(getActivity(), "No Internet!");
        }
    }

    private void doResendOtp() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setMobile(DataUtils.getMobileNumber(getActivity()));
        request.setCountryCode(DataUtils.getCountryCode(getActivity()));

        if (NetworkUtils.isOnline()) {
            progressDialog.show();
            userAPIService.resendOtp(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    if (!response.isSuccess()) {
                        Utils.showToast(getActivity(), "Failed to resend");
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    progressDialog.dismiss();
                }
            });
        } else {
            Utils.showToast(getActivity(), "No Internet!");
        }
    }

    @OnClick(R.id.register_otp_change_number)
    public void onClickChangeNumber(View v) {
        ((RegistrationActivity) getActivity()).replaceFragment(RegisterMobileFragment.newInstance(null, null));
    }

    @OnClick(R.id.register_otp_resend)
    public void onClickResendOtp(View v) {
        doResendOtp();
    }

    private void launchHomeScreen() {
        getActivity().getIntent().putExtra("complete", true);
        ((RegistrationActivity) getActivity()).replaceFragment(ProfileFragment.newInstance(null, null));
    }

}
