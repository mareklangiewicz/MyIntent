package pl.mareklangiewicz.myutils

import mod

/**
 * Created by Marek Langiewicz on 16.02.16.
 */

/** Priorities */
// TODO LATER: consider enum
const val VERBOSE = 2
const val DEBUG = 3
const val INFO = 4
const val WARN = 5
const val ERROR = 6
const val ASSERT = 7

interface ILogger {
    fun log(priority: Int = INFO, message: String = "", throwable: Throwable? = null)

    fun v(message: String = "", throwable: Throwable? = null) { log(VERBOSE, message, throwable) }
    fun d(message: String = "", throwable: Throwable? = null) { log(DEBUG  , message, throwable) }
    fun i(message: String = "", throwable: Throwable? = null) { log(INFO   , message, throwable) }
    fun w(message: String = "", throwable: Throwable? = null) { log(WARN   , message, throwable) }
    fun e(message: String = "", throwable: Throwable? = null) { log(ERROR  , message, throwable) }
    fun a(message: String = "", throwable: Throwable? = null) { log(ASSERT , message, throwable) }

    fun q(message: String = "", throwable: Throwable? = null) { }
}



class EmptyLogger : ILogger {
    override fun log(priority: Int, message: String, throwable: Throwable?) { }
}


class SystemLogger : ILogger {
    override fun log(priority: Int, message: String, throwable: Throwable?) {
        val stream = (priority > WARN) % System.err ?: System.out
        stream.println(message)
    }
}