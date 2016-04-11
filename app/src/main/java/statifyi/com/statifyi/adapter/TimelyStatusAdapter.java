package statifyi.com.statifyi.adapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.listener.AlarmReceiver;
import statifyi.com.statifyi.model.TimelyStatus;
import statifyi.com.statifyi.utils.TimelyStatusUtils;
import statifyi.com.statifyi.widget.TextView;

/**
 * Created by KT on 14/02/16.
 */
public class TimelyStatusAdapter extends RecyclerView.Adapter<TimelyStatusAdapter.CustomViewHolder> implements View.OnClickListener {

    private TimelyStatus[] itemList;

    private Context mContext;
    private View.OnClickListener onClickListener;

    public TimelyStatusAdapter(Context mContext) {
        this.mContext = mContext;
        loadStatusList();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.timely_status_list_item, null);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        TimelyStatus timelyStatus = itemList[i];
        final int position = i;

        customViewHolder.name.setText(timelyStatus.getName());
        customViewHolder.icon.setImageResource(timelyStatus.getIcon());
        customViewHolder.start.setText(getFormattedTime(timelyStatus.getStartHour(), timelyStatus.getStartMin()));
        customViewHolder.end.setText(getFormattedTime(timelyStatus.getEndHour(), timelyStatus.getEndMin()));
        customViewHolder.enabled.setChecked(timelyStatus.isEnabled());

        if (timelyStatus.isEnabled()) {
            customViewHolder.start.setOnClickListener(onClickListener);
            customViewHolder.end.setOnClickListener(onClickListener);
            Log.d("STAT", "Called for enabled " + position);
        } else {
            customViewHolder.start.setOnClickListener(null);
            customViewHolder.end.setOnClickListener(null);
            Log.d("STAT", "Called for disabled " + position);
        }

        customViewHolder.enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                Log.d("STAT", "Enabled for " + position);
                itemList[position].setEnabled(isChecked);
                TimelyStatusUtils.saveTimelyStatusList(mContext, itemList);
//                notifyItemChanged(position);
            }
        });

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d("STAT", "Called for clicked " + position);
                switch (v.getId()) {
                    case R.id.timely_status_item_start_time:
                        showTimePickerDialog(new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                                TextView start = (TextView) v;
                                start.setText(getFormattedTime(hourOfDay, minute));
                                setTimelyAlarm(mContext, AlarmReceiver.TIMELY_STATUS_ALARM_START_ID, hourOfDay, minute);
                                itemList[position].setStartHour(hourOfDay);
                                itemList[position].setStartMin(minute);
                                TimelyStatusUtils.saveTimelyStatusList(mContext, itemList);
                            }
                        });
                        break;
                    case R.id.timely_status_item_end_time:
                        showTimePickerDialog(new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                                TextView end = (TextView) v;
                                end.setText(getFormattedTime(hourOfDay, minute));
                                setTimelyAlarm(mContext, AlarmReceiver.TIMELY_STATUS_ALARM_END_ID, hourOfDay, minute);
                                itemList[position].setEndHour(hourOfDay);
                                itemList[position].setEndMin(minute);
                                TimelyStatusUtils.saveTimelyStatusList(mContext, itemList);
                            }
                        });
                        break;
                }
            }
        };
    }

    private String getFormattedTime(int hourOfDay, int minute) {
        if (hourOfDay == -1)
            return " - ";
        String am_pm = " AM";
        if (hourOfDay >= 12) {
            am_pm = " PM";
            if (hourOfDay > 12) {
                hourOfDay = hourOfDay - 12;
            }
        }
        return String.format("%02d:%02d " + am_pm, hourOfDay, minute);
    }

    private void showTimePickerDialog(TimePickerDialog.OnTimeSetListener listener) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(
                listener,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        dpd.show(((Activity) mContext).getFragmentManager(), "Choose the Time");
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.length : 0);
    }

    @Override
    public void onClick(final View v) {

    }

    public void loadStatusList() {
        itemList = TimelyStatusUtils.getTimelyStatusList(mContext);
        if (itemList == null) {
            itemList = new TimelyStatus[3];

            TimelyStatus status1 = new TimelyStatus();
            status1.setName("Sleeping");
            status1.setIcon(R.drawable.sleeping);
            status1.setStartHour(-1);
            status1.setStartMin(-1);
            status1.setEndHour(-1);
            status1.setEndMin(-1);
            itemList[0] = status1;

            TimelyStatus status2 = new TimelyStatus();
            status2.setName("At Office");
            status2.setIcon(R.drawable.at_office);
            status2.setStartHour(-1);
            status2.setStartMin(-1);
            status2.setEndHour(-1);
            status2.setEndMin(-1);
            itemList[1] = status2;

            TimelyStatus status3 = new TimelyStatus();
            status3.setName("In DND mode");
            status3.setIcon(R.drawable.dnd);
            status3.setStartHour(-1);
            status3.setStartMin(-1);
            status3.setEndHour(-1);
            status3.setEndMin(-1);
            itemList[2] = status3;
        }
        notifyDataSetChanged();
    }

    public TimelyStatus[] getItemList() {
        return itemList;
    }

    public void setTimelyAlarm(Context mContext, int requestCode, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra("alarm_id", requestCode);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.timely_status_item_icon)
        ImageView icon;

        @InjectView(R.id.timely_status_item_name)
        TextView name;

        @InjectView(R.id.timely_status_item_start_time)
        TextView start;

        @InjectView(R.id.timely_status_item_end_time)
        TextView end;

        @InjectView(R.id.timely_status_item_switch)
        SwitchCompat enabled;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
