package pl.mareklangiewicz.myutils

import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 * Created by Marek Langiewicz on 16.02.16.
 */


fun ImageView.loadUrl(url: String) {
    Picasso.get().load(url).into(this)
}
