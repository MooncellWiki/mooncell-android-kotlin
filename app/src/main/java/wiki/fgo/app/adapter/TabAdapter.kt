package wiki.fgo.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import wiki.fgo.app.fragment.TabWebViewFragment

class TabAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    val fragments = arrayOfNulls<TabWebViewFragment>(4)
    override fun getItemCount(): Int = 4
    override fun createFragment(position: Int): Fragment {
        val f = TabWebViewFragment(position)
        fragments[position] = f
        return f
    }

}