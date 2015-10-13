// [ vim: set tabstop=2 shiftwidth=2 expandtab : ] 

/**                                           
 * @author marek.langiewicz@gmail.com (Marek Langiewicz)
 * @fileoverview
 * <p>
 * This is the <strong>NOH</strong> (NO HTML) library.
 * </p>
 * <p>
 * It allows to create the html documents dynamically in pure JavaScript (with almost no html code at all)
 * It contains a kind of a wrappers to DOM hierarchy.
 * We have a function for every html element like: {@linkcode table, tr, td, div, span} etc..;
 * but also we have functions that constructs many specialized and more complex elements that have some dynamic behaviour
 * implemented (like {@linkcode menu, oneof, bar, logger}, and more).
 * TODO: implement some srccode, some logger and some tester.
 * </p>
 * <p>
 * Please check the files: {@link noh_example.js} (and {@link noh_example.html}) for full (but simple) working example.
 * Main documentation with introduction and examples is available here: {@link index.html|NOH library documentation}
 * Additional API documentation generated with {@link http://usejsdoc.org/|jsdoc3} is available here: {@link apidoc/index.html|NOH API documentation} TODO
 * </p>
 * <p>
 * NOH library depends on jQuery. TODO: Limit jQuery usage for NOH to be able to work with SVG or other elements (not only html)
 * {@linkcode http://stackoverflow.com/questions/3642035/jquerys-append-not-working-with-svg-element|stackoverflow}
 * </p>
 *
 * Released under the MIT license.
 */

/*
 * Example:
 *
 * Instead of HTML code like:
 * <div id="someid">
 *     <h2>EXAMPLE</h2>
 *     <p>
 *         <h4>Some header</h4>
 *         Some content
 *     </p>
 *     <p>
 *         <h4>Other header</h4>
 *         Other content
 *     </p>
 * </div>
 *
 * We write JS code like:
 * noh.div({id:"someid"},
 *     noh.h2("EXAMPLE"),
 *     noh.p(noh.h4("Some header"),"Some content"),
 *     noh.p(noh.h4("Other header"),"Other content")
 * )
 */


/**
 * @namespace Main NOH library namespace.
 */
var noh = {};




/** @typedef {!Object.<string, string>} */
noh.Attrs;

/** @typedef {Array.<!noh.Node>} */
noh.Nodes;

/** @typedef {(noh.Attrs|noh.Node|string|Array.<noh.AttrsAndNodes>|Arguments|undefined)} */
noh.AttrsAndNodes; 

/** @typedef {{attrs: noh.Attrs, nodes:noh.Nodes}} */
noh.RecAttrsAndNodes;



/**
 * @param {string=} opt_msg
 * @constructor
 * @extends {Error}
 */
noh.NotImplementedError = function(opt_msg) { if(opt_msg) this.message = opt_msg; };
noh.NotImplementedError.prototype = new Error("Not implemented");

/**
 * @param {string=} opt_msg
 * @constructor
 * @extends {noh.NotImplementedError}
 */
noh.NotSupportedError = function(opt_msg) { if(opt_msg) this.message = opt_msg; };
noh.NotSupportedError.prototype = new noh.NotImplementedError("Operation not supported");




/**
 * The list of HTML tags (used for automatic helper function generation) (read only)
 * @const
 */
noh.TAGS = [
  //TODO: this is too big - remove unwanted tags later (we want probably only those inside body)..
  //TODO: add SVG related tags (maybe MathML related tags too??) (but maybe in another file(s))
  "html", "head", "body", "script", "meta", "title", "link",
  "div", "p", "span", "a", "img", "br", "hr", "em", "i", "strong",
  "table", "tr", "th", "td", "thead", "tbody", "tfoot", "colgroup", "col",
  "ul", "ol", "li", 
  "dl", "dt", "dd",
  "h1", "h2", "h3", "h4", "h5", "h6",
  "form", "fieldset", "input", "textarea", "label", "select", "option", "b", "pre", "code", "button",
  "kbd"
];




// We can not generate all these functions dynamically, because we need closure compiler to correctly check types, shorten names, and remove not needed functions.

