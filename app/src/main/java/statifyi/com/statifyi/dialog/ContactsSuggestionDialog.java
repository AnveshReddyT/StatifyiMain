package statifyi.com.statifyi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.adapter.ContactsAdapter;
import statifyi.com.statifyi.model.Contact;
import statifyi.com.statifyi.utils.Utils;

/**
 * Created by KT on 19/02/16.
 */
public class ContactsSuggestionDialog extends Dialog {

    @InjectView(R.id.contactsList)
    ListView contactsListview;

    private List<Contact> contacts;

    private ContactsAdapter contactsAdapter;

    private Contact contact;

    private Context mContext;

    private Activity mActivity;

    public ContactsSuggestionDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ContactsSuggestionDialog(Activity context, List<Contact> contacts) {
        super(context);
        this.mContext = context;
        this.contacts = contacts;
        this.mActivity = context;
        for (Contact contact : contacts) {
            Log.d("MSP", contact.toString());
        }
    }

    public ContactsSuggestionDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_contacts_suggestions);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.inject(this);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        int height = Utils.getScreenHeight(mContext);
        int width = Utils.getScreenWidth(mContext);
        lp.width = (int) (width * 0.85);
//        lp.height = (int) (height * 0.85);
        getWindow().setAttributes(lp);

        contactsAdapter = new ContactsAdapter(mActivity, contacts);
        contactsAdapter.setContactsSuggestionListener(new ContactsSuggestionListener() {
            @Override
            public void onContactSelected(int position) {
                setContact(contacts.get(position));
                dismiss();
            }
        });
        contactsListview.setAdapter(contactsAdapter);
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public interface ContactsSuggestionListener {
        void onContactSelected(int position);
    }
}
