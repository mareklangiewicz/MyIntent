package pl.mareklangiewicz.myintent;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pl.mareklangiewicz.myfragments.MyFragment;

/**
 * Created by Marek Langiewicz on 12.11.15.
 */
public class MICmdListFragment extends MyFragment {

    private @Nullable RecyclerView mRecyclerView;
    private final CmdAdapter mAdapter = new CmdAdapter();

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); // just for logging

        View rootView = inflater.inflate(R.layout.mi_cmd_list_fragment, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.mi_cmd_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        //noinspection ConstantConditions
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        //noinspection ConstantConditions
        mRecyclerView.setAdapter(null);
        mRecyclerView = null;
        super.onDestroyView();
    }

    public void setCommands(List<String> commands) {
        mAdapter.setCommands(commands);
    }
    public void setImageRes(@DrawableRes int res) { mAdapter.setImageRes(res); }

    public @Nullable List<String> getCommands() { return mAdapter.getCommands(); }
}

