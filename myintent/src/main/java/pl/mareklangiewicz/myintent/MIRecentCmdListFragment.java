package pl.mareklangiewicz.myintent;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marek Langiewicz on 12.11.15.
 */
public class MIRecentCmdListFragment extends MICmdListFragment {
    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setImageRes(R.drawable.ic_youtube_searched_for_black_24dp);
        List<String> commands = new ArrayList<>();
        MIContract.CmdRecent.load(getActivity(), commands);
        setCommands(commands);
    }
}
