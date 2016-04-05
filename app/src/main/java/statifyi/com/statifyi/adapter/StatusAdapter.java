package statifyi.com.statifyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.Utils;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 24/12/15.
 */
public class StatusAdapter extends BaseAdapter {

    private Context mContext;
    private String[] statusMessages;

    public StatusAdapter(Context mContext) {
        this.mContext = mContext;
        statusMessages = mContext.getResources().getStringArray(R.array.default_status_list);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.status_grid_item, null, false);
        ViewHolder holder = new ViewHolder(convertView);

        if (position % 3 == 0) {
            holder.divider.setVisibility(View.INVISIBLE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
        String statusMessage = statusMessages[position];
        holder.status.setText(statusMessage);
        holder.icon.setImageResource(Utils.getDrawableResByName(mContext, statusMessage));
        Animation scaleAnim = AnimationUtils.loadAnimation(mContext, R.anim.scale);
        holder.icon.startAnimation(scaleAnim);
        return convertView;
    }

    @Override
    public int getCount() {
        return statusMessages.length;
    }

    @Override
    public Object getItem(int position) {
        return statusMessages[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        @InjectView(R.id.status_grid_item_text)
        TextView status;
        @InjectView(R.id.status_grid_item_icon)
        ImageView icon;
        @InjectView(R.id.status_grid_item_divider)
        View divider;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
