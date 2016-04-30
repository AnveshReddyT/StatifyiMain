package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import statifyi.com.statifyi.api.model.StatusResponse;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.dialog.ProgressDialog;
import statifyi.com.statifyi.service.SyncAllStatusService;
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

    @OnClick(R.id.register_otp_btn)
    public void onClick(View v) {
        doActivateUser();
    }

    private void doActivateUser() {
        final ActivateUserRequest request = new ActivateUserRequest();
        request.setCode(otpText.getText().toString());
        final String mobileNumber = DataUtils.getMobileNumber(getActivity());
        request.setMobile(mobileNumber);

        progressDialog.show();
        userAPIService.activateUser(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    getActivity().startService(new Intent(getActivity(), SyncAllStatusService.class));
                    userAPIService.getUserStatus(request.getMobile()).enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Response<StatusResponse> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                StatusResponse s = response.body();
                                DataUtils.setActive(getActivity(), true);
                                DataUtils.saveName(getActivity(), s.getName());
                                DataUtils.saveStatus(getActivity(), s.getStatus());
                                DataUtils.saveIcon(getActivity(), Utils.getDrawableResByName(getActivity(), s.getIcon()));
                                if (!TextUtils.isEmpty(s.getName())) {
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
    }

    @OnClick(R.id.register_otp_change_number)
    public void onClickChangeNumber(View v) {
        ((RegistrationActivity) getActivity()).replaceFragment(RegisterMobileFragment.newInstance(null, null));
    }

    private void launchHomeScreen() {
        getActivity().getIntent().putExtra("complete", true);
        ((RegistrationActivity) getActivity()).replaceFragment(ProfileFragment.newInstance(null, null));
    }

}
