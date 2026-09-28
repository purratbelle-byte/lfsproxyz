# LFS Recovery Engine — Android WebView build

This project wraps the supplied HTML file in a fullscreen Android WebView.

Included:
- Local HTML asset
- JavaScript + DOM storage
- INTERNET + network-state permissions
- Native validated-internet check
- Fullscreen immersive UI
- Offline "NETWORK PROBLEM" screen
- GitHub Actions workflow that builds and signs an APK

IMPORTANT:
The workflow creates a temporary signing key. For an APK that you will distribute long-term, replace this with your own protected keystore/secrets.

The HTML remains a demo/simulation. Do not use it to collect real wallet seed phrases, private keys, or funds.
