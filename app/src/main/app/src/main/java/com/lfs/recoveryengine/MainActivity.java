package com.lfs.recoveryengine;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

    private WebView webView;
    private TextView offlineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        hideSystemBars();

        FrameLayout root = new FrameLayout(this);

        webView = new WebView(this);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setBackgroundColor(Color.BLACK);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        webView.loadUrl("file:///android_asset/index.html");

        offlineView = new TextView(this);
        offlineView.setText(
                "NETWORK PROBLEM\n\n" +
                "Internet connection is required to use this app.\n\n" +
                "Turn on mobile data or Wi-Fi and try again."
        );
        offlineView.setTextColor(Color.WHITE);
        offlineView.setTextSize(17);
        offlineView.setGravity(android.view.Gravity.CENTER);
        offlineView.setBackgroundColor(Color.BLACK);
        offlineView.setVisibility(View.GONE);

        root.addView(webView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        root.addView(offlineView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        setContentView(root);

        checkInternet();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (cm == null) return false;

        Network network = cm.getActiveNetwork();

        if (network == null) return false;

        NetworkCapabilities capabilities =
                cm.getNetworkCapabilities(network);

        return capabilities != null &&
                capabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) &&
                capabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                );
    }

    private void checkInternet() {
        if (isInternetAvailable()) {
            webView.setVisibility(View.VISIBLE);
            offlineView.setVisibility(View.GONE);
        } else {
            webView.setVisibility(View.GONE);
            offlineView.setVisibility(View.VISIBLE);
        }
    }
