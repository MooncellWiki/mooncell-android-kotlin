package wiki.fgo.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import wiki.fgo.app.R
import wiki.fgo.app.adapter.TabAdapter

class MultiWebViewFragment() : Fragment() {
    private lateinit var pager: ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_multi, container, false)
        val tabLayout = layout.findViewById<TabLayout>(R.id.tab_layout)
        pager = layout.findViewById(R.id.vp_content)
        pager.adapter = TabAdapter(activity!!)
        pager.isUserInputEnabled = false
        pager.offscreenPageLimit = 4
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = "tab $position"
        }.attach()
        return layout
    }

    fun getCurrentWebViewFragment(): SwipeRefreshWebViewFragment? {
        return (pager.adapter as TabAdapter).fragments[pager.currentItem]
    }
}