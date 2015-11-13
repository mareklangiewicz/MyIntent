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
        EXAMPLE_COMMANDS.add("settings bluetooth");
        EXAMPLE_COMMANDS.add("settings roaming");
        EXAMPLE_COMMANDS.add("settings display");
        EXAMPLE_COMMANDS.add("settings internal storage");
        EXAMPLE_COMMANDS.add("settings location");
        EXAMPLE_COMMANDS.add("settings apps");
        EXAMPLE_COMMANDS.add("settings memory card");
        EXAMPLE_COMMANDS.add("settings network");
        EXAMPLE_COMMANDS.add("settings nfc");
        EXAMPLE_COMMANDS.add("settings privacy");
        EXAMPLE_COMMANDS.add("settings search");
        EXAMPLE_COMMANDS.add("settings security");
        EXAMPLE_COMMANDS.add("settings sound");
        EXAMPLE_COMMANDS.add("settings sync");
        EXAMPLE_COMMANDS.add("settings wi-?fi");
        EXAMPLE_COMMANDS.add("settings wireless");
        EXAMPLE_COMMANDS.add("settings");
        EXAMPLE_COMMANDS.add("start custom action listen");
        EXAMPLE_COMMANDS.add("start custom action say data hello world!");
        EXAMPLE_COMMANDS.add("say have a nice day!");
        EXAMPLE_COMMANDS.add("say something funny");
        EXAMPLE_COMMANDS.add("say something smart");
        EXAMPLE_COMMANDS.add("say something positive");
        EXAMPLE_COMMANDS.add("say something motivational");
        EXAMPLE_COMMANDS.add("say time");
        EXAMPLE_COMMANDS.add("say date");
        EXAMPLE_COMMANDS.add("play some drums");
        EXAMPLE_COMMANDS.add("play some more drums");
        EXAMPLE_COMMANDS.add("teleport to beach");
        EXAMPLE_COMMANDS.add("teleport to new york");
        EXAMPLE_COMMANDS.add("teleport to my house");
        EXAMPLE_COMMANDS.add("data google.navigation:q=wroclaw");
        EXAMPLE_COMMANDS.add("data geo:0,0?q=mount+everest");
        EXAMPLE_COMMANDS.add("my name is john");
        EXAMPLE_COMMANDS.add("what's your name");
        EXAMPLE_COMMANDS.add("hey you");
        EXAMPLE_COMMANDS.add("silence");
        EXAMPLE_COMMANDS.add("exit");
        EXAMPLE_COMMANDS.add("action edit data content://contacts/people/1");
        EXAMPLE_COMMANDS.add("action show alarms");
        EXAMPLE_COMMANDS.add("action insert type contacts");
        EXAMPLE_COMMANDS.add("action insert data calendar events");
        EXAMPLE_COMMANDS.add("action insert data calendar events extra string title iron maiden concert extra string eventLocation stadion wroclaw poland");
        EXAMPLE_COMMANDS.add("action insert type contacts extra string name Satan extra string phone 666");
        EXAMPLE_COMMANDS.add("action main category music");
        EXAMPLE_COMMANDS.add("action main category browser");
        EXAMPLE_COMMANDS.add("action main category calculator");
        EXAMPLE_COMMANDS.add("action main category calendar");
        EXAMPLE_COMMANDS.add("action main category contacts");
        EXAMPLE_COMMANDS.add("action main category email");
        EXAMPLE_COMMANDS.add("action main category gallery");
        EXAMPLE_COMMANDS.add("action main category maps");
        EXAMPLE_COMMANDS.add("action main category market");
        EXAMPLE_COMMANDS.add("action main category messaging");

        EXAMPLE_RULES.addAll(MyCommands.DEFAULT_EXAMPLE_RULES);

        EXAMPLE_RULES.add(new MyCommands.RERule(true, "", "",
                "^play (some )?drums",
                "type application/ogg data http://mareklangiewicz.pl/homepage_2007/muzyka/hydrokoza.ogg"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(true, "", "",
                "^play (some )?more drums",
                "type application/ogg data http://mareklangiewicz.pl/homepage_2007/muzyka/drum.ogg"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(true, "", "",
                "^teleport to beach$",
                "data google.streetview:cbll=-23.3036925,151.9150093"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(true, "", "",
                "^teleport to new york$",
                "data google.streetview:cbll=40.7584954,-73.9851351"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(true, "", "",
                "^teleport to my house$",
                "data google.streetview:cbll=48.1848573,16.3122329"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(true, "", "",
                "^my name is (\\w+)\\b.*",
                "say Hi $1."
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(true, "", "",
                "^what's your name\\b.*",
                "say Bond. James Bond."
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(true, "", "",
                "^hey you\\b.*",
                "say Are you talking to me?"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(true, "", "",
                "^silence\\b.*",
                "say I kill you!"
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(true, "say", "",
                "^say ",
                "start custom action say data "
        ));
        EXAMPLE_RULES.add(new MyCommands.RERule(true, "exit", "",
                "^(exit|quit|finish)\\b",
                "start custom action exit"
        ));
    }
}
