package pl.mareklangiewicz.myintent;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Marek Langiewicz on 04.10.15.
 * Simple recent search suggestion provider.
 */
public class MISuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "pl.mareklangiewicz.myintent.MISuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MISuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}

