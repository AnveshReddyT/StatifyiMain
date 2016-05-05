package statifyi.com.statifyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.data.DBHelper;
import statifyi.com.statifyi.model.Status;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 24/12/15.
 */
public class StatusAdapter extends BaseAdapter {

    private final String[] defaultStatusList;
    private final DBHelper dbHelper;
    private Context mContext;
    private ArrayList<Status> statusMessages;
    private int totalItems;
    private int extraItems;

    public StatusAdapter(Context mContext) {
        this.mContext = mContext;
        dbHelper = DBHelper.getInstance(mContext);
        defaultStatusList = mContext.getResources().getStringArray(R.array.default_status_list);
        loadData();
    }

    public void loadData() {
        statusMessages = dbHelper.getCustomStatusList();
        for (String defaultStatus : defaultStatusList) {
            Status status = new Status();
            status.setStatus(defaultStatus);
            status.setIcon(defaultStatus);
            status.setDate(0);
            statusMessages.add(status);
        }
        totalItems = statusMessages.size();
        extraItems = statusMessages.size() % 3;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.status_grid_item, null, false);
        ViewHolder holder = new ViewHolder(convertView);

        if (position % 3 == 0) {
            holder.leftDivider.setVisibility(View.INVISIBLE);
        } else {
            holder.leftDivider.setVisibility(View.VISIBLE);
        }

        if (isLastRow(position)) {
            holder.bottomDivider.setVisibility(View.INVISIBLE);
        } else {
            holder.bottomDivider.setVisibility(View.VISIBLE);
        }

        Status statusMessage = statusMessages.get(position);
        holder.status.setText(statusMessage.getStatus());
        holder.icon.setImageResource(Utils.getDrawableResByName(mContext, statusMessage.getIcon()));
//        Animation scaleAnim = AnimationUtils.loadAnimation(mContext, R.anim.scale);
//        holder.icon.startAnimation(scaleAnim);
        return convertView;
    }

    @Override
    public int getCount() {
        return statusMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return statusMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isLastRow(int position) {
        return totalItems - (position + 1) < extraItems;
    }

    static class ViewHolder {
        @InjectView(R.id.status_grid_item_text)
        TextView status;
        @InjectView(R.id.status_grid_item_icon)
        ImageView icon;
        @InjectView(R.id.status_grid_item_left_divider)
        View leftDivider;
        @InjectView(R.id.status_grid_item_bottom_divider)
        View bottomDivider;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
