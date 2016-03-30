package pl.mareklangiewicz.myutils

import android.app.Activity

/**
 * Created by Marek Langiewicz on 30.03.16.
 */
class MyCommandsExecutor(val activity: Activity) : Function1<MyCommand, Unit> {
    override fun invoke(cmd: MyCommand) {
        throw UnsupportedOperationException()
    }
}