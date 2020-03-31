package wiki.fgo.app.webview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import wiki.fgo.app.BuildConfig


class WebviewInit {
    companion object {
        @SuppressLint("SetJavaScriptEnabled")
        fun setWebView(
            webView: WebView,
            ctx: Context
        ) {
            // Get the web view settings instance
            val settings = webView.settings
            //5.0以上开启混合模式加载
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.javaScriptEnabled = true
            // Enable and setup web view cache
            settings.setAppCacheEnabled(true)
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.setAppCachePath(ctx.cacheDir.path)
            settings.setSupportZoom(false)
            // Enable zooming in web view
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
            // Enable disable images in web view
            settings.blockNetworkImage = false
            // Whether the WebView should load image resources
            settings.loadsImagesAutomatically = true
            //设置UA
            settings.userAgentString =
                settings.userAgentString + " mooncellApp/" + BuildConfig.VERSION_NAME
            // More web view settings
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            // More optional settings, you can enable it by yourself
            settings.domStorageEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.loadWithOverviewMode = true
            settings.setGeolocationEnabled(true)
            settings.allowFileAccess = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(true)
            //webview setting
            webView.fitsSystemWindows = true
        }
    }
}