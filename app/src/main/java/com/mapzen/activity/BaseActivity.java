package com.mapzen.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.mapzen.AutoCompleteCursor;
import com.mapzen.MapzenApplication;
import com.mapzen.R;
import com.mapzen.adapters.AutoCompleteAdapter;
import com.mapzen.entity.Feature;
import com.mapzen.fragment.MapFragment;
import com.mapzen.fragment.SearchResultsFragment;

import org.oscim.android.MapActivity;
import org.oscim.map.Map;

import java.util.ArrayList;

import static com.mapzen.MapzenApplication.LOG_TAG;

public class BaseActivity extends MapActivity
        implements MenuItem.OnActionExpandListener {
    private AutoCompleteAdapter autoCompleteAdapter;
    private MenuItem menuItem;
    private MapzenApplication app;
    private MapFragment mapFragment;
    private SearchResultsFragment searchResultsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        app = MapzenApplication.getApp(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        setContentView(R.layout.base);
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
        searchResultsFragment = (SearchResultsFragment) fragmentManager.findFragmentById(R.id.search_results_fragment);
        // TODO remove fugly HACK
        searchResultsFragment.setMapFragment(mapFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                ArrayList<Feature> features = bundle.getParcelableArrayList("features");
                int pos = app.getCurrentPagerPosition();
                searchResultsFragment.setSearchResults(features, pos);
                Feature feature = bundle.getParcelable("feature");
                if (feature != null) {
                    showPlace(feature, false);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        menuItem = menu.findItem(R.id.search);
        menuItem.setOnActionExpandListener(this);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        setupAdapter(searchView);
        searchView.setOnQueryTextListener(autoCompleteAdapter);
        if (!app.getCurrentSearchTerm().isEmpty()) {
            menuItem.expandActionView();
            Log.v(LOG_TAG, "search: " + app.getCurrentSearchTerm());
            searchView.setQuery(app.getCurrentSearchTerm(), false);
            searchView.clearFocus();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        searchResultsFragment.hideResultsWrapper();
        return true;
    }

    public Map getMap() {
        return mMap;
    }

    public SearchView getSearchView() {
        return (SearchView) menuItem.getActionView();
    }

    private void setupAdapter(SearchView searchView) {
        if (autoCompleteAdapter == null) {
            autoCompleteAdapter = new AutoCompleteAdapter(getActionBar().getThemedContext());
            autoCompleteAdapter.setSearchView(searchView);
            autoCompleteAdapter.setMapFragment(mapFragment);
            autoCompleteAdapter.setSearchResultsFragment(searchResultsFragment);
        }
        searchView.setSuggestionsAdapter(autoCompleteAdapter);
    }

    private void clearSearchText() {
        app.setCurrentSearchTerm("");
        final SearchView searchView = (SearchView) menuItem.getActionView();
        assert searchView != null;
        searchView.setQuery("", false);
        searchView.clearFocus();
    }

    public void showPlace(Feature feature, boolean clearSearch) {
        searchResultsFragment.flipTo(feature);
        if (clearSearch) {
            clearSearchText();
        }
        mapFragment.centerOn(feature);
    }
}
