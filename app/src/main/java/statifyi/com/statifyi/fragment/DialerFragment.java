package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.CustomCallRequest;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.dialog.CustomCallDialog;
import statifyi.com.statifyi.dialog.ProgressDialog;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.Button;
import statifyi.com.statifyi.widget.TextView;


public class DialerFragment extends Fragment implements View.OnClickListener {

    public static final String PARAM_MOBILE_NUM = "MOBILE_NUMBER";
    @InjectView(R.id.dialpad_0_layout)
    RelativeLayout dialpad0Layout;
    @InjectView(R.id.dialpad_1_layout)
    RelativeLayout dialpad1Layout;
    @InjectView(R.id.dialpad_2_layout)
    RelativeLayout dialpad2Layout;
    @InjectView(R.id.dialpad_3_layout)
    RelativeLayout dialpad3Layout;
    @InjectView(R.id.dialpad_4_layout)
    RelativeLayout dialpad4Layout;
    @InjectView(R.id.dialpad_5_layout)
    RelativeLayout dialpad5Layout;
    @InjectView(R.id.dialpad_6_layout)
    RelativeLayout dialpad6Layout;
    @InjectView(R.id.dialpad_7_layout)
    RelativeLayout dialpad7Layout;
    @InjectView(R.id.dialpad_8_layout)
    RelativeLayout dialpad8Layout;
    @InjectView(R.id.dialpad_9_layout)
    RelativeLayout dialpad9Layout;
    @InjectView(R.id.dialpad_star_layout)
    RelativeLayout dialpadStarLayout;
    @InjectView(R.id.dialpad_hash_layout)
    RelativeLayout dialpadHashLayout;
    @InjectView(R.id.dialpad_message_layout)
    RelativeLayout dialpadMessageLayout;
    @InjectView(R.id.dialpad_call_layout)
    RelativeLayout dialpadCallLayout;
    @InjectView(R.id.dialpad_delete_layout)
    RelativeLayout dialpadDeleteLayout;
    @InjectView(R.id.dialer_text)
    TextView dialerText;
    @InjectView(R.id.dialer_button_emergency)
    Button emergencyBtn;
    @InjectView(R.id.dialer_button_business)
    Button businessBtn;
    @InjectView(R.id.dialer_button_casual)
    Button casualbtn;
    @InjectView(R.id.dialer_button_custom)
    Button customBtn;
    private UserAPIService userAPIService;
    private ProgressDialog progressDialog;

    public DialerFragment() {
        // Required empty public constructor
    }

    public static DialerFragment newInstance(String param1, String param2) {
        DialerFragment fragment = new DialerFragment();
        Bundle args = new Bundle();
        if (param1 != null) {
            args.putString(PARAM_MOBILE_NUM, param1);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dialer, container, false);
        ButterKnife.inject(this, root);
        userAPIService = NetworkUtils.provideUserAPIService(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        if (getActivity().getIntent().hasExtra(PARAM_MOBILE_NUM)) {
            dialerText.setText(getActivity().getIntent().getStringExtra(PARAM_MOBILE_NUM));
        }
        RelativeLayout[] layouts = {
                dialpad0Layout, dialpad1Layout, dialpad2Layout, dialpad3Layout,
                dialpad4Layout, dialpad5Layout, dialpad6Layout, dialpad7Layout,
                dialpad8Layout, dialpad9Layout, dialpadHashLayout, dialpadStarLayout,
                dialpadMessageLayout, dialpadCallLayout, dialpadDeleteLayout
        };

        Button buttons[] = {
                emergencyBtn, businessBtn, casualbtn, customBtn
        };

        int width = Utils.getScreenWidth(getActivity()) - 64;
        int height = Utils.getScreenHeight(getActivity());
        height = (int) (height * 0.7);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dialpad0Layout.getLayoutParams();
        layoutParams.width = width / 3;
        layoutParams.height = height / 6;

        for (RelativeLayout layout : layouts) {
            layout.setLayoutParams(layoutParams);
            MaterialRippleLayout.on(layout)
                    .rippleOverlay(true)
                    .rippleAlpha(0.2f)
                    .rippleColor(getResources().getColor(R.color.accentColor))
                    .rippleHover(true)
                    .create();
            layout.setOnClickListener(this);
        }

        for (Button button : buttons) {


            button.setOnClickListener(this);
        }

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialpad_0_layout:
                dialerText.append("0");
                break;
            case R.id.dialpad_1_layout:
                dialerText.append("1");
                break;
            case R.id.dialpad_2_layout:
                dialerText.append("2");
                break;
            case R.id.dialpad_3_layout:
                dialerText.append("3");
                break;
            case R.id.dialpad_4_layout:
                dialerText.append("4");
                break;
            case R.id.dialpad_5_layout:
                dialerText.append("5");
                break;
            case R.id.dialpad_6_layout:
                dialerText.append("6");
                break;
            case R.id.dialpad_7_layout:
                dialerText.append("7");
                break;
            case R.id.dialpad_8_layout:
                dialerText.append("8");
                break;
            case R.id.dialpad_9_layout:
                dialerText.append("9");
                break;
            case R.id.dialpad_star_layout:
                dialerText.append("*");
                break;
            case R.id.dialpad_hash_layout:
                dialerText.append("#");
                break;
            case R.id.dialpad_call_layout:
                CharSequence phone = dialerText.getText();
                if (Patterns.PHONE.matcher(phone).matches()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone.toString()));
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.invalid_number), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.dialpad_delete_layout:
                CharSequence editable = dialerText.getText();
                if (!TextUtils.isEmpty(editable)) {
                    String text = editable.toString();
                    dialerText.setText(text.substring(0, text.length() - 1));
                }
                break;
            case R.id.dialpad_message_layout:
                CharSequence mobile = dialerText.getText();
                if (Patterns.PHONE.matcher(mobile).matches()) {
                    Utils.sendSMS(getActivity(), mobile.toString());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.invalid_number), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.dialer_button_emergency:
                if (Patterns.PHONE.matcher(dialerText.getText()).matches()) {
                    makeCustomCallRequest(getString(R.string.emergency_call));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.invalid_number), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.dialer_button_business:
                if (Patterns.PHONE.matcher(dialerText.getText()).matches()) {
                    makeCustomCallRequest(getString(R.string.business_call));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.invalid_number), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.dialer_button_casual:
                if (Patterns.PHONE.matcher(dialerText.getText()).matches()) {
                    makeCustomCallRequest(getString(R.string.casual_call));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.invalid_number), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.dialer_button_custom:
                if (Patterns.PHONE.matcher(dialerText.getText()).matches()) {
                    final CustomCallDialog customCallDialog = new CustomCallDialog(getActivity());
                    customCallDialog.show();
                    customCallDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (!TextUtils.isEmpty(customCallDialog.getMessage())) {
                                makeCustomCallRequest(customCallDialog.getMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), getString(R.string.invalid_number), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private void makeCustomCallRequest(String message) {
        CustomCallRequest request = new CustomCallRequest();
        request.setFromMobile(DataUtils.getMobileNumber(getActivity()));
        request.setMobile(Utils.getLastTenDigits(dialerText.getText().toString()));
        request.setMessage(message);
        progressDialog.show();
        userAPIService.customCall(request).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Response<Boolean> response, Retrofit retrofit) {
                progressDialog.dismiss();
                if (response.isSuccess()) {
                    if (response.body()) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + dialerText.getText().toString()));
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Utils.showToast(getActivity(), "Failed! Please try again.");
            }
        });
    }

}
