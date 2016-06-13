package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.api.model.CustomCall;
import statifyi.com.statifyi.api.model.CustomCallRequest;
import statifyi.com.statifyi.api.model.TopicMessageData;
import statifyi.com.statifyi.api.model.TopicMessageRequest;
import statifyi.com.statifyi.api.model.User;
import statifyi.com.statifyi.api.service.GCMAPIService;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.dialog.ContactsSuggestionDialog;
import statifyi.com.statifyi.dialog.CustomCallDialog;
import statifyi.com.statifyi.dialog.InfoDialog;
import statifyi.com.statifyi.dialog.ProgressDialog;
import statifyi.com.statifyi.model.Contact;
import statifyi.com.statifyi.service.FloatingService;
import statifyi.com.statifyi.service.GCMIntentService;
import statifyi.com.statifyi.utils.DataUtils;
import statifyi.com.statifyi.utils.DialerUtils;
import statifyi.com.statifyi.utils.GCMUtils;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.Button;
import statifyi.com.statifyi.widget.TextView;


public class DialerFragment extends Fragment implements View.OnClickListener {

    public static final String PARAM_MOBILE_NUM = "MOBILE_NUMBER";

    @InjectView(R.id.dialpad_0_layout)
    LinearLayout dialpad0Layout;

    @InjectView(R.id.dialpad_1_layout)
    LinearLayout dialpad1Layout;

    @InjectView(R.id.dialpad_2_layout)
    LinearLayout dialpad2Layout;

    @InjectView(R.id.dialpad_3_layout)
    LinearLayout dialpad3Layout;

    @InjectView(R.id.dialpad_4_layout)
    LinearLayout dialpad4Layout;

    @InjectView(R.id.dialpad_5_layout)
    LinearLayout dialpad5Layout;

    @InjectView(R.id.dialpad_6_layout)
    LinearLayout dialpad6Layout;

    @InjectView(R.id.dialpad_7_layout)
    LinearLayout dialpad7Layout;

    @InjectView(R.id.dialpad_8_layout)
    LinearLayout dialpad8Layout;

    @InjectView(R.id.dialpad_9_layout)
    LinearLayout dialpad9Layout;

    @InjectView(R.id.dialpad_star_layout)
    LinearLayout dialpadStarLayout;

    @InjectView(R.id.dialpad_hash_layout)
    LinearLayout dialpadHashLayout;

    @InjectView(R.id.dialpad_message_layout)
    LinearLayout dialpadMessageLayout;

    @InjectView(R.id.dialpad_call_layout)
    LinearLayout dialpadCallLayout;

    @InjectView(R.id.dialpad_delete_layout)
    LinearLayout dialpadDeleteLayout;

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

    @InjectView(R.id.dialpad_contact_name)
    TextView contactName;

    @InjectView(R.id.dialpad_status)
    TextView contactStatus;

    @InjectView(R.id.dialpad_status_icon)
    ImageView contactStatusIcon;

    @InjectView(R.id.dialpad_status_layout)
    RelativeLayout contactStatusLayout;

    @InjectView(R.id.dialpad_contact_layout)
    RelativeLayout contactLayout;

    @InjectView(R.id.dialpad_more_contacts)
    ImageView moreContacts;

    private UserAPIService userAPIService;

    private ProgressDialog progressDialog;

    private DBHelper dbHelper;

    private DialerUtils dialerUtils;

    private List<Contact> contactList;

    private Thread contactsThread;

    private List<Contact> totalContacts;

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
        dbHelper = DBHelper.getInstance(getActivity());
        dialerUtils = new DialerUtils();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        contactList = new ArrayList<>();
        totalContacts = Utils.readPhoneContacts(getActivity());

