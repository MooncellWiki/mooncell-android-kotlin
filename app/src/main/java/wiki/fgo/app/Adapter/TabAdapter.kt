package wiki.fgo.app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView
import wiki.fgo.app.McWebview.WebviewInit
import wiki.fgo.app.R
import wiki.fgo.app.ViewModel.ItemTabViewModel

class TabAdapter(private val context: Context, private val viewModel: ItemTabViewModel) :
    RecyclerView.Adapter<TabAdapter.TabViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TabViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.webview,
            parent, false
        )
        val webView = v.findViewById<WebView>(R.id.webView)
        WebviewInit.setWebView(webView, parent.context.cacheDir.path)
        webView.loadUrl("https://fgo.wiki/index.php?title=首页&mobileaction=toggle_view_mobile")
        return TabViewHolder(v)
    }

    override fun getItemCount(): Int = if (viewModel.size > 0) viewModel.size else 0

    override fun getItemId(position: Int): Long = viewModel.itemId(position)

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        val itemId = holder.itemId
        holder.bind(viewModel.getUrlById(itemId.toShort()))
    }

    inner class TabViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val webView: WebView = view.findViewById(R.id.webView)
        fun bind(url: String) {
            if (url != webView.url) {
                webView.loadUrl(url)
            }
        }
    }
}