package com.farah.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapPickActivity extends AppCompatActivity {
    private MapView map;
    private Button btnConfirm;
    private ProgressBar progress;
    private ExecutorService exec = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));
        setContentView(R.layout.activity_map_pick);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        btnConfirm = findViewById(R.id.btnConfirm);
        progress = findViewById(R.id.progress);

        GeoPoint startPoint = new GeoPoint(31.9539, 35.9106);
        map.getController().setZoom(14.0);
        map.getController().setCenter(startPoint);

        btnConfirm.setOnClickListener(v -> {
            GeoPoint center = (GeoPoint) map.getMapCenter();
            progress.setVisibility(View.VISIBLE);

            exec.execute(() -> {
                String address = reverseGeocode(center.getLatitude(), center.getLongitude());
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Intent result = new Intent();
                    result.putExtra("pickedLat", center.getLatitude());
                    result.putExtra("pickedLon", center.getLongitude());
                    result.putExtra("pickedAddress", address != null ? address : "");
                    setResult(RESULT_OK, result);
                    finish();
                });
            });
        });
    }

    private String reverseGeocode(double lat, double lon) {
        try {
            String urlStr = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat="
                    + URLEncoder.encode(String.valueOf(lat), "UTF-8")
                    + "&lon=" + URLEncoder.encode(String.valueOf(lon), "UTF-8");
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", getPackageName());
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (conn.getResponseCode() != 200) return null;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) sb.append(line);
            br.close();
            JSONObject obj = new JSONObject(sb.toString());
            if (obj.has("display_name")) return obj.getString("display_name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume(){ super.onResume(); map.onResume(); }
    @Override
    protected void onPause(){ super.onPause(); map.onPause(); }
}
