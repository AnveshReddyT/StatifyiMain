package statifyi.com.statifyi.fragment;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import statifyi.com.statifyi.R;
import statifyi.com.statifyi.adapter.TimelyStatusAdapter;
import statifyi.com.statifyi.utils.GAUtils;

public class TimelyStatusSettingsFragment extends Fragment {

    @InjectView(R.id.timely_status_recycler)
    RecyclerView listView;

    private TimelyStatusAdapter timelyStatusAdapter;

    public TimelyStatusSettingsFragment() {
        // Required empty public constructor
    }

    public static TimelyStatusSettingsFragment newInstance(String param1, String param2) {
        TimelyStatusSettingsFragment fragment = new TimelyStatusSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GAUtils.sendScreenView(getActivity().getApplicationContext(), TimelyStatusSettingsFragment.class.getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timelystatus, container, false);
        ButterKnife.inject(this, root);

        listView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.recycler_divider));
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadStatusList();

        return root;
    }

    private void loadStatusList() {
        timelyStatusAdapter = new TimelyStatusAdapter(getActivity());
        listView.setAdapter(timelyStatusAdapter);
        timelyStatusAdapter.notifyDataSetChanged();
    }

    class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

}
