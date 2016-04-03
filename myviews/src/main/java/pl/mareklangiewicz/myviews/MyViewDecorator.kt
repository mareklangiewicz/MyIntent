package pl.mareklangiewicz.myviews

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT

/**
 * Created by Marek Langiewicz on 31.05.15.
 * This class contains some static methods that allow to inject some decorations
 * to selected views in view hierarchy. It is probably bad idea to mess with
 * default view hierarchy generation, so... don't use this class. :-)
 * We do not decorate inside already decorated view.
 */
object MyViewDecorator {

        fun decorateOne(view: View, @LayoutRes decoration: Int, inflater: LayoutInflater = LayoutInflater.from(view.context)) {
            val params = view.layoutParams
            val parent = view.parent as ViewGroup
            val idx = parent.indexOfChild(view)
            parent.removeViewAt(idx)
            val decorated = inflater.inflate(decoration, null)
            parent.addView(decorated, idx, params)
            val content = decorated.findViewById(android.R.id.content) as ViewGroup
            content.addView(view, MATCH_PARENT, MATCH_PARENT)
        }

        fun decorateTree(root: View, tag: String, @LayoutRes decoration: Int, inflater: LayoutInflater = LayoutInflater.from(root.context)) {
            if (tag == root.tag) decorateOne(root, decoration, inflater)
            else if (root is ViewGroup)
                for (i in 0..root.childCount - 1)
                    decorateTree(root.getChildAt(i), tag, decoration, inflater)
        }

        fun decorateTree(root: View, decorations: Map<String, Int>, inflater: LayoutInflater = LayoutInflater.from(root.context)) {
            val d = decorations[root.tag.toString()]
            if (d !== null)
                decorateOne(root, d, inflater)
            else if (root is ViewGroup)
                for (i in 0..root.childCount - 1)
                    decorateTree(root.getChildAt(i), decorations, inflater)
        }
}
