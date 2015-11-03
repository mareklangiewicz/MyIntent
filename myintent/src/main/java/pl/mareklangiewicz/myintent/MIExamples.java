package pl.mareklangiewicz.myintent;

import java.util.ArrayList;
import java.util.List;

import pl.mareklangiewicz.myutils.MyCommands;

/**
 * Created by Marek Langiewicz on 31.10.15.
 */
public class MIExamples {

    static public final List<String> EXAMPLE_COMMANDS = new ArrayList<>();
    static public final List<MyCommands.RERule> EXAMPLE_RULES = new ArrayList<>();

    static {
        EXAMPLE_COMMANDS.addAll(MyCommands.DEFAULT_EXAMPLE_COMMANDS);
        EXAMPLE_COMMANDS.add("start custom action listen");
        EXAMPLE_COMMANDS.add("start custom action say data hello world!");
        EXAMPLE_COMMANDS.add("say have a nice day!");
        //TODO LATER: add some good examples

        EXAMPLE_RULES.addAll(MyCommands.DEFAULT_EXAMPLE_RULES);
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "say",
                "",
                "^say ",
                "start custom action say data "
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "listen",
                "",
                "^listen",
                "start custom action listen"
        ));
        //TODO LATER: add some smart example nonintrusive user rules
    }
}
