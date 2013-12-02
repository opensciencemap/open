package com.mapzen.activity;

import android.os.Bundle;

import com.mapzen.MapzenApplication;
import com.mapzen.R;
import org.oscim.android.MapActivity;
import org.oscim.android.MapView;
import org.oscim.layers.tile.vector.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.InternalRenderTheme;
import org.oscim.tiling.source.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class VectorMapActivity extends MapActivity {

    MapView mMapView;
    VectorTileLayer mBaseLayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vector);
        setupMap();
    }

    private void setupMap() {
        mMapView = (MapView) findViewById(R.id.map);
        TileSource tileSource = new OSciMap4TileSource();
        tileSource.setOption(getString(R.string.tiles_source_url_key), getString(R.string.tiles_source_url));
        mBaseLayer = mMap.setBaseMap(tileSource);
        mMap.getLayers().add(new BuildingLayer(mMap, mBaseLayer.getTileLayer()));
        mMap.getLayers().add(new LabelLayer(mMap, mBaseLayer.getTileLayer()));
        mMap.setTheme(InternalRenderTheme.DEFAULT);
        mMap.setMapPosition(MapzenApplication.getLocationPosition(this));
    }
}