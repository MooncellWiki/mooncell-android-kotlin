package wiki.fgo.app

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.*
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.webview.*
import wiki.fgo.app.McWebview.WebviewInit


class PageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_page, container, false)
        val page = arguments?.getInt(PAGE_NUM)
        val cacheDir = arguments?.getString("cacheDirPath")
        val tvHello: WebView = view.findViewById(R.id.webView_replica)

        if (cacheDir != null) {
            WebviewInit.setWebView(tvHello, cacheDir)
        }
        tvHello.webViewClient = object : WebViewClient() {}
        tvHello.webChromeClient = object : WebChromeClient() {
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
        tvHello.loadUrl("https://fgo.wiki/index.php?title=首页&mobileaction=toggle_view_mobile")
        return view
    }

    companion object {
        const val PAGE_NUM = "PAGE_NUM"
        fun newInstance(page: Int): PageFragment {
            val fragment = PageFragment()
            val args = Bundle()
            args.putInt(PAGE_NUM, page)
            fragment.arguments = args
            return fragment
        }
    }
}