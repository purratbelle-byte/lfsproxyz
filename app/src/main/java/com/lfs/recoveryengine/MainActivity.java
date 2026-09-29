package com.lfs.recoveryengine;

import android.app.Activity;
import android.os.Bundle;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceError;
import android.webkit.JavascriptInterface;
import android.provider.Settings;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private WebView webView;
    private FrameLayout root;
    private TextView offline;

    private boolean hasInternet() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network n = cm.getActiveNetwork();
        if (n == null) return false;
        NetworkCapabilities c = cm.getNetworkCapabilities(n);
        return c != null && c.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && c.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    private void showOffline(boolean show) {
        if (offline == null) return;
        offline.setVisibility(show ? View.VISIBLE : View.GONE);
        if (webView != null) webView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void loadWhenOnline() {
        if (hasInternet()) {
            showOffline(false);
            webView.loadUrl("file:///android_asset/index.html");
        } else {
            showOffline(true);
        }
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        getWindow().setStatusBarColor(Color.rgb(3,6,12));
        getWindow().setNavigationBarColor(Color.rgb(3,6,12));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        root = new FrameLayout(this);
        webView = new WebView(this);
        offline = new TextView(this);

        offline.setText("NETWORK PROBLEM\n\nInternet connection is required to use this app.\n\nTurn on mobile data or Wi-Fi and try again.");
        offline.setTextColor(Color.WHITE);
        offline.setTextSize(15);
        offline.setGravity(android.view.Gravity.CENTER);
        offline.setPadding(40, 40, 40, 40);
        offline.setBackgroundColor(Color.rgb(3,6,12));

        root.addView(webView, new FrameLayout.LayoutParams(-1,-1));
        root.addView(offline, new FrameLayout.LayoutParams(-1,-1));
        setContentView(root);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);

        webView.addJavascriptInterface(new DeviceBridge(), "Android");

        webView.setBackgroundColor(Color.rgb(3,6,12));
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onReceivedError(WebView v, WebResourceError e) {
                if (!hasInternet()) showOffline(true);
            }
        });

        loadWhenOnline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) loadWhenOnline();
    }

    private class DeviceBridge {
        @JavascriptInterface
        public String getDeviceId() {
            String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            return id == null ? "" : id;
        }
    }

    @Override
    public void onBackPressed() {
        // Keep the single-screen app locked to the WebView.
    }
}
