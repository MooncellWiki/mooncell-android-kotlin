package wiki.fgo.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import wiki.fgo.app.fragment.MultiWebViewFragment
import wiki.fgo.app.fragment.SwipeRefreshWebViewFragment

class LayoutAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    val fragments = mutableMapOf<Int, Fragment>()
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            val f = SwipeRefreshWebViewFragment()
            println("single!")
            fragments[0] = f
            f
        } else {
            val f = MultiWebViewFragment()
            println("multi!")
            fragments[1] = f
            f
        }
    }
}