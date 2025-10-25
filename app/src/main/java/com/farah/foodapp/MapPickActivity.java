package com.farah.foodapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.GeolocationPermissions;
import androidx.appcompat.app.AppCompatActivity;

public class MapPickActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        WebView webView = new WebView(this);
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("myapp://pick-location")) {
                    Uri uri = Uri.parse(url);
                    double lat = Double.parseDouble(uri.getQueryParameter("lat"));
                    double lon = Double.parseDouble(uri.getQueryParameter("lng"));
                    String address = uri.getQueryParameter("address");

                    Intent result = new Intent();
                    result.putExtra("pickedLat", lat);
                    result.putExtra("pickedLon", lon);
                    result.putExtra("pickedAddress", address);
                    setResult(RESULT_OK, result);
                    finish();
                    return true;
                }
                return false;
            }
        });

        String baseUrl = getString(R.string.map_url);
        String mapUrl = baseUrl + "/select-location/";
        webView.loadUrl(mapUrl);
    }
}
