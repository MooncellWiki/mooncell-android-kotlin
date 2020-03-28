package wiki.fgo.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.tab_layout_activity.*

class TabLayoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tab_layout_activity)

        toolBarLayout.title = "对比模式"

        val pageAdapter = PageAdapter(supportFragmentManager)
        val bundle = Bundle()
        bundle.putString("cacheDirPath", this.cacheDir.path)

        // create fragments from 0 to 3
        for (i in 0 until 4) {
            pageAdapter.add(PageFragment.newInstance(i), "对比栏$i", bundle)
        }

        view_pager.adapter = pageAdapter
        view_pager.offscreenPageLimit = 4
        tabs.setupWithViewPager(view_pager)
    }

    class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val tabNames: ArrayList<String> = ArrayList()
        private val fragments: ArrayList<Fragment> = ArrayList()

        fun add(
            fragment: Fragment,
            title: String,
            arguments: Bundle
        ) {
            tabNames.add(title)
            fragment.arguments = arguments
            fragments.add(fragment)
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabNames[position]
        }
    }
}