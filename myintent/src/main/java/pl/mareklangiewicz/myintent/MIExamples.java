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
        EXAMPLE_COMMANDS.add("say something funny");
        EXAMPLE_COMMANDS.add("say something smart");
        EXAMPLE_COMMANDS.add("exit");
        //TODO LATER: add some good examples

        EXAMPLE_RULES.addAll(MyCommands.DEFAULT_EXAMPLE_RULES);

        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^my name is (\\w+)\\b.*",
                "say Hi $1."
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^what's your name\\b.*",
                "say Bond. James Bond."
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^hey you\\b.*",
                "say Are you talking to me?"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^silence\\b.*",
                "say I kill you!"
        ));
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
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "exit",
                "",
                "^((exit)|(quit)|(finish))\\b",
                "start custom action exit"
        ));
        //TODO LATER: add some smart example nonintrusive user rules
    }
}
