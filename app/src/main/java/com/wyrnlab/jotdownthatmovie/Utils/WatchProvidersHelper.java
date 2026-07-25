package com.wyrnlab.jotdownthatmovie.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.WatchProvidersAPI;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.conexion.SearchBaseUrl;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.WatchProviders.ModelCountryProviders;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.WatchProviders.ModelProvider;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.WatchProviders.ModelWatchProvidersResponse;
import com.wyrnlab.jotdownthatmovie.Model.Streaming;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.StreamingRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fetches TMDB's official watch/providers endpoint once (it returns every country in a single
 * response) and lets the user switch which country's results are shown locally, without any
 * extra network call.
 */
public class WatchProvidersHelper {

    public static void setup(Activity activity, String mediaId, String mediaType, LinearLayout streamingRowLayout,
                              Button searchButton, RecyclerView recyclerView, TextView countryLabel,
                              StreamingRecyclerViewAdapter.ItemClickListener clickListener) {
        streamingRowLayout.removeView(searchButton);

        // Provider logos are served relative to TMDB's configured image base URL; make sure
        // it has been fetched (it's not needed to load DB-cached posters, so it may still be null).
        if (General.base_url == null) {
            SearchBaseUrl searchBaseUrl = new SearchBaseUrl(activity) {
                @Override
                public void onResponseReceived(Object result) {
                    fetch(activity, mediaId, mediaType, recyclerView, countryLabel, clickListener);
                }
            };
            MyUtils.execute(searchBaseUrl);
        } else {
            fetch(activity, mediaId, mediaType, recyclerView, countryLabel, clickListener);
        }
    }

    private static void fetch(Activity activity, String mediaId, String mediaType, RecyclerView recyclerView,
                               TextView countryLabel, StreamingRecyclerViewAdapter.ItemClickListener clickListener) {
        WatchProvidersAPI searchor = new WatchProvidersAPI(activity, mediaId, mediaType, activity.getResources().getString(R.string.searching)) {
            @Override
            public void onResponseReceived(Object result) {
                ModelWatchProvidersResponse response = (ModelWatchProvidersResponse) result;
                Map<String, ModelCountryProviders> results = (response != null && response.results != null)
                        ? response.results
                        : Collections.emptyMap();
                render(activity, results, pickDefaultCountry(results), recyclerView, countryLabel, clickListener);
            }
        };
        MyUtils.execute(searchor);
    }

    private static void render(Activity activity, Map<String, ModelCountryProviders> results, String country,
                                RecyclerView recyclerView, TextView countryLabel,
                                StreamingRecyclerViewAdapter.ItemClickListener clickListener) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        StreamingRecyclerViewAdapter adapter = new StreamingRecyclerViewAdapter(activity, buildStreamingList(activity, results.get(country)));
        adapter.setClickListener(clickListener);
        recyclerView.setAdapter(adapter);

        if (country == null || results.isEmpty()) {
            countryLabel.setVisibility(View.GONE);
            return;
        }

        countryLabel.setVisibility(View.VISIBLE);
        countryLabel.setText(activity.getResources().getString(R.string.ChangeCountryFormat, displayName(country)));
        countryLabel.setOnClickListener(v -> showCountryPicker(activity, results, country, selected ->
                render(activity, results, selected, recyclerView, countryLabel, clickListener)));
    }

    private static List<Streaming> buildStreamingList(Activity activity, ModelCountryProviders countryProviders) {
        List<Streaming> streamingList = new ArrayList<>();
        if (countryProviders == null) {
            return streamingList;
        }
        addCategory(streamingList, countryProviders.flatrate, countryProviders.link, activity.getResources().getString(R.string.StreamingSubscription), false);
        addCategory(streamingList, countryProviders.free, countryProviders.link, activity.getResources().getString(R.string.StreamingFree), false);
        addCategory(streamingList, countryProviders.ads, countryProviders.link, activity.getResources().getString(R.string.StreamingAds), false);
        addCategory(streamingList, countryProviders.rent, countryProviders.link, activity.getResources().getString(R.string.StreamingRent), true);
        addCategory(streamingList, countryProviders.buy, countryProviders.link, activity.getResources().getString(R.string.StreamingBuy), true);
        return streamingList;
    }

    private static void addCategory(List<Streaming> streamingList, List<ModelProvider> providers, String link, String categoryLabel, boolean isPaid) {
        if (providers == null) {
            return;
        }
        for (ModelProvider provider : providers) {
            streamingList.add(new Streaming(provider, link, categoryLabel, isPaid));
        }
    }

    private static String pickDefaultCountry(Map<String, ModelCountryProviders> results) {
        if (results.isEmpty()) {
            return null;
        }
        String deviceCountry = Locale.getDefault().getCountry();
        if (!deviceCountry.isEmpty() && results.containsKey(deviceCountry)) {
            return deviceCountry;
        }
        if (results.containsKey("US")) {
            return "US";
        }
        return results.keySet().iterator().next();
    }

    private static String displayName(String countryCode) {
        return new Locale("", countryCode).getDisplayCountry();
    }

    private static void showCountryPicker(Activity activity, Map<String, ModelCountryProviders> results, String currentCountry, OnCountrySelected callback) {
        List<String> codes = new ArrayList<>(results.keySet());
        Collections.sort(codes, (a, b) -> displayName(a).compareToIgnoreCase(displayName(b)));

        String[] labels = new String[codes.size()];
        int checkedItem = -1;
        for (int i = 0; i < codes.size(); i++) {
            labels[i] = displayName(codes.get(i)) + " (" + codes.get(i) + ")";
            if (codes.get(i).equalsIgnoreCase(currentCountry)) {
                checkedItem = i;
            }
        }

        new AlertDialog.Builder(activity)
                .setTitle(R.string.SelectCountry)
                .setSingleChoiceItems(labels, checkedItem, (dialog, which) -> {
                    dialog.dismiss();
                    callback.onSelected(codes.get(which));
                })
                .setNegativeButton(R.string.Cancel, null)
                .show();
    }

    private interface OnCountrySelected {
        void onSelected(String countryCode);
    }
}
