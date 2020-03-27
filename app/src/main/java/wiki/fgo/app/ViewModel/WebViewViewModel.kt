package wiki.fgo.app.ViewModel

import androidx.lifecycle.ViewModel

object WebViewViewModel : ViewModel() {
    private val mainUrl = "https://fgo.wiki/index.php?title=首页&mobileaction=toggle_view_mobile"
    private var initValue: Short = 0
    var items = (1..4).map { Bean(initValue++, mainUrl) }.toMutableList()
    fun getUrlById(id: Short): String = items[id.toInt()].Url
    fun itemId(position: Int): Long = items[position].id.toLong()
    val size: Int get() = items.size

    data class Bean(var id: Short, var Url: String) {
    }
}
