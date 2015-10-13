// [ vim: set tabstop=2 shiftwidth=2 expandtab : ] 


MP3EX = "/storage/emulated/0/Music/Wintersun/2004 - Wintersun/06 - Starchild.mp3";
GEOEX = "geo:47.6, -122.3";



function init() {
  var body = mi_body();
  body.attachToDOM(document.body);
}


$(document).ready(init);


function mi_body() {

  var cmdline = noh.cmdline(40);
  var logger = noh.log.reel(15, 240000);
  //logger = noh.log.limitlen(logger, 120);
  verbose(logger);
  noh.log.l2c(
    noh.log.multi([
      noh.log.c2l(window.console),
      logger
   ])
  ).install();
  var body = noh.div(
    noh.p(
      noh.fancy(noh.h1("MyIntent")),
      noh.ul(
        la("file:/sdcard/"),
        la("file:///sdcard/"),
        la(window.location.origin + window.location.pathname + "#task=service;action=MAREK_ACTION;data=some_marek_data"),
        la(window.location.origin + window.location.pathname + "#task=activity;action=MAREK_ACTION;data=some_marek_data"),
        la(window.location.origin + window.location.pathname + "#task=broadcast;action=MAREK_ACTION;data=some_marek_data"),
        la(window.location.origin + window.location.pathname + "#action=view;data=" + encodeURIComponent(GEOEX) + ""),
        la(window.location.origin + window.location.pathname + "#action=android.media.action.MEDIA_PLAY_FROM_SEARCH;data=" + encodeURIComponent(MP3EX)),
        la(window.location.origin + window.location.pathname + "#action=set_alarm;end"),
        la(window.location.origin + window.location.pathname + "#action=view;data=http://www.google.pl"),
        la("http://mi#action=view;data=" + encodeURIComponent(GEOEX)),
        la("mi://#action=view;data=" + encodeURIComponent(GEOEX)),
        noh.li(noh.a({href:'https://docs.google.com/document/d/1tF9x6qF8OkZT9V1bP_fChxCy4ESt4HsAkmShcTDA_Ck/edit?usp=sharing'}, 'More examples in google text document'))
      ),
      noh.p(
        noh.h3("Intent:"),
        noh.ul(
          lparam('task'),
          lparam('action'),
          lparam('data'),
          lparam('extra')
        )
      ),
      noh.p(
        logger,
        cmdline
      )
    ).css("margin", 20)
  ).addclass("smooth");
  var welcome = [
    'Welcome to Intent web page.',
    'Enter: help() to get some help.',
    'Enter: todo() to see what has to be done yet..',
    'Enter: tests() to run some tests..'
  ];
  if(isNativeApp()) {
    welcome.push("NATIVE INTENT APP DETECTED! TODO: change this page for this special case!");
  }
  slow_log(welcome, 500);
  window.setTimeout(function() {cmdline[0].$.focus();}, 200);
  return body;
}

// User commands:

function help() {
  slow_log([
    'Help for MyIntent app:',
    'MyIntent is a small android application that can translate appropriate http links to any chosen android intents.',
    'You can place such special links on your web page, and it will fire appropriate intent,',
    'when you click on it - using android device with Intent app installed.',
    'This web page just provides some information and helps to generate proper links for the main android app.',
    'You have to install MyIntent android application to use it. TODO: link to MyIntent on play store',
    'You can invoke function "intent" to generate proper link for specific intent. Examples:',
    'intent({ action:"android.intent.action.VIEW", data:"geo:51.05,17.22" })',
    'intent({ action:"android.intent.action.SET_ALARM" })',
    'intent({ action:"android.intent.action.SET_ALARM", extra:{"android.intent.extra.alarm.HOUR":7, "android.intent.extra.alarm.MINUTES":35} })',
    'Enter: todo() to see what has to be done yet..',
    'Enter: tests() to run some tests..'
  ], 200);
}

function todo() {
  slow_log([
    'TODO: rewrite this whole page!!!',
    'TODO: more examples',
    'TODO: create some real documentation',
    'TODO SOMEDAY: forwarding intents to special desktop application(launcher) - especially voice commands, so a phone can listen for commands for desktop computer..',
    "(for example user could create some rules to create simple voice gateway for new commands like: play intent ....)"
  ], 200);
}

function tests() {

  slow_log([
    'TODO: run some tests...',
    "file:" + MP3EX,
    "file://" + MP3EX,
    GEOEX,
    window.location.origin + window.location.pathname + "#action=view;data=" + encodeURIComponent(GEOEX),
    window.location.origin + window.location.pathname + "#action=android.media.action.MEDIA_PLAY_FROM_SEARCH;data=" + encodeURIComponent(MP3EX),
    window.location.origin + window.location.pathname + "#action=set_alarm",
    window.location.origin + window.location.pathname + "#action=view;data=http://www.google.pl",
    "http://miaction=view;data=" + encodeURIComponent(GEOEX),
    "mi://action=view;data=" + encodeURIComponent(GEOEX)
  ], 200);
}









function verbose(logger) {
  logger.lv_ = logger.log;
  logger.log = function(classes, data) {
    for(var i = 0; i < data.length; ++i) {
      if((typeof data[i] !== "string") && noh.arr.isArrayLike(data[i])) {
        logger.lv_(classes, ['[']);
        for(var j = 0; j < data[i].length; ++j) {
          logger.lv_(classes, ['- ' + data[i][j]]);
          if(j > 25) {
            logger.lv_(classes, ['...']);
            break;
          }
        }
        logger.lv_(classes, [']']);
      }
      else
        logger.lv_(classes, [data[i]]);
    }
  };
}

function a(addr) { return noh.a({href:addr}, addr); }
function la(addr) { return noh.li(a(addr)); }

function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
            return sParameterName[1];
    }
}          

function lparam(name) { return noh.li(name+': ' + decodeURIComponent(getUrlParameter(name))); }




function slow_log(lines, delay) {
  var idx = 0;
  var interval = undefined;
  var callback = function() {
    if(idx >= lines.length) {
      window.clearInterval(interval);
      interval = undefined;
      return;
    }
    console.log(lines[idx]);
    idx ++;
  };

  interval = window.setInterval(callback, delay);
}




function isNativeApp() {
  return /Intent\/[0-9\.]+$/.test(navigator.userAgent);
}

function intent(options) {
  var prefix = "http://mareklangiewicz.pl/mi.html";
  var params = [];
  for(var op in options)
    if(op == "extra")
      for(var ex in options.extra)
        params.push("extra_" + encodeURIComponent(ex) + "=" + encodeURIComponent("" + options.extra[ex]));
    else
      params.push("" + encodeURIComponent(op) + "=" + encodeURIComponent("" + options[op]));

  var url = prefix;
  if(params.length > 0)
    url += '?' + params[0];

  for(var i = 1; i < params.length; ++i)
    url += '&' + params[i];

  return url;
}

