// [ vim: set tabstop=2 shiftwidth=2 expandtab : ] 


function a(addr, opt_name) { return noh.a({href:addr}, opt_name === undefined ? addr : opt_name); }

function la(addr, opt_name) { return noh.li(a(addr, opt_name)); }

function cmd2url(cmd) { return "http://mareklangiewicz.pl/mi.html#" + cmd.replace(/ /g, ";"); }

function cmd2a(cmd) { return a(cmd2url(cmd), cmd); }

function cmd2la(cmd) { return la(cmd2url(cmd), cmd); }

function ex(cmd, desc) {
  return noh.li(
    cmd2a(cmd),
    noh.p(desc)
  )
}

function isNativeApp() { return /Intent\/[0-9\.]+$/.test(navigator.userAgent); }


function init() {
  var body = mi_body();
  body.attachToDOM(document.body);
}


$(document).ready(init);


function mi_body() {

  var body = noh.div(
    noh.div(


      noh.h1("My Intent"),

      noh.p('An app that allows user to start ', noh.b('any'), ' android ', noh.b('intent'), ' easily.'),

      window.location.hash.length < 2
      ?
      noh.p(
        "You can install it from ", a("https://play.google.com/store/search?q=My%20Intent", "Google Play") // TODO LATER: real link!!!
      )
      :
      noh.p(
        noh.b("WARNING:"), " This page should be opened by My Intent app. Please install it from ",
        a("https://play.google.com/store/search?q=My%20Intent", "Google Play"), // TODO LATER: real link!!!
        ", and use it to open this page correctly."
      ),

      noh.p(
        'My Intent uses so called ', noh.b('commands'),
        ' - text sentences that can represent any intent with any action, data, category, extras etc..'
      ),

      noh.p(
        'You should understand android intent mechanism to use this app ',
        a('http://developer.android.com/guide/components/intents-filters.html', '(more info)'),
        '.'
      ),
      noh.p('Some example commands are:',
        noh.ul(
          cmd2la("start activity action android.intent.action.VIEW data http://google.com"),
          cmd2la('start activity action android.settings.DISPLAY_SETTINGS'),
          cmd2la('start activity action android.intent.action.INSERT type vnd.android.cursor.dir/contact'),
          cmd2la('start activity action android.intent.action.SET_TIMER extra integer android.intent.extra.alarm.LENGTH 13')
        ),
        'These are pretty long, but My Intent allows to use ', noh.b('rules'), ' to transform commands, ',
        'so for example the following commands will perform exactly the same as the ones above:',
        noh.ul(
          cmd2la('data http://google.com'),
          cmd2la('settings display'),
          cmd2la('action insert type contacts'),
          cmd2la('set timer 13')
        )
      ),
      noh.p('More about rules (how they work and how user can define his own) later.'),
      noh.p('Commands can be started in many different ways:',
        noh.ul(
          noh.li('by typing the command in app main text field, and pressing "play";'),
          noh.li('by pressing the red microphone icon and just saying the command;'),
          noh.li('by selecting any of recent/example commands listed in the app;'),
          noh.li('by pressing the search button and finding some recent/example command;'),
          noh.li('by using Google Now and saying something like: Ok Google, search for [some command] on my intent;'),
          noh.li('by clicking on special links in internet browser or any other app'),
          noh.li('TODO SOMEDAY: by creating small widgets that launch given command'),
          noh.li('TODO SOMEDAY: by using the lock screen widgets than enables speech recognition'),
          noh.li('TODO SOMEDAY: by tapping your smartphone with specific patterns :-)')
        ),
        "but first lets see how commands are structured..."
      ),
      noh.h3('Command structure'),
      noh.p(
        'There are six kinds of commands defined by word after the "start" keyword:',
        noh.ul(
          noh.li('start activity'),
          noh.li('start service'),
          noh.li('start broadcast'),
          noh.li('start fragment'),
          noh.li('start custom'),
          noh.li('start nothing')
        )
      ),
      noh.h4('start activity'),
      noh.p(
          'This is the most common kind of command. It constructs the ', noh.b('intent'),
          ' as specified by the rest of the command, and starts an activity with it. ',
          'details: ',
          a(
            'http://developer.android.com/reference/android/content/Context.html#startActivity(android.content.Intent)',
            'Context.startActivity(Intent)'
          )
      ),
      noh.p(
        'The "start activity" command is a default kind of command, so you can skip the "start activity" part. ',
        'For example these two commands are equivalent:',
        noh.ul(
          cmd2la('start activity action insert type contacts'),
          cmd2la('action insert type contacts')
        )
      ),
      noh.p(
        'You just need to specify the rest of the command which defines the intent. ',
        'Go to ', noh.b('intent specification'), ' below, to learn how to specify intent parameters.'
      ),



      noh.h4('start service'),
      noh.p(
        'This kind of command starts an android service using the ',
        a('http://developer.android.com/reference/android/content/Context.html#startService(android.content.Intent)', 'Context.startService(Intent)'),
        ' method, with specified intent.'
      ),

      noh.h4('start broadcast'),
      noh.p(
        'This kind of command sends a broadcast intent using the ',
        a('http://developer.android.com/reference/android/content/Context.html#sendBroadcast(android.content.Intent)', 'Context.sendBroadcast(Intent)'),
        ' method, with specified intent.'
      ),

      noh.h4('start fragment'),
      'This special kind of command does NOT construct or launch any android intent. ',
      'It replaces main app fragment with new fragment of given class. Mostly for internal usage.',


      noh.h4('start custom'),
      'Custom commands are handled differently depending on specified action:',
      noh.ul(
        noh.li( noh.b('start custom action listen'), ' - starts a speech recognizer.' ),
        noh.li( noh.b('start custom action say data [sentence]'), ' - uses TTS to say out loud any given sentence.' ),
        noh.li( noh.b('start custom action exit'), ' - exits the app.' )
      ),


      noh.h4('start nothing'),
      noh.p('It just does nothing. And the rest of the command is ignored too. It is here just for tests...'),


      noh.h3('Intent specification'),
      noh.p(
        'The intent specification can contain a few different segments: ',
        noh.ul(
          noh.li(
            noh.b('action'), ' - an action name e.g. "action android.intent.action.INSERT" ',
            'The default action is "android.intent.action.VIEW", so you can skip this segment if you want the default.'
          ),
          noh.li(
            noh.b('data'), ' - a data URI e.g. "data tel:123456789"'
          ),
          noh.li(
            noh.b('type'), ' - a mime type e.g. "type text/plain"'
          ),
          noh.li(
            noh.b('category'), ' - a category name e.g. "category android.intent.category.APP_BROWSER"'
          ),
          noh.li(
            noh.b('extra'),
            ' - one or more extra intent parameter. The format is: "extra [type] [key] [value]". ',
            'Supported extra types are: string, boolean, byte, char, double, float, integer, long, short. ',
            'Check out ', noh.b('example commands'), ' at the end of this document.'
          ),
          noh.li(noh.b('package'), ' - a package name'),
          noh.li(noh.b('component'), ' - a component name'),
          noh.li(noh.b('flags'), ' - an intent flags as a decimal number e.g. "flags 12"'),
          noh.li(noh.b('scheme')),
          noh.li(noh.b('bounds'))
        )

      ),


      noh.h3('How to start a command'),
      noh.p('Commands can be started in many different ways:',
        noh.ul(
          noh.li('by typing the command in app main text field, and pressing "play";'),
          noh.li('by pressing the red microphone icon and just saying the command;'),
          noh.li('by selecting any of recent/example commands listed in the app;'),
          noh.li('by pressing the search button and finding some recent/example command;'),
          noh.li('by using Google Now and saying something like: Ok Google, search for [some command] on my intent;'),
          noh.li('by clicking on special links in internet browser or any other app'),
          noh.li('more to come.. (like tapping your smartphone with specific patterns; adding special home screen shortcuts)')
        ),
        'You can just try the app to figure these features by yourself. One functionality that needs explanation is:'
       ),
       noh.h4('Special links'),
       noh.p(
        'My Intent app is registered as a default app to open all links which start with the: "http://mareklangiewicz.pl/mi.html#" prefix. ',
        'Anything after that "#" character is treated as a command (you can use semicolons in commands instead of spaces).'
      ),
      noh.p(
        'Place such link anywhere you want (web page, hangout/skype message, google text document, etc..), and it will start given command immediately ',
        'every time you click on it. '
      ),
      noh.p(
        'For example you can send a message: "http://mareklangiewicz.pl/mi.html#call;123456789" to someone with MyIntent app, ',
        'and his smartphone will start calling given number if he click on it. Another use case of special links can be in google docs. ',
        'Lets say you are creating a song book witch chords and lyrics for your favorite songs. You can then add special links to it that will ',
        'play a particular song using your favourite media player. You can send someone a special link that will add your birthday to his calendar :-) ',
        'Another use case would be for android developers, to test ',
        'how their app react to different intents. In fact almost all links in this documentation page are special links, so you can test those easily. ',
        'Check out ', noh.b('example commands'), ' at the end of this document, to see some samples of what is possible.'
       ),

      noh.h3('Rules'),
      noh.p(
        'MyIntent uses Java Regular Expressions to transform any command, or any part of command freely. ',
        'This allows to use shorter commands, and to define commands that can be easily recognized by voice recognition engine. '
      ),
      noh.p(
        'There is a lot of predefined RE Rules, but user can add his own and modify, reorder or delete them. ',
        'Check out the application itself to see what RE Rules are there and how are they ordered and grouped. ',
        'There are several groups of rules. Every command is matched in order with every rule, but only in groups that match. '
      ),
      noh.p(
        'You can see in app itself how any command is matched and transformed by watching log messages displayed in app. ',
        'For now only rules in "user" group are editable by user. ',
        'The easiest way to understand it all is just to open the app and play with it. :-)'
      ),
      noh.h3('Example commands'),
      noh.ul(
        ex("data http://mareklangiewicz.pl", 'Just "view" any given data URI with default app (or ask the user for app to choose). '),
        ex('settings wi-fi', 'Starts the Wi-Fi settings.'),
        ex('dial 123456789', 'Starts the phone app with given number entered - ready to call.'),
        ex('call 123456789', 'Starts calling given phone number immediately.'),
        ex('search king', 'Searches for given phrase using any app user choose.'),
        ex('web search rxkotlin', 'Searches for given phrase using web browser'),
        ex('wake me up at 7 30', 'Sets an alarm to 7:30 am using default alarm app.'),
        ex('set alarm to 9 15', 'Sets an alarm to 9:15 am.'),
        ex('set timer for 40', 'Sets a timer for 40 seconds using default timer app.'),
        ex('take a picture', 'Starts a camera app ready to take a picture.'),
        ex('record a movie', 'Starts a camera app ready to record a video.'),
        ex('note buy a cat food', 'Adds given note to default note taking app.'),
        ex('start nothing', 'Does nothing.'),
        ex('settings bluetooth', ''),
        ex('settings roaming', ''),
        ex('settings display', ''),
        ex('settings internal storage', ''),
        ex('settings location', ''),
        ex('settings apps', ''),
        ex('settings memory card', ''),
        ex('settings network', ''),
        ex('settings nfc', ''),
        ex('settings privacy', ''),
        ex('settings search', ''),
        ex('settings security', ''),
        ex('settings sound', ''),
        ex('settings sync', ''),
        ex('settings wi-fi', ''),
        ex('settings wireless', ''),
        ex('settings', ''),
        ex('start custom action listen', ''),
        ex('start custom action say data hello world!', ''),
        ex('say have a nice day!', ''),
        ex('say something funny', ''),
        ex('say something smart', ''),
        ex('say something positive', ''),
        ex('say something motivational', ''),
        ex('say time', ''),
        ex('say date', ''),
        ex('play some drums', ''),
        ex('play some more drums', ''),
        ex('teleport to beach', ''),
        ex('teleport to new york', ''),
        ex('teleport to my house', ''),
        ex('data google.navigation:q=wroclaw', ''),
        ex('data geo:0,0?q=mount+everest', ''),
        ex('my name is john', ''),
        ex('what\'s your name', ''),
        ex('hey you', ''),
        ex('silence', ''),
        ex('translate duck', ''),
        ex('exit', ''),
        ex('action edit data content://contacts/people/1', ''),
        ex('action show alarms', ''),
        ex('action insert type contacts', ''),
        ex('action insert data calendar events', ''),
        ex('action insert data calendar events extra string title iron maiden concert extra string eventLocation stadion wroclaw poland', ''),
        ex('action insert type contacts extra string name Satan extra string phone 666', ''),
        ex('action main category music', ''),
        ex('action main category browser', ''),
        ex('action main category calculator', ''),
        ex('action main category calendar', ''),
        ex('action main category contacts', ''),
        ex('action main category email', ''),
        ex('action main category gallery', ''),
        ex('action main category maps', ''),
        ex('action main category market', ''),
        ex('action main category messaging', '')
      )
    ).css("margin", 10)
  ).addclass("smooth");
  return body;
}




