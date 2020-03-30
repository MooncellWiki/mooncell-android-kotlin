package wiki.fgo.app.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.Uri.decode
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import wiki.fgo.app.McWebview.WebviewInit
import wiki.fgo.app.R
import wiki.fgo.app.viewModel.UserViewModel


class SwipeRefreshWebViewFragment() : Fragment() {
    private val cssLayer: String =
        "javascript:var style = document.createElement(\"style\");style.type = \"text/css\";style.innerHTML=\".minerva-footer{display:none;}.header-container{display:none;}\";style.id=\"addStyle\";document.getElementsByTagName(\"HEAD\").item(0).appendChild(style);"
    private val user: UserViewModel by activityViewModels()
    private val mainUrl = "https://fgo.wiki/index.php?title=首页&mobileaction=toggle_view_mobile"
    lateinit var webView: WebView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        swipeRefreshLayout =
            inflater.inflate(R.layout.swipe_refresh_webview, container, false) as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        webView = swipeRefreshLayout.findViewById(R.id.webView)
        WebviewInit.setWebView(webView, this.context!!)
        return swipeRefreshLayout
    }

    override fun onStart() {
        super.onStart()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                webView.isVisible = false
                swipeRefreshLayout.setProgressViewEndTarget(false, 250)
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
                swipeRefreshLayout.isRefreshing = true
                super.onPageStarted(view, url, favicon)
                webView.loadUrl(cssLayer)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl(cssLayer)
                webView.isVisible = true
                swipeRefreshLayout.isRefreshing = false
                val cookieManager: CookieManager = CookieManager.getInstance()
                if (cookieManager.getCookie(url) == null) {
                    println("cookie is null")
                } else {
                    try {
                        val cookieMap = mutableMapOf<String, String>()
                        val cookieStr: String = cookieManager.getCookie(url)
                        val temp: List<String> = cookieStr.split(";")
                        for (ar1 in temp) {
                            val temp1 = ar1.split("=").toTypedArray()
                            cookieMap[temp1[0].replace(" ", "")] = temp1[1]
                        }
                        if (cookieMap["my_wiki_fateUserName"] != null &&
                            user.getUserName().value != decode(cookieMap["my_wiki_fateUserName"])
                        ) {
                            user.userName(decode(cookieMap["my_wiki_fateUserName"]))
                        }
                        if (cookieMap["my_wiki_fateUserID"] != null &&
                            user.getUserId().value != decode(cookieMap["my_wiki_fateUserID"])
                        ) {
                            user.userId(decode(cookieMap["my_wiki_fateUserID"]))
                        }
                    } catch (e: IllegalStateException) {
                        println("处理IllegalStateException")
                    }
                }
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
                startActivity(intent, null)
                return true
            }
        }
        swipeRefreshLayout.setOnRefreshListener {
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
        webView.loadUrl(mainUrl)
    }
}