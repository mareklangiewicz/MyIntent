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
        EXAMPLE_COMMANDS.add("play some drums");
        EXAMPLE_COMMANDS.add("play some more drums");
        EXAMPLE_COMMANDS.add("exit");
        //TODO LATER: add some good examples

        EXAMPLE_RULES.addAll(MyCommands.DEFAULT_EXAMPLE_RULES);

        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^take( a)? (photo|picture|selfie|image)",
                "action android.media.action.STILL_IMAGE_CAMERA"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^record a? (movie|video|film)",
                "action android.media.action.VIDEO_CAMERA"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^dial (.*)$",
                "action dial data tel:$1"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^call (.*)$",
                "action call data tel:$1"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^play (some )?drums",
                "type application/ogg data http://mareklangiewicz.pl/homepage_2007/muzyka/hydrokoza.ogg"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^play (some )?more drums",
                "type application/ogg data http://mareklangiewicz.pl/homepage_2007/muzyka/drum.ogg"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^note (.*)$",
                "action create note type text/plain extra text $1"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^search (.*)$",
                "action search extra string query $1"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^web search (.*)$",
                "action web search extra string query $1"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^teleport to beach$",
                "data google.streetview:cbll=-23.3036925,151.9150093"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^teleport to new york$",
                "data google.streetview:cbll=40.7584954,-73.9851351"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(
                true,
                "",
                "",
                "^teleport to my house$",
                "data google.streetview:cbll=48.1848573,16.3122329"
        ));
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
                "^(exit|quit|finish)\\b",
                "start custom action exit"
        ));
        //TODO LATER: add some smart example nonintrusive user rules
    }
}
