package statifyi.com.statifyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.model.CountryCode;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 28/12/15.
 */
public class CountryCodesAdapter extends ArrayAdapter<CountryCode> {

    @InjectView(R.id.country_code_row_name)
    TextView name;

    private Context mContext;

    private List<CountryCode> countryCodes;

    public CountryCodesAdapter(Context context, int resource, List<CountryCode> objects) {
        super(context, resource, objects);
        this.mContext = context;
        countryCodes = objects;
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.country_code_row, parent, false);
        ButterKnife.inject(this, row);
        name.setText(countryCodes.get(position).getName());

        return row;
    }
}
