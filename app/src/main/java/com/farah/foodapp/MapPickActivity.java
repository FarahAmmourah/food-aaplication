package com.farah.foodapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MapPickActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);

        String baseUrl = getString(R.string.map_url);
        String mapUrl = baseUrl + "/select-location/";

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

        webView.loadUrl(mapUrl);
    }
}
