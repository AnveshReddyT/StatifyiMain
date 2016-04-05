package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.Utils;
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
    @InjectView(R.id.dialer_text)
    TextView dialerText;
    @InjectView(R.id.dialer_input_delete)
    ImageView deleteText;
    @InjectView(R.id.dialer_call_btn)
    android.widget.TextView dialerCallBtn;

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

        if (getActivity().getIntent().hasExtra(PARAM_MOBILE_NUM)) {
            dialerText.setText(getActivity().getIntent().getStringExtra(PARAM_MOBILE_NUM));
        }
        RelativeLayout[] layouts = {
                dialpad0Layout, dialpad1Layout, dialpad2Layout, dialpad3Layout,
                dialpad4Layout, dialpad5Layout, dialpad6Layout, dialpad7Layout,
                dialpad8Layout, dialpad9Layout, dialpadHashLayout, dialpadStarLayout
        };

        int width = Utils.getScreenWidth(getActivity()) - 64;
        int height = Utils.getScreenHeight(getActivity());
        height = (int) (height * 0.75);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dialpad0Layout.getLayoutParams();
        layoutParams.width = width / 3;
        layoutParams.height = height / 5;

        for (RelativeLayout layout : layouts) {
            layout.setLayoutParams(layoutParams);
            layout.setOnClickListener(this);
        }

        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence editable = dialerText.getText();
                if (!TextUtils.isEmpty(editable)) {
                    String text = editable.toString();
                    dialerText.setText(text.substring(0, text.length() - 1));
                }

            }
        });

        dialerCallBtn.setOnClickListener(this);

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
            case R.id.dialer_call_btn:
                CharSequence phone = dialerText.getText();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getActivity(), "Invalid number!", Toast.LENGTH_LONG).show();
                } else if (Patterns.PHONE.matcher(phone).matches()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone.toString()));
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Invalid number!", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}