        if (getActivity().getIntent().hasExtra(PARAM_MOBILE_NUM)) {
            dialerText.setText(getActivity().getIntent().getStringExtra(PARAM_MOBILE_NUM));
            startSuggestionsThread();
        }
        LinearLayout[] layouts = {
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
        height = (int) (height * 0.6);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dialpad0Layout.getLayoutParams();
        layoutParams.width = width / 3;
        layoutParams.height = height / 6;

        for (LinearLayout layout : layouts) {
            layout.setLayoutParams(layoutParams);
            MaterialRippleLayout.on(layout)
                    .rippleOverlay(true)
                    .rippleAlpha(0.2f)
                    .rippleDuration(400)
                    .rippleColor(getResources().getColor(R.color.accentColor))
                    .rippleHover(true)
                    .create();
            layout.setOnClickListener(this);
        }

        for (Button button : buttons) {
            button.setOnClickListener(this);
        }
        dialpad0Layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickedDialPad("+");
                return true;
            }
        });

        contactLayout.setOnClickListener(this);

        return root;
    }

    private void startSuggestionsThread() {
        if (contactsThread != null) {
            contactsThread.interrupt();
        }
        CharSequence mobile = dialerText.getText();
        moreContacts.setVisibility((!TextUtils.isEmpty(mobile) && mobile.length() > 1) ? View.VISIBLE : View.INVISIBLE);
        contactsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                fetchContacts();
            }
        });
        contactsThread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialpad_0_layout:
                clickedDialPad("0");
                break;
            case R.id.dialpad_1_layout:
                clickedDialPad("1");
                break;
            case R.id.dialpad_2_layout:
                clickedDialPad("2");
                break;
            case R.id.dialpad_3_layout:
                clickedDialPad("3");
                break;
            case R.id.dialpad_4_layout:
                clickedDialPad("4");
                break;
            case R.id.dialpad_5_layout:
                clickedDialPad("5");
                break;
            case R.id.dialpad_6_layout:
                clickedDialPad("6");
                break;
            case R.id.dialpad_7_layout:
                clickedDialPad("7");
                break;
            case R.id.dialpad_8_layout:
                clickedDialPad("8");
                break;
            case R.id.dialpad_9_layout:
                clickedDialPad("9");
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
                    startSuggestionsThread();
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
            case R.id.dialpad_contact_layout:
                if (contactName.getTag() != null) {
                    dialerText.setText(contactName.getTag().toString());
                }
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.dialpad_more_contacts)
    public void showContactsDialog() {
        if (contactList != null && !contactList.isEmpty()) {
            final ContactsSuggestionDialog contactsSuggestionDialog = new ContactsSuggestionDialog(getActivity(), contactList);
            contactsSuggestionDialog.show();
            contactsSuggestionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (contactsSuggestionDialog.getContact() != null) {
                        Contact contact = contactsSuggestionDialog.getContact();
                        contactName.setText(contact.getName());
                        contactName.setTag(contact.getMobile());
                        if (contactName.getTag() != null) {
                            dialerText.setText(contactName.getTag().toString());
                        }
                        User user = dbHelper.getUser(Utils.getLastTenDigits(contact.getMobile()));
                        if (user != null) {
                            contactStatusLayout.setVisibility(View.VISIBLE);
                            contactStatus.setText(user.getStatus());
                            contactStatusIcon.setImageResource(Utils.getDrawableResByName(getActivity(), user.getIcon()));
                        } else {
                            contactStatusLayout.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    private void clickedDialPad(String letter) {
        dialerText.append(letter);
        startSuggestionsThread();
    }

    private void getContactsByNumber(String number) {
        for (Contact mContact : totalContacts) {
            if (mContact.getMobile() != null && mContact.getMobile().contains(number)) {
                contactList.add(mContact);
                updateSuggestion();
            }
        }
    }

    private void getContactsByName(String name) {
        name = name.toLowerCase();
        for (Contact mContact : totalContacts) {
            String mContactName = mContact.getName().toLowerCase();
            if (mContactName.startsWith(name)) {
                contactList.add(mContact);
                updateSuggestion();
            }
        }
    }

    private void fetchContacts() {
        CharSequence mobile = dialerText.getText();
        contactList.clear();
        if (!TextUtils.isEmpty(mobile) && mobile.length() > 1) {
//            suggestPhoneContacts(getActivity(), mobile.toString(), true);
            getContactsByNumber(mobile.toString());
            ArrayList<String> list = dialerUtils.letterCombinations(mobile.toString());
            for (String s : list) {
                if (contactsThread.isInterrupted()) {
                    return;
                }
//                suggestPhoneContacts(getActivity(), s, false);
                getContactsByName(s);
            }
            if (contactList.isEmpty()) {
                clearSuggestions();
            }
        } else {
            clearSuggestions();
        }
    }

    private void clearSuggestions() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contactName.setText(null);
                contactName.setTag(null);
                contactStatusLayout.setVisibility(View.GONE);
            }
        });
    }

    private void suggestPhoneContacts(Context cntx, String partial, boolean isNumber) {
        final String[] PROJECTION = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        };
        List<String> temp = new ArrayList<>();
        String selection = null;
        if (isNumber) {
            selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + partial + "%'";
        } else {
            selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%" + partial + "%'";
        }
        if (cntx == null) {
            return;
        }
        Cursor cursor = cntx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, selection, null, null);
        if (cursor != null) {
            try {
                Integer contactsCount = cursor.getCount();
                if (contactsCount > 0 && cursor.moveToFirst()) {
                    while (cursor.moveToNext()) {
                        if (contactsThread.isInterrupted()) {
                            return;
                        }
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                        String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo = phoneNo.replaceAll(" ", "");
                        if (!temp.contains(phoneNo)) {
                            Contact mContact = new Contact();
                            mContact.setMobile(phoneNo);
                            mContact.setName(name);
                            mContact.setPhoto(imageUri);
                            contactList.add(mContact);
                            updateSuggestion();
                            temp.add(phoneNo);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
    }

    private void updateSuggestion() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!contactList.isEmpty()) {
                    Contact contact = contactList.get(0);
                    contactName.setText(contact.getName());
                    contactName.setTag(contact.getMobile());
                    User user = dbHelper.getUser(Utils.getLastTenDigits(contact.getMobile()));
                    if (user != null) {
                        contactStatusLayout.setVisibility(View.VISIBLE);
                        contactStatus.setText(user.getStatus());
                        contactStatusIcon.setImageResource(Utils.getDrawableResByName(getActivity(), user.getIcon()));
                    } else {
                        contactStatusLayout.setVisibility(View.GONE);
                    }
                } else {
                    contactName.setText(null);
                    contactName.setTag(null);
                    contactStatusLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void makeCustomCallRequest(final String message) {
        if (NetworkUtils.isOnline()) {
            final String lastTenDigits = Utils.getLastTenDigits(dialerText.getText().toString());

            User user = dbHelper.getUser(lastTenDigits);
            if (user != null) {
                CustomCall customCall = new CustomCall();
                customCall.setMessage(message);
                customCall.setMobile(DataUtils.getMobileNumber(getActivity()));
                TopicMessageRequest topicRequest = new TopicMessageRequest();
                topicRequest.setTo(GCMIntentService.TOPICS + lastTenDigits + "-customCall");
                TopicMessageData data = new TopicMessageData();
                data.setMessage(customCall.toString());
                topicRequest.setData(data);

                GCMAPIService gcmapiService = NetworkUtils.provideGCMAPIService(getActivity(), true);
                gcmapiService.sendGcmMessaheToTopic(topicRequest).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                        if (!response.isSuccess()) {
                            Utils.showToast(getActivity(), "Failed! Please make a normal call");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        progressDialog.dismiss();
                        Utils.showToast(getActivity(), "Failed! Please try again.");
                    }
                });
                FloatingService.customCall = getCustomCall(message, lastTenDigits);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + dialerText.getText().toString()));
                startActivity(intent);
            } else {
                if (TextUtils.isEmpty(contactName.getText())) {
                    progressDialog.show();
                    CustomCallRequest request = new CustomCallRequest();
                    request.setMobile(lastTenDigits);
                    request.setMessage(message);
                    userAPIService.customCall(GCMUtils.getRegistrationId(getActivity()), request).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Response<Boolean> response, Retrofit retrofit) {
                            progressDialog.dismiss();
                            if (response.isSuccess()) {
                                if (response.body()) {
                                    FloatingService.customCall = getCustomCall(message, lastTenDigits);
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:" + dialerText.getText().toString()));
                                    startActivity(intent);
                                } else {
                                    showInfoDialog(dialerText.getText().toString());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            progressDialog.dismiss();
                            Utils.showToast(getActivity(), "Failed! Please try again.");
                        }
                    });
                } else {
                    showInfoDialog(dialerText.getText().toString());
                }
            }
        } else {
            Utils.showToast(getActivity(), "No Internet! Please make a normal call");
        }
    }

    @NonNull
    private CustomCall getCustomCall(String message, String lastTenDigits) {
        CustomCall customCall = new CustomCall();
        customCall.setMessage(message);
        customCall.setMobile(lastTenDigits);
        customCall.setTime(System.currentTimeMillis());
        return customCall;
    }

    private void showInfoDialog(String mobile) {
        String contact = Utils.getContactName(getActivity(), mobile);
        InfoDialog infoDialog = new InfoDialog(getActivity());
        infoDialog.show();
        infoDialog.setInfoTitle("STATIFYI");
        infoDialog.setMessage("You cannot make a custom call!\n" + contact + " is not on StatiFYI.");
    }

}
