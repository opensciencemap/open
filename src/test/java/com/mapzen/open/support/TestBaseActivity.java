package com.mapzen.open.support;

import com.mapzen.android.lost.LocationClient;
import com.mapzen.open.activity.BaseActivity;

import org.mockito.Mockito;
import org.oscim.android.MapView;
import org.oscim.map.Map;
import org.oscim.map.TestMap;

import android.app.ActionBar;
import android.os.Bundle;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public class TestBaseActivity extends BaseActivity {
    private ActionBar actionBar = new TestActionBar();
    private boolean backPressed = false;
    private boolean optionsMenuInvalidated = false;
    private String debugDataEndpoint;
    private Map map = new TestMap();
    @Inject LocationClient locationClient;

    @Override
    public ActionBar getActionBar() {
        return actionBar;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapView mapView = Mockito.mock(MapView.class);
        Mockito.when(mapView.map()).thenReturn(new TestMap());
        registerMapView(mapView);
        actionBar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed = true;
    }

    public boolean isBackPressed() {
        return backPressed;
    }

    @Override
    public void supportInvalidateOptionsMenu() {
        super.supportInvalidateOptionsMenu();
        optionsMenuInvalidated = true;
    }

    public boolean isOptionsMenuInvalidated() {
        return optionsMenuInvalidated;
    }

    @Override
    public Map getMap() {
        return map;
    }

    @Override
    public void initDebugDataSubmitter() {
        super.initDebugDataSubmitter();
        debugDataSubmitter.setEndpoint(debugDataEndpoint);
    }

    public void setDebugDataEndpoint(String debugDataEndpoint) {
        this.debugDataEndpoint = debugDataEndpoint;
    }

    public void setDebugDataExecutor(Executor executor) {
        debugDataExecutor = executor;
    }

    public boolean actionBarIsEnabled() {
        return enableActionbar;
    }
}