/** @param {...noh.AttrsAndNodes} var_args */ noh.body       = function(var_args) { return new noh.Element("body"      , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.link       = function(var_args) { return new noh.Element("link"      , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.div        = function(var_args) { return new noh.Element("div"       , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.p          = function(var_args) { return new noh.Element("p"         , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.span       = function(var_args) { return new noh.Element("span"      , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.a          = function(var_args) { return new noh.Element("a"         , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.img        = function(var_args) { return new noh.Element("img"       , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.br         = function(var_args) { return new noh.Element("br"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.hr         = function(var_args) { return new noh.Element("hr"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.em         = function(var_args) { return new noh.Element("em"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.i          = function(var_args) { return new noh.Element("i"         , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.strong     = function(var_args) { return new noh.Element("strong"    , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.table      = function(var_args) { return new noh.Element("table"     , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.tr         = function(var_args) { return new noh.Element("tr"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.th         = function(var_args) { return new noh.Element("th"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.td         = function(var_args) { return new noh.Element("td"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.thead      = function(var_args) { return new noh.Element("thead"     , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.tbody      = function(var_args) { return new noh.Element("tbody"     , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.tfoot      = function(var_args) { return new noh.Element("tfoot"     , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.colgroup   = function(var_args) { return new noh.Element("colgroup"  , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.col        = function(var_args) { return new noh.Element("col"       , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.ul         = function(var_args) { return new noh.Element("ul"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.ol         = function(var_args) { return new noh.Element("ol"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.li         = function(var_args) { return new noh.Element("li"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.dl         = function(var_args) { return new noh.Element("dl"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.dt         = function(var_args) { return new noh.Element("dt"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.dd         = function(var_args) { return new noh.Element("dd"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.h1         = function(var_args) { return new noh.Element("h1"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.h2         = function(var_args) { return new noh.Element("h2"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.h3         = function(var_args) { return new noh.Element("h3"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.h4         = function(var_args) { return new noh.Element("h4"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.h5         = function(var_args) { return new noh.Element("h5"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.h6         = function(var_args) { return new noh.Element("h6"        , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.form       = function(var_args) { return new noh.Element("form"      , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.fieldset   = function(var_args) { return new noh.Element("fieldset"  , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.input      = function(var_args) { return new noh.Element("input"     , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.textarea   = function(var_args) { return new noh.Element("textarea"  , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.label      = function(var_args) { return new noh.Element("label"     , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.select     = function(var_args) { return new noh.Element("select"    , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.option     = function(var_args) { return new noh.Element("option"    , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.b          = function(var_args) { return new noh.Element("b"         , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.pre        = function(var_args) { return new noh.Element("pre"       , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.code       = function(var_args) { return new noh.Element("code"      , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.button     = function(var_args) { return new noh.Element("button"    , arguments); };
/** @param {...noh.AttrsAndNodes} var_args */ noh.kbd        = function(var_args) { return new noh.Element("kbd"       , arguments); };



/** @param {string} text */ noh.text = function(text) { return new noh.Text(text); };


/**
 * This helper function is used to parse the arguments in a clever way.
 * We want to get an object representing the attributes of created Element (like {@linkcode {href:"http://foo.com", "class":"some_css_class"..}})
 * and a list of children of new created element, where every child is a Node.
 * But we want the user to have the ability to provide this information in many different ways.
 * So this function have to guess for example:
 * <ul>
 * <li> which arguments represents the Element attributes </li>
 * <li> which are not Nodes but simple strings and have to be wrapped in Text nodes </li>
 * <li> which are single children, and which is a whole list of children (All nested Array like objects are just flattened) </li>
 * <li> which we have to ignore (user can pass some undefined arguments and we will ignore them) </li>
 * </ul>
 * Pretty examples of using this flexibility should be presented in the main documentation: {@link index.html|NOH Library documentation}
 * @param {noh.AttrsAndNodes} args Arguments to be parsed as attributes and nodes.
 * @param {number=} opt_ignore Number of elements to ignore at the beginning of args list. It is important only if args is an array-like object (default is 0)
 * @param {noh.RecAttrsAndNodes=} opt_result A result object to be extended.
 * @return {!noh.RecAttrsAndNodes} Attributes and children extracted from args.
 * @suppress {checkTypes} FIXME: how can I tell the closure compiler that in some cases an args is array-like?
 */
noh.organize = function(args, opt_ignore, opt_result) {

  var ignore = opt_ignore === undefined ? 0 : opt_ignore;
  var result = opt_result ? opt_result : {
    attrs: {},
    nodes: []
  };

  if(args instanceof noh.Node)
    result.nodes.push(args);
  else if(typeof args === "string")
    result.nodes.push(noh.text(args));
  else if(noh.arr.isArrayLike(args))
    for(var i = ignore; i < args.length; ++i)
      noh.organize(args[i], 0, result);
  else if(args instanceof Object)
    $.extend(result.attrs, args);
  else if((args === undefined)||(args === null)) {
    // do nothing.
  }
  else
    throw new TypeError("Unknown argument type: " + typeof args + " value: " + String(args));

  return result;
};


noh.arr = {};


/**
 * Inserts one array elements to the other array (at the end).
 * @param {Array} arrIn The source array.
 * @param {Array} arrOut The destination array.
 * @return {number} New length of the destination array.
 * TODO: do we need it now at all?
 */
noh.arr.push = function(arrIn, arrOut) {
  return arrOut.push.apply(arrOut, arrIn);
};


/**
 * @return {number} index of first occurrence of val in arr; or -1 if not found.
 * @param {*} val A value to find in arr.
 * @param {!Array} arr Array to find the val in.
 */
noh.arr.indexOf = function(val, arr) {
  for(var x = 0; x < arr.length; ++x)
    if(val == arr[x])
      return x;
  return -1;
};



/**
 * Converts an array to an object.
 * @param {!Array.<string>} records Array of object property names in the same order as values in arr parameter.
 * @param {Array} arr array (or null).
 * @return {Object} An object that gets it's property names from records parameter, and values from arr parameter.
 * If arr is null, the returned object will be also null.
 * TODO: do we need it now at all?
 */
noh.arr.arr2obj = function(records, arr) {
  if(arr === null) return null;
  var obj = {};
  for(var x = 0; x < records.length; ++x)
    obj[records[x]] = arr[x];
  return obj;
};

/**
 * Symetric function to the one above.
 * Also accepts null input (and returns null in that case).
 * @param {!Array.<string>} records Array of object property names that defines the order to put the obj values to returned array.
 * @param {Object} obj An input object that provides values to return in array.
 * @return {Array} An array of obj values in order defined by records parameter.
 * TODO: do we need it now at all?
 */
noh.arr.obj2arr = function(records, obj) {
  if(obj === null) return null;
  var arr = [];
  for(var x = 0; x < records.length; ++x)
    arr.push(obj[records[x]]);
  return arr;
};

/**
 * Checks if the provided object is an array or can be used as an array.
 * @param {Object=} arr An object to test.
 * @return {boolean} True if arr is an array like object
 */
noh.arr.isArrayLike = function(arr) {
  if(arr == undefined) return false;
  if(arr == null) return false;
  if(arr instanceof jQuery) return true;
  if(jQuery.isArray(arr)) return true;
  var len = arr.length;
  if(typeof(len) !== "number") return false;
  if(len == 0) return true;
  if(len < 0) return false;
  var hasOwn = Object.prototype.hasOwnProperty;
  if(!hasOwn.call(arr, 0)) return false;
  if(!hasOwn.call(arr, len - 1)) return false;
  return true;
};



noh.str = {};

/**
 * Shorten given text to given length max.
 * If text is too long it cuts it, and changes three last letters to "..."
 * @param {string} text
 * @param {number} maxlen
 * @return {string} shortened text
 */
noh.str.limitlen = function(text, maxlen) {
  if(text.length > maxlen) 
    text = text.substr(0, maxlen-3) + "...";
  return text;
};


noh.str.prefix = function(text, prefix, len) {
  while(text.length < len)
    text = prefix + text;
  return text;
};


/**
 * A base constructor for Node objects. This is base "class" for all UI objects created by NOH.
 * @constructor
 */
noh.Node = function() {

  /** Number of children of this Node. Users can not change this directly!. */
  this.length = 0;

  /**
   * @type {noh.Node}
   */
  this.parent;

  /**
   * the dom property have to be overriden in derivatives!
   * @type {!Node}
   */
  this.dom;

  /**
   * The jQuery object representing this node.
   * the $ property have to be overriden in derivatives!
   * TODO: better type description?
   * @type {!Object}
   */
  this.$;
};


/**
 * Adds a new child node at the end.
 * @param {!noh.Node} node A node to add as a last child of our node.
 * @return {!noh.Node} this (for chaining)
 */
noh.Node.prototype.add = function(node) {
  node.attachToDOM(this.dom);
  node.parent = this;
  Array.prototype.push.call(this, node);
  return this;
};

/**
 * Removes last child node.
 * @return {!noh.Node} this (for chaining)
 */
noh.Node.prototype.rem = function() {
  var node = Array.prototype.pop.call(this);
  node.parent = null;
  node.detachFromDOM(this.dom);
  return this;
};


/**
 * This dummy method is only for console to display our Node as an array..
 * TODO: implement real splice with callback for inserting and removing DOM elements;
 * then implement other array-like methods using splice. (like: pop, push, shift, unshift) 
 * @see http://stackoverflow.com/questions/6599071/array-like-objects-in-javascript
 */
noh.Node.prototype.splice = function() { throw new noh.NotSupportedError(); };


/**
 * Attaches a node to given DOM root element
 * @param {!Node} root (It's a DOM Node class (not noh.Node)
 * @return {!noh.Node} this (for chaining)
 */
noh.Node.prototype.attachToDOM = function(root) {
  root.appendChild(this.dom);
  return this;
};

/**
 * Detaches a node from given DOM root element
 * @param {!Node} root
 * @return {!noh.Node} this (for chaining)
 */
noh.Node.prototype.detachFromDOM = function(root) {
  root.removeChild(this.dom);
  return this;
};



/**
 * @param {string} text
 * @constructor
 * @extends {noh.Node}
 */
noh.Text = function(text) {
  this.text = text;
  noh.Node.call(this);
  this.dom = document.createTextNode(text);
  this.dom.noh = this;
  this.$ = $(this.dom);
};

noh.Text.prototype = new noh.Node();



/**
 * A base constructor for the DOM elements (Besides HTML elements it can be SVG or MathMl elements).
 * @param {string} tag Tag name like: div or img or table etc..
 * @param {...noh.AttrsAndNodes} var_args Attributes and children nodes. See {@linkcode noh.organize} for more detailed explanation about attributes and children parameters.
 * @constructor
 * @extends {noh.Node}
 */
noh.Element = function(tag, var_args) {
  this.tag = tag;

  noh.Node.call(this);
    
  this.dom = document.createElement(tag);
  this.dom.noh = this;
  this.$ = $(this.dom);

  var an = noh.organize(arguments, 1);

  for(var a in an.attrs)
    this.attr(a, an.attrs[a]);

  for(var i = 0; i < an.nodes.length; ++i)
    this.add(an.nodes[i]);
};

noh.Element.prototype = new noh.Node();

/**
 * Sets an element's attribute
 * @param {string} name
 * @param {string} value
 * @return {!noh.Element} this (for chaining)
 */
noh.Element.prototype.attr = function(name, value) {
  this.$.attr(name, value);
  return this;
};


/**
 * Applies the css style (just a convenient shortcut for typical jQuery method invocation)
 * @see http://api.jquery.com/css/#css2
 * @param {string} name CSS property name
 * @param {string|number} value CSS property value
 * @return {!noh.Element} this (for chaining)
 */
noh.Element.prototype.css = function(name, value) {
  this.$.css(name, value);
  return this;
};

/**
 * Attach an event handling function for one or more events to this element.
 * (just a convenient shortcut for typical jQuery method invocation)
 * @see http://api.jquery.com/on/#on-events-selector-data-handlereventObject
 * @param {string} events One or more space separated events (usually its just one word like: "click")
 * @param {function(Object=):(boolean|undefined)} handler A function to execute when the event is triggered.
 * @return {!noh.Element} this (for chaining)
 */
noh.Element.prototype.on = function(events, handler) {
  this.$.on(events, handler);
  return this;
};

/**
 * Add one or more classes to element's "class" attribute
 * @param {string} aclass One or more space-separated classes to add to the class attribute
 * @return {!noh.Element} this (for chaining)
 */
noh.Element.prototype.addclass = function(aclass) {
  this.$.addClass(aclass);
  return this;
}

noh.Element.prototype.hasclass = function(aclass) {
  return this.$.hasClass(aclass);
}

noh.Element.prototype.toggleclass = function(aclass) {
  this.$.toggleClass(aclass);
  return this;
}




/**
 * Remove one or more classes from element's "class" attribute
 * @param {string} aclass One or more space-separated classes to be removed from the class attribute
 * @return {!noh.Element} this (for chaining)
 */
noh.Element.prototype.remclass = function(aclass) {
  this.$.removeClass(aclass);
  return this;
}




/**
 * Scrolls the page to given position (in pixels)
 * @param {number} offset Position to which to scroll. (in pixels; from the top of the page)
 * @param {number=} opt_duration Time in miliseconds determining how long the scrolling will run. Default is 1000
 */
noh.scroll = function(offset, opt_duration) {
  $('html,body').animate( {scrollTop: offset }, opt_duration ? opt_duration : 1000); 
};


/**
 * Scrolls the page, so the element is on the top
 * @param {number=} opt_duration Time in miliseconds determining how long the scrolling will run. Default is 1000
 * @return {!noh.Element} this (for chaining)
 */
noh.Element.prototype.scroll = function(opt_duration) {
  noh.scroll(this.$.offset().top, opt_duration);
  return this;
}


/* 
 * *************************************************************
 * Core code ends here.
 * The rest of this file contains some basic but useful examples
 * *************************************************************
 */







/**
 * Just a shortcut for a table with one row only
 * @param {...noh.AttrsAndNodes} var_args Attributes and children nodes. See {@linkcode noh.organize} for more detailed explanation about attributes and children parameters.
 * @return {noh.Element} A new table Node with one row and given attributes and children.
 */
noh.table1r = function(var_args) {
  var an = noh.organize(arguments);
  return noh.table(an.attrs, noh.tbody(noh.tr(an.nodes)));
};



/**
 * A horizontal bar of any given elemens that uses table with one row (with attribute "class" set to "bar" by default)
 * Yes, I know that using tables to force layout is a bad practice :-)
 * @param {...noh.AttrsAndNodes} var_args Attributes and children nodes.
 */
noh.tablebar = function(var_args) {
    var an = noh.organize(arguments);
    var cells = [];
    for(var x = 0; x < an.nodes.length; ++x)
        cells.push(noh.td(an.nodes[x]));
    return noh.table1r({"class":"noh bar"}, an.attrs, cells);
};
// TODO: better bars: bar(horizontal/vertical, ...); hbar = bar(horizontal, ...); vbar = ... And no tables! (but css)
// FIXME: UPDATE: tablebar is not tested and not sure if needed at all.






// Just a div element with style changed to clearly separate its content from the rest of the page.
noh.sdiv = function(var_args) {
  return noh.div({style:"margin:10px; padding:10px; border:solid 2px"}, arguments);
};


//TODO: documentation for simple elements for tests and docs. (write some nice introduction in noh_doc.js using these elements)
noh.ex = {};

noh.ex.simple = function() {
  return noh.span("Simpleeeee! kind of man!").css("padding", 10).css("background-color", "lightgray").css("border", "solid 2px");
};

noh.ex.goofy = function() {
  return noh.span("Daaaaa..").css("padding", 20).css("background-color", "gray").css("border", "solid 4px");
};

noh.ex.silly = function() {
  return noh.span({style:"padding: 25px; font-size:xx-large; color:red; background-color:yellow; border:solid 1px red; border-radius:40px"}, ":-)");
};

noh.ex.whitey = function() {
  return noh.span({style: "padding:15px; color:white; background-color:white; border:solid 2px white"}, "White on white");
};

noh.ex.shiny = function() {
  return noh.span({style:"padding:10px; color:red; background-color:pink; border:solid 2px red"}, ":*");
};

noh.ex.idiots = function() {
  return noh.div(noh.ex.simple(), noh.ex.goofy(), noh.ex.silly());
};

noh.ex.color = function(color) {
  return noh.span({style:"padding:8px"}, "C").css("color", color).css("background-color", color)
};

//TODO: insert this code to introduction as an example of simplest "template".
//TODO: write in introduction that first examples are written so even javascript beginner should be able to understand it
/**
 * @param {number} len How many colors we want
 */
noh.ex.rainbow = function(len) {
  var arr = [];
  for(var i = 0; i < len; ++i)
    arr.push(noh.ex.color("hsl(" + (i*360/len) + ",100%,50%)"));
  return noh.span(arr);
};





/**
 * Generates the pre element prepared for SyntaxHighlighter plugin
 * @see http://alexgorbatchev.com/SyntaxHighlighter/
 * @param {string} brush The SyntaxHighlighter brush to use (like "js" fo JavaScript)
 * @param {string} code The code to display.
 * @return {noh.Element} A new pre Element prepared to colorize by SyntaxHighlighter plugin.
 */
noh.syntaxhl = function(brush, code) {
  return noh.pre({"class":"brush: " + brush + "; toolbar: false"}, code);
};

/**
 * This Element creates the "pre" html element with a source code of given function inside.
 * The "pre" element CSS "class" is set to match the SyntaxHighlighter requirements and
 * can be easly coloured using that plugin.
 * @see {@link http://alexgorbatchev.com/SyntaxHighlighter|SyntaxHighlighter} The highlighting plugin.
 * @see index.html Examples of using this element and SyntaxHighlighter plugin.
 * @param {function()} afunction The function which source code should be rendered.
 * @return {noh.Element} A new srccode Element.
 */
noh.srccode = function(afunction) {
  return noh.syntaxhl("js", afunction.toString());
};




/**
 * This Element will fly over the page (position:fixed). User should add some CSS classes to it:
 * "left" or "right" and "top" or "bottom" (but never: "left" and "right" or "top" and "bottom")
 * It will stick to given screen side automaticly (with some little margin)
 * and it can be swept away to nearest side using .hide() method; and brought back using the .show() method().
 * It should be under element with "smooth" class so it moves smoothly.
 * If user fails to add some position related classes to it, he can still manage the overlay position by hand.
 * Note: it is a good idea for overlay's children to have CSS class "pretty" so it will get some pretty default styles
 * @param {...noh.AttrsAndNodes} var_args Attributes and children nodes.
 * @return {!noh.Element} A new overlay.
 */
noh.overlay = function(var_args) {

  var overlay = noh.div(arguments).addclass("noh overlay");
  
  /**
   * @param {number=} idx of child to show (undefined means whole overlay)
   * @this {noh.Element}
   */
  overlay.show = function(idx) {
    //TODO: validate idx in debug mode
    var obj = idx === undefined ? this : this[idx];
    if(!obj.hasclass("hidden"))
      return overlay;
    if(this.hasclass("left") || this.hasclass("right"))
      obj.css("left", "").css("right", ""); // determined by the CSS rule
    if(this.hasclass("top") || this.hasclass("bottom"))
      obj.css("top", "").css("bottom", ""); // determined by the CSS rule
    obj.remclass("hidden").addclass("visible");
    return this;
  };

  /**
   * @param {number=} idx of child to hide (undefined means whole overlay)
   * @this {noh.Element}
   */
  overlay.hide = function(idx) {
    //TODO: validate idx in debug mode

    var obj = idx === undefined ? this : this[idx];
    if(obj.hasclass("hidden"))
      return overlay;

    var winwidth = $(window).width();
    var winheight = $(window).height();
    var width = obj.$.outerWidth(true);
    var height = obj.$.outerHeight(true);
    var offset = obj.$.offset();
    var left = offset.left;
    var top = offset.top - $(document).scrollTop();
    var right = winwidth - left - width;
    var bottom = winheight - top - height;

    if(this.hasclass("left"))
      obj.css("left", "" + (-left-width-5) + "px");
    else if(this.hasclass("right")) {
      if(obj === this) // we are moving whole overlay (position:fixed; alligned to right side)
        obj.css("right", "" + (-right-width-5) + "px");
      else // we are moving one child (position:relative)
        obj.css("left", "" + (width+right+5) + "px");
    }
    else if(this.hasclass("top"))
      obj.css("top", "" + (-top-height-5) + "px");
    else if(this.hasclass("bottom")) {
      if(obj === this) // we are moving whole overlay (position:fixed; alligned to bottom side)
        obj.css("bottom", "" + (-bottom-height-5) + "px");
      else // we are moving one child (position:relative)
        obj.css("top", "" + (height+bottom+5) + "px");
    }

    obj.remclass("visible").addclass("hidden");
    return this;
  };

  return overlay;
};




/**
 * Makes given element sleepy. By default it is in "asleep" state (it has the "asleep" CSS class)
 * If we wake it (method: wake) it will be awake (will have the "awake" CSS class) for some time.
 * Then it will fall asleep again (the "awake" CSS class is removed, and it gets "asleep" CSS class).
 * User can wake it again by invoking the "wake" method.
 * User can of course define how it will behave in "awake" and in "asleep" states in CSS file.
 * @param {!noh.Element} element to modify
 * @param {number=} opt_duration How many miliseconds will it be awake by default (it will be 1000 if not provided).
 * @return {!noh.Element}
 */
noh.sleepy = function(element, opt_duration) {

  element.addclass("noh").addclass("sleepy");

  element.defaultAwakeTime_ = opt_duration === undefined ? 1000 : opt_duration;

  /** @this {noh.Element} */
  element.wake = function(opt_duration) {
    this.remclass("asleep").addclass("awake");
    window.clearTimeout(this.timeoutId_);
    var duration = opt_duration === undefined ? element.defaultAwakeTime_ : opt_duration;
    var callback = function() { element.sleep(); }
    this.timeoutId_ = window.setTimeout(callback, duration);
  };

  /** @this {noh.Element} */
  element.sleep = function() {
    window.clearTimeout(this.timeoutId_);
    this.timeoutId_ = undefined;
    this.remclass("awake").addclass("asleep");
  };

  element.sleep();

  return element;
};








/**
 * An object that can show or hide it's content by rolling it down (hidden->visible) or up (visible->hidden)
 * @interface
 */
noh.IBlind = function() {};

/**
 * Returns if the content is visible (down).
 * @return {boolean}
 */
noh.IBlind.prototype.down = function() {};

/**
 * Rolls the blind down (to show it content) or up (hiding the content)
 * @param {boolean} down
 * @return {!noh.IBlind} this (for chaining)
 */
noh.IBlind.prototype.roll = function(down) {};



/**
 * An object that can show or hide it's content by rolling it down (hidden->visible) or up (visible->hidden)
 * @param {...noh.AttrsAndNodes} var_args Attributes and children nodes. See {@linkcode noh.organize} for more detailed explanation about attributes and children parameters.
 * @constructor
 * @extends {noh.Element}
 * @implements {noh.IBlind}
 * TODO: it can change its size dynamicly so it should be inside some absolutely positioned block, for better performance.
 * (to avoid forcing browser to relayout the whole page too much)
 */
noh.Blind = function(var_args) {
  var content = noh.div(arguments).addclass('noh blind content');
  noh.Element.call(this, "div", content);
  this.addclass('noh blind');
  this.content_ = content;
  this.roll(false);
  var this_ = this;
  this.$.show(function() {this_.roll(this_.down());});
};

noh.Blind.prototype = new noh.Element("div");

/**
 * Returns if the content is visible (down).
 * @return {boolean}
 */
noh.Blind.prototype.down = function() { return this.down_; };

/**
 * Rolls the blind down (to show it content) or up (hiding the content)
 * @param {boolean} down
 * @return {!noh.Blind} this (for chaining)
 */
noh.Blind.prototype.roll = function(down) {
  var $blind = this.$;
  var $content = this.content_.$;
  var w = $content.width();
  var h = $content.height();
  $content.css("clip", "rect(0px " + w + "px " + (down ? h : 0) + "px 0px");
  if(down) {
    this.content_.remclass("hidden");
    this.content_.addclass("visible");
  }
  else {
    this.content_.remclass("visible");
    this.content_.addclass("hidden");
  }
  $blind.width(w);
  $blind.height(down ? h : 0);
  this.down_ = down;
  return this;
};

/**
 * TODO: description
 * @param {...noh.AttrsAndNodes} var_args Attributes and children nodes
 * @return {!noh.Blind}
 */
noh.blind = function(var_args) {
  return new noh.Blind(arguments);
};




/**
 * An object that contains a collection of elements and always one of them can be "selected" (or none)
 * @interface
 */
noh.IOneOf = function() {};

/**
 * Returns which element is selected (or -1 if none is selected)
 * @return {number}
 */
noh.IOneOf.prototype.selected = function() {};

/**
 * Selects an element with given index (-1 means: do not select any element)
 * @param {number} idx
 * @return {!noh.IOneOf} this (for chaining)
 */
noh.IOneOf.prototype.select = function(idx) {};





/**
 * Element that displays one of his children at a time (or none).
 * (the children are placed one below another and then their visibility is changed respectively)
 * @param {...noh.AttrsAndNodes} var_args Attributes and children nodes. See {@linkcode noh.organize} for more detailed explanation about attributes and children parameters.
 * @constructor
 * @extends {noh.Element}
 * @implements {noh.IOneOf}
 * TODO: it can change its size dynamicly so it should be inside some absolutely positioned block, for better performance.
 */
noh.OneOf = function(var_args) {
  var an = noh.organize(arguments);
  for(var i = 0; i < an.nodes.length; ++i) {
    var blind = noh.blind(an.nodes[i]);
    an.nodes[i] = blind;
    blind.oneOfIdx_ = i;
  }
  noh.Element.call(this, "div", an.attrs, an.nodes);
  this.addclass('noh oneof');

  this.selected_ = -1; 
};

noh.OneOf.prototype = new noh.Element("div");

/**
 * @return {number}
 */
noh.OneOf.prototype.selected = function() { return this.selected_; };

/**
 * Displays given child and hides all the others.
 * @param {(number|noh.Node)} idx Number of child to display or the child Node itself. (-1 or null means: do not display any child)
 * @return {!noh.OneOf} this (for chaining)
 */
noh.OneOf.prototype.select = function(idx) {
  if(idx instanceof noh.Node)
    idx = idx.oneOfIdx_;
  else if(idx === null)
    idx = -1;
  var l = this.length;
  if((idx < -1) || (idx >= l))
    idx = -1;

  if(this.selected_ != -1)
    this[this.selected_].roll(false);

  if(idx != -1)
    this[idx].roll(true);

  this.selected_ = idx;

  return this;
};

/** @private */
noh.OneOf.prototype.selectModulo_ = function(idx) {
  return this.select(idx < 0 ? this.length-1 : idx % this.length);
};

/**
 * Changes the displayed child to the next one.
 */
noh.OneOf.prototype.next = function() { return this.selectModulo_(this.selected() + 1); };

/**
 * Changes the displayed child to the previous one.
 */
noh.OneOf.prototype.prev = function() { return this.selectModulo_(this.selected() - 1); };


/**
 * TODO: description
 * @param {...noh.AttrsAndNodes} var_args Attributes and children nodes
 * @return {!noh.OneOf}
 */
noh.oneof = function(var_args) {
  return new noh.OneOf(arguments);
};



/**
 * This Element displays the "details..." button, and displays the content only after user clicks it.
 * Then the user can hide the content again by clicking the button again.
 * @param {...noh.AttrsAndNodes} var_args Attributes and children nodes
 * @return {!noh.Element} 
 */
noh.details = function(var_args) {
  var content = noh.div(arguments).addclass('noh details content');
  var blind = noh.blind(content);
  var button = noh.button("details...").addclass('noh details button').attr('title', 'show/hide details');
  button.on("click", function() {blind.roll(!blind.down());});
  var details = noh.div(noh.div(button), noh.div(blind)).addclass('noh details');
  details.down = function() { return blind.down(); };
  details.roll = function(down) { return blind.roll(down); };
  return details;
};




/**
 * An object that can rotate its children up or down
 * @param {number} lines How many elements will be visible. The rest will be hidden (opacity:0 unless user change some css styles)
 * @param {string|number} width A width of the reel. It should be set big enough, so all elements fit inside.
 * It will be used to set three CSS properties: "width", "min-width", "max-width", so it can be string like "400px".
 * It can also be set to special value: "dynamic" or: "automatic",
 * and in that case it will be computed automaticly using the actual size of elements, when reel is shown and when it rotates.
 * The difference between "automatic" and "dynamic" is that "dynamic" take into account only visible element in every moment,
 * and "automatic" checks all elements every time (also hidden ones) (as a result "dynamic" is little more dynamic that "automatic")
 * @param {string|number} height A height of the reel. It should be set big enough, so elements don't overlap too much.
 * See param width for details.
 * @param {...noh.AttrsAndNodes} var_args Attributes and children nodes. See {@linkcode noh.organize} for more detailed explanation about attributes and children parameters.
 * @constructor
 * @extends {noh.Element}
 * @implements {noh.IOneOf}
 * TODO: it can change its size dynamicly so it should be inside some absolutely positioned block, for better performance.
 */
noh.Reel = function(lines, width, height, var_args) {
  var an = noh.organize(arguments, 3);
  for(var i = 0; i < an.nodes.length; ++i) {
    var element = noh.div(an.nodes[i]).addclass('noh reel element');
    an.nodes[i] = element;
  }
  noh.Element.call(this, "div", an.attrs, an.nodes);
  this.addclass('noh reel');

  /**
   * @readonly
   */
  this.lines = lines;

  /**
   * @readonly
   */
  this.rotation = 0;

  this.width = width;
  this.height = height;
  this.chksize();

  var this_ = this;
  this.$.show(function() {
    this_.update();
  });
  this.selected_ = -1;
};

noh.Reel.prototype = new noh.Element("div");

/**
 * @return {number}
 */
noh.Reel.prototype.selected = function() { return this.selected_; };

/**
 * Select given line (element at that line nr will always have CSS class "selected")
 * @param {number} nr Line nr to select.
 * @return {!noh.Reel} this (for chaining)
 */
noh.Reel.prototype.select = function(nr) {
  if((nr < -1) || (nr >= this.length))
    nr = -1;

  if(this.selected_ == nr)
    return this;

  if(this.selected_ != -1)
    this.getelem(this.selected_).remclass("selected");

  if(nr != -1)
    this.getelem(nr).addclass("selected");

  this.selected_ = nr;

  return this;
};

/**
 * Updates CSS properties: "min-width", "max-width", "width", "min-height", "max-height", "height"; if reel width and height was specified as "dynamic"
 */
noh.Reel.prototype.chksize = function() {
  var size = 0;
  var maxsize = 0;
  var esize = 0;
  if(this.width == "automatic") {
    for(var i = 0; i < this.length; ++i) {
      esize = this[i].$.width();
      if(esize > size)
        size = esize;
    }
  }
  else if(this.width == "dynamic") {
    for(var i = 0; i < this.lines; ++i) {
      esize = this.getelem(i).$.width();
      if(esize > size)
        size = esize;
    }
  }
  else
    size = this.width;
  this.css("min-width", size).css("max-width", size).css("width", size);
  this.exactwidth_ = size;

  size = 0;
  if(this.height == "automatic") {
    for(var i = 0; i < this.length; ++i) {
      esize = this[i].$.height();
      if(esize > size)
        size = esize;
    }
    size *= this.lines;
  }
  else if(this.height == "dynamic") {
    for(var i = 0; i < this.lines; ++i) {
      esize = this.getelem(i).$.height();
      if(esize > size)
        size = esize;
    }
    size *= this.lines;
  }
  else
    size = this.height;
  this.css("min-height", size).css("max-height", size).css("height", size);
  this.exactheight_ = size;
};

/**
 * @param {number} nr Line nr
 * @return {number} fixed line nr. Between 0 and this.length-1
 */
noh.Reel.prototype.fixLineNr_ = function(nr) {
  while(nr < 0)
    nr += this.length; //FIXME: better computation, without loop.
  while(nr >= this.length)
    nr -= this.length; //FIXME: better computation, without loop.

  return nr;
};


/**
 * Returns a child at given position according to actual rotation.
 * Position 0 is at the top, 1 is below it, and so on..
 * @param {number} nr Which line to get.
 * @return {noh.Node} A child at given position.
 */
noh.Reel.prototype.getelem = function(nr) {
  return this[this.fixLineNr_(nr - this.rotation)];
};

/**
 * Update the reel properties like size, elements positions, CSS classes etc.
 * @param {number=} opt_lines Optionally we can change the number of visible elements.
 * @return {!noh.Reel} this (for chaining)
 */
noh.Reel.prototype.update = function(opt_lines) {

  if(opt_lines !== undefined)
    this.lines = opt_lines;
  //TODO: check opt_lines value in debug mode
  //TODO: additional warning in debug mode, when lines is to close to length - so it can look ugly (test it in practice first)

  this.chksize();

  for(var i = 0; i < this.length; ++i) {
    var element = this.getelem(i);
    element.remclass("selected");
    if(i < this.lines) {
      element.remclass("hidden").addclass("visible");
      element.css("top", "" + (i * this.exactheight_ / this.lines) + "px");
    }
    else {
      element.remclass("visible").addclass("hidden");
      element.css("top", "" + ((this.length-i-1) * this.exactheight_ / (this.length-this.lines)) + "px");
    }
  }
  if(this.selected_ != -1)
    this.getelem(this.selected_).addclass("selected");
  return this;
};


/**
 * Rotates the reel
 * @param {number} count How many lines to rotate down. (Negative means up)
 * @return {!noh.Reel} this (for chaining)
 */
noh.Reel.prototype.rotate = function(count) {
  this.rotation = this.fixLineNr_(this.rotation + count);
  return this.update();
};


/**
 * Rotates the reel many times with time gaps
 * @param {number} count How many lines to rotate down. (Negative means up)
 * @param {number=} opt_random If specified, the reel will generate random number between 0 and opt_random, and add it to param count.
 * @param {number=} opt_time Defines how many milisecond to wait between single rotations. Default is 200
 * @return {!noh.Reel} this (for chaining)
 */
noh.Reel.prototype.spin = function(count, opt_random, opt_time) {
  if(this.intervalId_) {
    console.error("The reel is already spinning!");
    return this;
  }
  if(opt_random)
    count += Math.round(Math.random() * opt_random);
  var time = opt_time ? opt_time : 200;
  var this_ = this;
  var callback = function() {
    if(count == 0) {
      window.clearInterval(this_.intervalId_);
      this_.intervalId_ = undefined;
    }
    else if(count > 0) {
      this_.rotate(1);
      --count;
    }
    else {
      this_.rotate(-1);
      ++count;
    }
  };
  this.intervalId_ = window.setInterval(callback, time);
  return this;
};

/**
 * TODO: description
 * @param {number} lines See {@linkcode noh.Reel} for details.
 * @param {string|number} width See {@linkcode noh.Reel} for details. 
 * @param {string|number} height See {@linkcode noh.Reel} for details. 
 * @param {...noh.AttrsAndNodes} var_args See {@linkcode noh.Reel} for details.  
 * @return {!noh.Reel}
 */
noh.reel = function(lines, width, height, var_args) {
  var an = noh.organize(arguments, 3);
  return new noh.Reel(lines, width, height, an.attrs, an.nodes);
};



/** @typedef undefined */ //TODO: It will be a record type in future
noh.FancyOptions;

/**
 * Makes given element more fancy.
 * It always add a "fancy" class (so we can define in CSS file what really is "fancy",
 * but it can also add some more fun javascript stuff to some elements (depending of the element type)
 * you can enable/disable/configure different fancy features using "options" parameter.
 * @param {!noh.Element} element to modify
 * @param {noh.FancyOptions=} opt_options TODO: define some configuration options
 * @return {!noh.Element}
 */
noh.fancy = function(element, opt_options) {
  element.addclass("fancy");
  if(noh.arr.indexOf(element.tag, ["h1", "h2", "h3", "h4"]) != -1) {
    element.on("click", function() { this.noh.scroll(); });
  }
  else if(element.tag == "a") {
    var href = element.$.attr("href");
    if(href && href.length > 0 && href[0] == "#") {
      element.on("click", function() { noh.scroll($($(this).attr("href")).offset().top) });
    }
  }
  return element;
}


/**
 * Something like a simple "kbd" element, but it wraps urls in given text inside the appropriate "a" elements.
 * @param {string} atext Text to wrap inside kdb element.
 * @return {!noh.Element} kbd element with given text splitted to words; and with urls wrapped inside the a links
 */
noh.ukbd = function(atext) {
  var words = atext.split(/\s+/);
  var url = /(https?|ftp):\/\//;
  var map = words.map(function(word) {
    if(url.test(word))
      return noh.a({href:word, target:"_blank"}, word);
    else
      return " " + word + " ";
  });
  return noh.kbd(map);
}









noh.log = {};




/** @typedef {!Object} */
noh.log.Data; //FIXME: how to express ArrayLike type? (like: ArrayLike.<*>)


/**
 * @param {noh.log.Data} data
 * @return {string}
 */
noh.log.data2str = function(data) {
  var str = "";
  if(data.length > 0)
    str += data[0];
  for(var i = 1; i < data.length; ++i)
    str = str + " " + data[i].toString();  
  return str;
};

/**
 * @interface
 * This is basic interface for loggers.
 */
noh.log.ILogger = function() {};


/**
 * Logs given data with given severity
 * @param {string} classes One or more (space separated) classes to decorate the logged message (like: "info", or "warning", or "error", or "debug")
 * @param {noh.log.Data} data Data to log. It has to be an array like object.
 * @return {!noh.log.ILogger} this (for chaining)
 */
noh.log.ILogger.prototype.log = function(classes, data) {};





/**
 * Little (one line) logger.
 * @constructor
 * @implements {noh.log.ILogger}
 */
noh.log.Little = function() {
  noh.Element.call(this, "div");
  this.addclass('noh log little');
};

noh.log.Little.prototype = new noh.Element("div");

/**
/**
 * Logs given data with given severity
 * @param {string} classes One or more (space separated) classes to decorate the logged message (like: "info", or "warning", or "error", or "debug")
 * @param {noh.log.Data} data Data to log. It has to be an array like object.
 * @return {!noh.log.Little} this (for chaining)
 */
noh.log.Little.prototype.log = function(classes, data) { 
  if(this.length)
    this.rem(); // removes last (in this case only one) child.
  var ukbd = noh.ukbd(noh.log.data2str(data)).addclass("noh log element").addclass(classes);
  this.add(ukbd);
  return this;
};


/**
 * Little (one line) logger.
 * @return {!noh.log.Little}
 */
noh.log.little = function() {
  return new noh.log.Little();
};


/**
 * Sleepy little logger
 * @param {number=} opt_duration How many miliseconds will it be visible. See {@linkcode noh.sleepy}
 * @return {!noh.Element}
 */
noh.log.slittle = function(opt_duration) {
  var little = noh.log.little();
  var slittle = noh.sleepy(little, opt_duration);
  slittle.log_ = slittle.log;
  /** @this {noh.Element} */
  slittle.log = function(classes, data) { this.log_(classes, data); this.wake(); };
  return slittle;
};







/**
 * @interface
 * Another interface for loggers. This one is subset of chrome console API and firefox console API.
 * It just allows to log messages with three different severity levels:
 * "info", "warn", "error" (and "log" which is the same as "info")
 */
noh.log.IConsole = function() {};

/**
 * Logs given data with default (general) severity. Usually this is the same as "info"
 * @param {...*} var_args Data to log. Strings are printed as they are; numbers are converted to strings; Objects are converted to strings using .toString() method.
 * @return {!noh.log.IConsole} this (for chaining)
 */
noh.log.IConsole.prototype.log = function(var_args) {};

/**
 * Logs given data with "info" (normal) severity. Usually this is the same as "log"
 * @param {...*} var_args Data to log. {@linkcode noh.log.IConsole.prototype.log}
 * @return {!noh.log.IConsole} this (for chaining)
 */
noh.log.IConsole.prototype.info = function(var_args) {};

/**
 * Logs given data with "warn" (warning) severity. Usually this severity is marked somehow (like bold font), but not with red color.
 * @param {...*} var_args Data to log. {@linkcode noh.log.IConsole.prototype.log}
 * @return {!noh.log.IConsole} this (for chaining)
 */
noh.log.IConsole.prototype.warn = function(var_args) {};

/**
 * Logs given data with "error" severity. Usually this severity is highlighted (f.e. with red bold font).
 * @param {...*} var_args Data to log. {@linkcode noh.log.IConsole.prototype.log}
 * @return {!noh.log.IConsole} this (for chaining)
 */
noh.log.IConsole.prototype.error = function(var_args) {};




/**
 * Wraps an ILogger object into IConsole.
 * This console can be then installed as global console object (window.console), so all system logs will be logged using given logger.
 * @implements {noh.log.IConsole}
 * @constructor
 * @param {!noh.log.ILogger} logger A logger to wrap.
 */
noh.log.L2C = function(logger) {
  this.logger = logger;
};


/**
 * Logs given data with default (general) severity. This is the same as "info"
 * @param {...*} var_args Data to log. Strings are printed as they are; numbers are converted to strings; Objects are converted to strings using .toString() method.
 * @return {!noh.log.L2C} this (for chaining)
 */
noh.log.L2C.prototype.log = function(var_args) { this.logger.log("info", arguments); return this; };

/**
 * Logs given data with "info" (normal) severity.
 * @param {...*} var_args Data to log. {@linkcode noh.log.IConsole.prototype.log}
 * @return {!noh.log.L2C} this (for chaining)
 */
noh.log.L2C.prototype.info = function(var_args) { this.logger.log("info", arguments); return this; };

/**
 * Logs given data with "warn" (warning) severity. Usually this severity is marked somehow (like bold font), but not with red color.
 * @param {...*} var_args Data to log. {@linkcode noh.log.IConsole.prototype.log}
 * @return {!noh.log.L2C} this (for chaining)
 */
noh.log.L2C.prototype.warn = function(var_args) { this.logger.log("warning", arguments); return this; };

/**
 * Logs given data with "error" severity. Usually this severity is highlighted (f.e. with red bold font).
 * @param {...*} var_args Data to log. {@linkcode noh.log.IConsole.prototype.log}
 * @return {!noh.log.L2C} this (for chaining)
 */
noh.log.L2C.prototype.error = function(var_args) { this.logger.log("error", arguments); return this; };

/**
 * Logs given data with "debug" severity.
 * @param {...*} var_args Data to log. {@linkcode noh.log.IConsole.prototype.log}
 * @return {!noh.log.L2C} this (for chaining)
 */
noh.log.L2C.prototype.debug = function(var_args) { this.logger.log("debug", arguments); return this; };

/**
 * Installs this console as a global console object.
 * @suppress {checkTypes}
 */
noh.log.L2C.prototype.install = function() { window.console = this; };


/**
 * Wraps an ILogger object into IConsole.
 * This console can be then installed as global console object (window.console), so all system logs will be logged using given logger.
 * @param {!noh.log.ILogger} logger
 * @return {!noh.log.L2C}
 */
noh.log.l2c = function(logger) {
  return new noh.log.L2C(logger);  
};






/**
 * Wraps an IConsole object into ILogger.
 * @implements {noh.log.ILogger}
 * @constructor
 * @param {!noh.log.IConsole} console A console to wrap.
 */
noh.log.C2L = function(console) {
  this.console = console;
};

/**
 * Logs given data with given severity
 * @param {string} classes One or more (space separated) classes to decorate the logged message (like: "info", or "warning", or "error", or "debug")
 * @param {noh.log.Data} data Data to log. It has to be an array like object.
 * @return {!noh.log.C2L} this (for chaining)
 */
noh.log.C2L.prototype.log = function(classes, data) {
  if(/error/.test(classes))
    this.console.error.apply(this.console, data);
  else if(/warning/.test(classes))
    this.console.warn.apply(this.console, data);
  else if( (/debug/.test(classes)) && (this.console.debug instanceof Function) )
    this.console.debug.apply(this.console, data);
  else
    this.console.info.apply(this.console, data);
  return this;
};


/**
 * Wraps an IConsole object into ILogger.
 * @param {!noh.log.IConsole} console
 * @return {!noh.log.C2L}
 */
noh.log.c2l = function(console) {
  return new noh.log.C2L(console);
};




/**
 * Creates a logger that logs on all provided loggers.
 * @implements {noh.log.ILogger}
 * @constructor
 * @param {Array.<!noh.log.ILogger>} loggers
 */
noh.log.Multi = function(loggers) {
  this.loggers = loggers;
};

/**
 * Logs given data with given severity to multiple loggers
 * @param {string} classes One or more (space separated) classes to decorate the logged message (like: "info", or "warning", or "error", or "debug")
 * @param {noh.log.Data} data Data to log. It has to be an array like object.
 * @return {!noh.log.Multi} this (for chaining)
 */
noh.log.Multi.prototype.log = function(classes, data) {
  for(var i = 0; i < this.loggers.length; ++i)
    this.loggers[i].log(classes, data);
  return this;
};

/**
 * @param {Array.<!noh.log.ILogger>} loggers
 * @return {!noh.log.Multi}
 */
noh.log.multi = function(loggers) {
  return new noh.log.Multi(loggers);
};



/**
 * Creates a logger that filters every message first (through provided filter function),
 * and then logs the result.
 * @implements {noh.log.ILogger}
 * @constructor
 * @param {!noh.log.ILogger} logger
 * @param {function(noh.log.Data):noh.log.Data} filter
 */
noh.log.Filter = function(logger, filter) {
  this.logger = logger;
  this.filter = filter;
};

/**
 * Logs given data with given severity after filtering it.
 * @param {string} classes One or more (space separated) classes to decorate the logged message (like: "info", or "warning", or "error", or "debug")
 * @param {noh.log.Data} data Data to log. It has to be an array like object.
 * @return {!noh.log.Filter} this (for chaining)
 */
noh.log.Filter.prototype.log = function(classes, data) {
  this.logger.log(classes, this.filter(data));
  return this;
};

/**
 * @param {!noh.log.ILogger} logger
 * @param {function(noh.log.Data):noh.log.Data} filter
 * @return {!noh.log.Filter}
 */
noh.log.filter = function(logger, filter) {
  return new noh.log.Filter(logger, filter);
};



/**
 * TODO: description
 * @param {!noh.log.ILogger} logger
 * @return {!noh.log.ILogger}
 */
noh.log.addtime = function(logger) {
  var filter = function(data) {
    var now = new Date();
    var time =
      "[" +
      noh.str.prefix("" + now.getHours(), "0", 2) +
      ":" +
      noh.str.prefix("" + now.getMinutes(), "0", 2) +
      ":" +
      noh.str.prefix("" + now.getSeconds(), "0", 2) +
      "]";
    return [time].concat(Array.prototype.slice.call(data, 0));
  };
  return noh.log.filter(logger, filter);
};



/**
 * TODO: description
 * @param {!noh.log.ILogger} logger
 * @param {number} len
 * @return {!noh.log.ILogger}
 */
noh.log.limitlen = function(logger, len) {
  var filter = function(data) {
    var str = noh.log.data2str(data);
    return [noh.str.limitlen(str, len)];
  };
  return noh.log.filter(logger, filter);
};





/**
 * Creates a logger that uses Reel to rotate log lines.
 * @param {number} lines Number of visible lines.
 * @param {number=} opt_duration How many miliseconds will any log line be visible. See {@linkcode noh.log.slittle}
 * @return {!noh.Element}
 */
noh.log.reel = function(lines, opt_duration) {
  //TODO: validation of lines value in debug mode
  var length = lines * 2 + 1;
  var duration = opt_duration === undefined ? 10000 : opt_duration;
  var loggers = [];
  for(var i = 0; i < length; ++i)
    loggers.push(noh.log.slittle(duration));
  var reel = noh.reel(lines, "automatic", "automatic", loggers);
  reel.select(lines-1);
  var logger = noh.div(reel).addclass('noh log reel');
  logger.reel = reel;
  /** @this {noh.Element} */
  logger.log = function(classes, data) {
    var logger = this.reel.getelem(this.reel.lines)[0]; // This is first invisible logger
    logger.log(classes, data);
    this.reel.rotate(-1);
  };
  /** @this {noh.Element} */
  logger.setlines = function(lines) {
    var r = lines - this.reel.lines;
    this.reel.lines = lines;
    this.reel.rotate(r);
  };
  return logger;
};




/**
 * TODO: desc
 * @param {number} len
 * @param {string=} opt_placeholder
 * @return {!noh.Element}
 */
noh.cmdline = function(len, opt_placeholder) {
  var placeholder = opt_placeholder || 'alert("hello world")';
  var input = noh.input().attr('type', 'text').attr('size', len).attr('placeholder', placeholder).addclass('noh cmdline');
  var enter = noh.button(String.fromCharCode(8629)).attr('title', 'Press enter to run the command.').addclass('noh cmdline');
  var cmdline = noh.div(input, enter).css("display", "inline-block").addclass('noh cmdline');
  cmdline.run = function() {
    var val = input.$.val();
    console.log(val);

    try {
      var r = eval(val);
      if(r !== undefined)
        console.log(r);
    }
    catch(e) {
      console.error(e);
    }

    input.$.val("");
  };

  cmdline.type = function(text) {
    input.$.val(text);
  };

  cmdline.focus = function() {
    input.$.focus();
  };

  cmdline.onfocus = function(handler) {
    input.$.focus(handler);
    enter.$.focus(handler);
  };

  input.on("keypress", function(e) {
    if(e.which == 13)
      cmdline.run();
  });
  enter.on("click", function() {
    cmdline.run();
  });

  return cmdline;
};




noh.objtest = function(obj, commands) {

  var objwrap = noh.table1r({style:"border: 6px ridge green"},
    noh.td(obj)
  );

  var cmdline = noh.cmdline(60, 'obj.some_method()');

  cmdline.onfocus(function() {
    window["obj"] = obj; 
  });

  var buttons = [];
  for(var i = 0; i < commands.length; ++i) {
    var button = noh.button(commands[i]).addclass('noh button')
      .attr('title', 'Prepare the "' + commands[i] + '" command to run.');

    button.on("click", function() {
      window["obj"] = obj;
      cmdline.type(this.textContent);
      cmdline.focus();
    });

    buttons.push(button);
    buttons.push(noh.br());
  }


  return noh.div(
    noh.p(objwrap),
    noh.p(cmdline),
    noh.p(buttons)
  );
}










/*****************************************************************************
 * FIXME: the rest of this file is an old not tested code!
 * TODO: review it first! (use chaining - it wasn't there when this code was written)
 * TODO: make simple tests for it and try it out
 ****************************************************************************/











/**
 * An object that can be in two logical states. Selected or not selected.
 * @interface
 */
noh.ISelectable = function() {};

/**
 * Checks if the object is selected or not.
 * @return {boolean} If the object is selected.
 */
noh.ISelectable.prototype.selected = function() {};

/**
 * Selects/deselects the object. (depending on this.selected())
 * @return {!noh.ISelectable} this (for chaining)
 */
noh.ISelectable.prototype.toggle = function() {};



/** 
 * This Element is prepared to be used as a menu item. It can be selected or not.
 * It will have css classes: "noh", "menu" and "item", (and "selected" if it is in selected state).
 * It changes its state when clicked (selected/not selected) (by calling the toggle method)
 * The toggle method can be overriden to add some functionality when the state is changing.
 * @param {noh.Node|string} content Usually it is just a text to display, but it can be any noh.Node.
 * @constructor               
 * @extends {noh.Element}
 * @implements {noh.ISelectable}
 */
noh.MenuItem = function(content) {
  noh.Element.call(this, "div", {"class": "noh menu item"}, content);
  this.on("click", function() { this.noh.toggle(); return false; });
}

noh.MenuItem.prototype = new noh.Element("div");

/**
 * This method should be overriden if we want to add some new fuctionality when the state is changing;
 * but you should call the original toggle anyway
 */
noh.MenuItem.prototype.toggle = function() { this.toggleclass("selected"); return this; };

noh.MenuItem.prototype.selected = function() { return this.hasclass("selected"); };

/**
 * @param {noh.Node|string} content Usually it is just a text to display, but it can be any noh.Node.
 * @return {!noh.MenuItem}
 */
noh.menuitem = function(content) { return new noh.MenuItem(content); };

/**
 * A menuitem with additional payload that is shown only when the menuitem is selected
 * @param {noh.ISelectable} item A main part - this is visible all the time
 * @param {noh.Node|string} payload Second part - this is visible only when item is selected. It shows itself below the main part.
 * @return {noh.ISelectable} A menuitem with payload attached.
 * TODO: change to new class BigMenuItem - for better performance and consistency
 */
noh.bigmenuitem = function(item, payload) {
  var oneof = noh.oneof(payload)
  var bigmenuitem = noh.div(
    noh.div(item),
    noh.div(oneof)
  );
  item.toggle_orig_ = item.toggle;
  item.toggle = function() { bigmenuitem.toggle(); return this; };
  /** @this {noh.Element} */
  bigmenuitem.toggle = function() {
    item.toggle_orig_();  
    oneof.select(this.selected() ? 0 : -1);
  };
  bigmenuitem.selected = function() { return item.selected(); };

  return bigmenuitem;
};

//TODO: test: wrap some menu item with bigmenuitem a few times and check if all payloads are synced
//like: noh.bigmenuitem(noh.bigmenuitem(noh.menuitem("some item"), payload1), payload2)




/**
 * @extends {noh.Element}
 * @implements {noh.IOneOf}
 * @param {...noh.AttrsAndNodes} var_args Attributes and children. Children should be proper menuitems (implement:ISelectable extend:Element)
 * @constructor
 */
noh.Menu = function(var_args) {

  var an = noh.organize(arguments);

  noh.Element.call(this, "div", an.attrs, an.nodes);

  this.items_ = an.nodes;

  for(var i = 0; i < this.items_.length; ++i) {
    var item = this.items_[i];
    if(item.selected())
      item.toggle();
    item.menu_ = this;
    item.menuIdx_ = i;
    item.toggle_orig_ = item.toggle;
    item.toggle = function() {
      this.menu_.select(this.selected() ? -1 : this.menuIdx_);
    };
  }

  this.selected_ = -1;
};

noh.Menu.prototype = new noh.Element("div");

noh.Menu.prototype.selected = function() { return this.selected_; };

noh.Menu.prototype.select = function(idx) {

  if(this.selected_ != -1)
    this.items_[this.selected_].toggle_orig_(); // deselects old item

  //TODO: check the idx value in DEBUG mode (check the @define in closure compiler) (make sure it is removed completely in release mode)

  this.selected_ = idx;

  if(idx == -1)
    return this;

  this.items_[idx].toggle_orig_(); //selects new item

  return this;
};


/**
 * TODO: description
 * @param {...noh.AttrsAndNodes} var_args Attributes and children. Children should be proper menuitems (implement:ISelectable extend:Element)
 * @return {!noh.Menu}
 */
noh.menu = function(var_args) {
  return new noh.Menu(arguments);
};



/**
 * A menuitem with additional menu that is shown only when the menuitem is selected
 * @param {noh.ISelectable} item A main part - this is visible all the time
 * @param {noh.Menu} menu Second part - this menu is visible only when item is selected. It shows itself below the main part.
 * @return {noh.ISelectable} A menuitem with menu attached.
 */
noh.submenu = function(item, menu) {
  var submenu = noh.bigmenuitem(item, menu);
  submenu.menu = menu;
  return submenu;
};


