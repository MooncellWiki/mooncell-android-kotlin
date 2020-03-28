package wiki.fgo.app

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
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
        setHasOptionsMenu(true)
        val tvHello: WebView = view.findViewById(R.id.webView_replica)

        if (cacheDir != null) {
            WebviewInit.setWebView(tvHello, cacheDir)
        }
        tvHello.webViewClient = object : WebViewClient() {}
        tvHello.loadUrl("https://fgo.wiki")
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