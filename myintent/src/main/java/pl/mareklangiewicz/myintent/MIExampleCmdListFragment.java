package pl.mareklangiewicz.myintent;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marek Langiewicz on 12.11.15.
 */
public class MIExampleCmdListFragment extends MICmdListFragment {
    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setImageRes(R.drawable.ic_find_replace_black_24dp);
        List<String> commands = new ArrayList<>();
        MIContract.CmdExample.load(getActivity(), commands);
        setCommands(commands);
    }
}
