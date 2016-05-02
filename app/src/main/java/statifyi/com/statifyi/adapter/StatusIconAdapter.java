package statifyi.com.statifyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import statifyi.com.statifyi.R;
import statifyi.com.statifyi.utils.Utils;

/**
 * Created by KT on 02/05/16.
 */
public class StatusIconAdapter extends ArrayAdapter<String> {

    private final String[] statusMessages;
    private Context mContext;

    public StatusIconAdapter(Context context, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);
        this.mContext = context;
        statusMessages = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View row = inflater.inflate(R.layout.custom_status_icon_item, parent, false);

        ImageView icon = (ImageView) row.findViewById(R.id.custom_status_item_icon);
        icon.setImageResource(Utils.getDrawableResByName(mContext, statusMessages[position]));

        return row;
    }
}