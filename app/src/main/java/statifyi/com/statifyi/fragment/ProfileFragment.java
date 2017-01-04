package statifyi.com.statifyi.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.camera.CropImageIntentBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.HomeActivity;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.UserNameRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.dialog.ProgressDialog;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.GAUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.Button;
import statifyi.com.statifyi.widget.EditText;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_PICTURE = 301;

    private static final int REQUEST_CROP_PICTURE = 302;

    @InjectView(R.id.profile_avatar)
    CircularImageView avatar;

    @InjectView(R.id.profile_full_name)
    EditText fullName;

    @InjectView(R.id.profile_set_btn)
    Button setBtn;

    private UserAPIService userAPIService;

    private ProgressDialog progressDialog;

    private boolean isFirstTime;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAPIService = NetworkUtils.provideUserAPIService(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        GAUtils.sendScreenView(getActivity().getApplicationContext(), ProfileFragment.class.getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, root);
        fullName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    onClickUpdateProfile(fullName);
                    return true;
                }
                return false;
            }
        });
        String name = DataUtils.getName(getActivity());
        if (name != null) {
            fullName.setText(name);
        }
        isFirstTime = getActivity().getIntent().hasExtra("complete");
        if (isFirstTime) {
            setBtn.setText(R.string.finish);
        } else {
            setBtn.setText(R.string.update_profile);
        }
        DataUtils.setUserImage(getActivity(), avatar);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        File croppedImageFile = new File(getActivity().getFilesDir(), "cropped.jpg");
        if ((requestCode == REQUEST_PICTURE) && (resultCode == Activity.RESULT_OK)) {
            Uri croppedImage = Uri.fromFile(croppedImageFile);

            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(256, 256, croppedImage);
            cropImage.setSourceImage(data.getData());

            startActivityForResult(cropImage.getIntent(getActivity()), REQUEST_CROP_PICTURE);
        } else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == Activity.RESULT_OK)) {
            if (DataUtils.copyCroppedImage(getActivity())) {
                DataUtils.setUserImage(getActivity(), avatar);
            }
        }
    }

    @OnClick(R.id.profile_avatar)
    public void onAvatarClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), REQUEST_PICTURE);
    }

    @OnClick(R.id.profile_set_btn)
    public void onClickUpdateProfile(View v) {
        Editable fullNameText = fullName.getText();
        if (fullNameText != null && !TextUtils.isEmpty(fullNameText.toString())) {
            doUpdateProfile(fullNameText.toString());
        } else {
            fullName.setError("Cannot be empty!");
        }
    }

    private void doUpdateProfile(final String name) {
        UserNameRequest request = new UserNameRequest();
        request.setName(name);
        if (NetworkUtils.isConnectingToInternet(getActivity())) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            userAPIService.setUserName(GCMUtils.getRegistrationId(getActivity()), request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        DataUtils.saveName(getActivity(), name);
                        if (DataUtils.isAvatarChanged(getActivity())) {
                            doUploadImage();
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            finishUpdate();
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Utils.showToast(getActivity(), "Failed to update profile");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Utils.showToast(getActivity(), "Failed to update profile");
                }
            });
        } else {
            Utils.showToast(getActivity(), "No Internet!");
        }
    }

    private void finishUpdate() {
        if (isFirstTime) {
            startActivity(new Intent(getActivity(), HomeActivity.class));
        } else {
            Utils.showToast(getActivity(), "Updated Successfully!");
        }
        getActivity().finish();
    }

    private void doUploadImage() {
        String gcmId = GCMUtils.getRegistrationId(getActivity());
        File file = new File(getActivity().getFilesDir(), "user.jpg");
        userAPIService.uploadImage(gcmId, file).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    DataUtils.setAvatarChanged(getActivity(), false);
                    finishUpdate();
                } else {
                    Utils.showToast(getActivity(), "Failed to upload image");
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Utils.showToast(getActivity(), "Failed to upload image");
            }
        });
    }
}
