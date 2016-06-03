package statifyi.com.statifyi.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.SingleFragmentActivity;
import statifyi.com.statifyi.api.model.User;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.dialog.ContactsSuggestionDialog;
import statifyi.com.statifyi.fragment.DialerFragment;
import statifyi.com.statifyi.model.Contact;
import statifyi.com.statifyi.utils.NetworkUtils;
import statifyi.com.statifyi.utils.StringMatcher;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 12/11/15.
 */
public class ContactsAdapter extends BaseSwipeAdapter implements Filterable, SectionIndexer {

    private UserAPIService userAPIService;
    private DBHelper dbHelper;
    private Activity mContext;
    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private List<Contact> originalData = null;
    private List<Contact> filteredData = null;
    private ItemFilter mFilter = new ItemFilter();
    private ArrayList<User> users;
    private ContactsSuggestionDialog.ContactsSuggestionListener contactsSuggestionListener;

    public ContactsAdapter(Activity mContext, List<Contact> contacts) {
        this.mContext = mContext;
        userAPIService = NetworkUtils.provideUserAPIService(mContext);
        dbHelper = DBHelper.getInstance(mContext);
        setData(contacts);
    }

    public void setData(List<Contact> contacts) {
        this.filteredData = contacts;
        this.originalData = contacts;
        users = loadUsers();
    }

    public ArrayList<User> loadUsers() {
        return dbHelper.getAllUsers();
    }

    public void setContactsSuggestionListener(ContactsSuggestionDialog.ContactsSuggestionListener contactsSuggestionListener) {
        this.contactsSuggestionListener = contactsSuggestionListener;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, null);
        final SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, v.findViewById(R.id.bottom_wrapper));
        swipeLayout.setLeftSwipeEnabled(true);
        swipeLayout.setRightSwipeEnabled(false);
        return v;
    }

    private User findUser(String mobile) {
        if (mobile == null) {
            return null;
        }
        for (User u : users) {
            mobile = mobile.replaceAll("[^0-9]", "");
            if (mobile.contains(u.getMobile())) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        ViewHolder holder = new ViewHolder(convertView);

        final Contact mContact = filteredData.get(position);
        holder.name.setText(mContact.getName());
        final String mobile = mContact.getMobile();
        String tenDigitNumber = Utils.getLastTenDigits(mobile);
        holder.mobile.setText(mobile);

        User mUser = null;
        if (tenDigitNumber != null) {
            mUser = findUser(tenDigitNumber);
//            if (mUser == null) {
//                fetchStatus(tenDigitNumber, holder);
//            }
        }
        setStatusData(holder, mUser);

        String photo = mContact.getPhoto();
        if (photo != null) {
            holder.avatar.setImageURI(Uri.parse(photo));
        } else {
            Picasso picasso = NetworkUtils.providePicasso(mContext);
            picasso.load(NetworkUtils.provideAvatarUrl(tenDigitNumber))
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.avatar);
        }
        final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        convertView.findViewById(R.id.contact_list_item_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeLayout.close();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + filteredData.get(position).getMobile()));
                mContext.startActivity(intent);
            }
        });

        convertView.findViewById(R.id.contact_list_item_surface).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactsSuggestionListener != null) {
                    contactsSuggestionListener.onContactSelected(position);
                } else {
                    Intent intent = new Intent(mContext, SingleFragmentActivity.class);
                    intent.putExtra(SingleFragmentActivity.KEY_SINGLE_FRAGMENT, SingleFragmentActivity.FragmentName.DIALER);
                    intent.putExtra("title", "Dial");
                    intent.putExtra(DialerFragment.PARAM_MOBILE_NUM, mobile);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    private void setStatusData(ViewHolder holder, User mUser) {
        if (mUser != null && mUser.getStatus() != null) {
            holder.statusLayout.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.status.setText(mUser.getStatus());
            holder.icon.setImageResource(Utils.getDrawableResByName(mContext, mUser.getIcon()));
            holder.time.setText(Utils.timeAgo(mUser.getUpdated()));
        } else {
            holder.statusLayout.setVisibility(View.INVISIBLE);
            holder.time.setVisibility(View.INVISIBLE);
            holder.status.setText(null);
        }
    }

    @Override
    public int getCount() {
        return this.filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public int getPositionForSection(int section) {
        // If there is no item for current section, previous section will be selected
        for (int i = section; i >= 0; i--) {
            for (int j = 0; j < getCount(); j++) {
                if (i == 0) {
                    // For numeric section
                    for (int k = 0; k <= 9; k++) {
                        if (StringMatcher.match(String.valueOf(this.filteredData.get(j).getName().charAt(0)), String.valueOf(k)))
                            return j;
                    }
                } else {
                    if (StringMatcher.match(String.valueOf(this.filteredData.get(j).getName().charAt(0)), String.valueOf(mSections.charAt(i))))
                        return j;
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++)
            sections[i] = String.valueOf(mSections.charAt(i));
        return sections;
    }

    static class ViewHolder {
        @InjectView(R.id.conatct_list_item_name)
        TextView name;
        @InjectView(R.id.conatct_list_item_mobile)
        TextView mobile;
        @InjectView(R.id.conatct_list_item_status)
        TextView status;
        @InjectView(R.id.conatct_list_item_status_icon)
        ImageView icon;
        @InjectView(R.id.conatct_list_item_status_layout)
        RelativeLayout statusLayout;
        @InjectView(R.id.conatct_list_item_time)
        TextView time;
        @InjectView(R.id.contact_list_item_alphabet)
        TextView alphabet;
        @InjectView(R.id.conatct_list_item_avatar)
        ImageView avatar;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Contact> list = originalData;

            int count = list.size();
            final ArrayList<Contact> nlist = new ArrayList<Contact>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            filteredData = (ArrayList<Contact>) results.values;
            notifyDataSetChanged();
        }
    }
}