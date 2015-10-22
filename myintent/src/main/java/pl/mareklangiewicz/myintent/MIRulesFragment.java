package pl.mareklangiewicz.myintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.mareklangiewicz.myfragments.MyFragment;
import pl.mareklangiewicz.myutils.MyCommands;

/**
 * Created by Marek Langiewicz on 14.10.15.
 */
public class MIRulesFragment extends MyFragment {

    private @Nullable RecyclerView mRecyclerView;
    private final REGroupsAdapter mAdapter = new REGroupsAdapter(MyCommands.RE_RULES);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        View rootView = inflater.inflate(R.layout.mi_rules_fragment, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.mi_rules_recycler_view);

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
}
