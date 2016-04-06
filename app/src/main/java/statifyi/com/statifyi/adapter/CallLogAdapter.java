package statifyi.com.statifyi.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.SingleFragmentActivity;
import statifyi.com.statifyi.api.model.User;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.fragment.DialerFragment;
import statifyi.com.statifyi.model.CallLog;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 12/11/15.
 */
public class CallLogAdapter extends BaseSwipeAdapter implements Filterable {

    private Context mContext;
    private List<CallLog> originalData = null;
    private List<statifyi.com.statifyi.model.CallLog> filteredData = null;
    private ItemFilter mFilter = new ItemFilter();
    private ArrayList<User> users;

    public CallLogAdapter(Context mContext, List<CallLog> callLogs) {
        this.mContext = mContext;
        DBHelper dbHelper = new DBHelper(mContext);
        setData(callLogs);
        users = dbHelper.getAllUsers();
    }

    public void setData(List<CallLog> callLogs) {
        this.filteredData = callLogs;
        this.originalData = callLogs;
    }

    public int getCount() {
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.calllog_list_item, null);
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

        final statifyi.com.statifyi.model.CallLog callLog = filteredData.get(position);
        String callLogName = callLog.getName();
        callLogName = callLogName == null ? callLog.getPhone() : callLogName;
        holder.name.setText(callLogName);
        holder.mobile.setText(Utils.timeAgo(callLog.getDate()));
        int calltypeDrawable = 0;
        if (callLog.getType() == CallLog.CallType.OUTGOING) {
            calltypeDrawable = R.drawable.ic_call_outgoing;
        } else if (callLog.getType() == CallLog.CallType.INCOMING) {
            calltypeDrawable = R.drawable.ic_call_incoming;
        } else if (callLog.getType() == CallLog.CallType.MISSED) {
            calltypeDrawable = R.drawable.ic_call_missed;
        }

        User mUser = findUser(callLog.getPhone());
        if (mUser != null) {
            holder.statusLayout.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.status.setText(mUser.getStatus());
            holder.icon.setImageResource(Utils.getDrawableResByName(mContext, mUser.getStatus()));
            holder.time.setText(Utils.timeAgo(mUser.getUpdated()));
        } else {
            holder.statusLayout.setVisibility(View.INVISIBLE);
            holder.time.setVisibility(View.INVISIBLE);
            holder.status.setText(null);
        }

        holder.mobile.setCompoundDrawablesWithIntrinsicBounds(calltypeDrawable, 0, 0, 0);
        String photo = callLog.getPhoto();
        if (photo != null) {
            holder.avatar.setImageURI(Uri.parse(photo));
            holder.avatar.setVisibility(View.VISIBLE);
            holder.alphabet.setVisibility(View.GONE);
        } else {
            holder.avatar.setImageDrawable(null);
            holder.alphabet.setText(callLogName.substring(0, 1));
            holder.alphabet.setVisibility(View.VISIBLE);
            holder.avatar.setVisibility(View.GONE);
        }
        final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        convertView.findViewById(R.id.calllog_list_item_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeLayout.close();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + filteredData.get(position).getPhone()));
                mContext.startActivity(intent);
            }
        });
        convertView.findViewById(R.id.calllog_list_item_surface).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SingleFragmentActivity.class);
                intent.putExtra(SingleFragmentActivity.KEY_SINGLE_FRAGMENT, SingleFragmentActivity.FragmentName.DIALER);
                intent.putExtra("title", "Dial");
                intent.putExtra(DialerFragment.PARAM_MOBILE_NUM, callLog.getPhone());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    static class ViewHolder {
        @InjectView(R.id.calllog_list_item_name)
        TextView name;
        @InjectView(R.id.calllog_list_item_mobile)
        TextView mobile;
        @InjectView(R.id.calllog_list_item_status)
        TextView status;
        @InjectView(R.id.calllog_list_item_status_icon)
        ImageView icon;
        @InjectView(R.id.calllog_list_item_status_layout)
        RelativeLayout statusLayout;
        @InjectView(R.id.calllog_list_item_time)
        TextView time;
        @InjectView(R.id.calllog_list_item_alphabet)
        TextView alphabet;
        @InjectView(R.id.calllog_list_item_avatar)
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

            final List<statifyi.com.statifyi.model.CallLog> list = originalData;

            int count = list.size();
            final ArrayList<CallLog> nlist = new ArrayList<statifyi.com.statifyi.model.CallLog>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                String name = list.get(i).getName();
                filterableString = name == null ? list.get(i).getPhone() : name;
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
            filteredData = (ArrayList<statifyi.com.statifyi.model.CallLog>) results.values;
            notifyDataSetChanged();
        }

    }
}