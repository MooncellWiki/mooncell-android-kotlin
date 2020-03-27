package wiki.fgo.app.WebView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import wiki.fgo.app.BuildConfig


class WebviewInit {
    companion object {
        private const val cssLayer: String =
            "javascript:var style = document.createElement(\"style\");style.type = \"text/css\";style.innerHTML=\".minerva-footer{display:none;}\";style.id=\"addStyle\";document.getElementsByTagName(\"HEAD\").item(0).appendChild(style);"

        @SuppressLint("SetJavaScriptEnabled")
        fun setWebView(
            webView: WebView,
            swipeLayout: SwipeRefreshLayout,
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

            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    swipeLayout.setProgressViewEndTarget(false, 250)
                    swipeLayout.isRefreshing = true
                    super.onPageStarted(view, url, favicon)
                    webView.loadUrl(cssLayer)
                    swipeLayout.isRefreshing = false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    webView.loadUrl(cssLayer)
                    super.onPageFinished(view, url)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (Uri.parse(url).host == "fgo.wiki") {
                        // This is my web site, so do not override; let my WebView load the page
                        return false
                    }
                    // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                    val uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(ctx, intent, null)
                    return true
                }
            }
            swipeLayout.setOnRefreshListener {
                webView.reload()
                webView.loadUrl(cssLayer)
            }
            webView.webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(
                    view: WebView,
                    dialog: Boolean,
                    userGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    val result: WebView.HitTestResult = view.hitTestResult
                    val data: String? = result.extra
                    val context = view.context
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                    context.startActivity(browserIntent)
                    return false
                }
            }
        }
    }
}