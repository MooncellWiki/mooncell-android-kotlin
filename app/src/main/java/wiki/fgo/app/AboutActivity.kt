package wiki.fgo.app

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener(fun(_: View) {
            finish()
        })
        val list = findViewById<RecyclerView>(R.id.licenses_list)
        list.adapter = MyAdapter(data)
        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(MyItemDecoration())
        val list1 = findViewById<RecyclerView>(R.id.developers_list)
        list1.adapter = DeveloperAdapter(developerData)
        list1.layoutManager = LinearLayoutManager(this)
        list1.addItemDecoration(MyItemDecoration())
    }

    private val data = arrayOf(
        mapOf(
            "name" to "Glide",
            "source" to "https://github.com/bumptech/glide",
            "license" to "https://raw.githubusercontent.com/bumptech/glide/master/LICENSE"
        ),
        mapOf(
            "name" to "OkHttp",
            "source" to "https://github.com/square/okhttp",
            "license" to "https://raw.githubusercontent.com/square/okhttp/master/LICENSE.txt"
        ),
        mapOf(
            "name" to "FloatWindow",
            "source" to "https://github.com/yhaolpz/FloatWindow",
            "license" to "https://raw.githubusercontent.com/yhaolpz/FloatWindow/master/LICENSE.txt"
        ),
        mapOf(
            "name" to "EasyFloat",
            "source" to "https://github.com/princekin-f/EasyFloat",
            "license" to "https://raw.githubusercontent.com/princekin-f/EasyFloat/master/LICENSE"
        ),
        mapOf(
            "name" to "Demo_MiPush",
            "source" to "https://github.com/Carson-Ho/Demo_MiPush",
            "license" to "https://github.com/Carson-Ho/Demo_MiPush"
        )
    )

    private val developerData = arrayOf(
        mapOf(
            "name" to "StarHeartHunt",
            "avatar" to "http://avatar.mooncell.wiki/mc/558/128.png",
            "desc" to "Main Developer"
        ),
        mapOf(
            "name" to "夕舞八弦",
            "avatar" to "http://avatar.mooncell.wiki/mc/558/128.png",
            "desc" to "Main Developer"
        )
    )

    inner class MyAdapter(private val list: Array<Map<String, String>>) :
        RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.licenses_list_item, parent, false)
            return MyViewHolder(v)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.name.text = list[position]["name"]
            holder.source.text = list[position]["source"]
            holder.license.text = list[position]["license"]
            holder.itemView.setOnClickListener {
                val contentUrl: Uri = Uri.parse(holder.source.text as String?)
                val intent = Intent(Intent.ACTION_VIEW, contentUrl)
                startActivity(intent)
            }
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.dependent_name)
        val source: TextView = view.findViewById(R.id.dependent_source)
        val license: TextView = view.findViewById(R.id.dependent_license)
    }

    inner class MyItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(0, 0, 0, 3)
//            super.getItemOffsets(outRect, view, parent, state)
        }
    }

    inner class DeveloperAdapter(private val list1: Array<Map<String, String>>) :
        RecyclerView.Adapter<DeveloperViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeveloperViewHolder {
            val v: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.developers_list_item, parent, false)
            return DeveloperViewHolder(v)
        }

        override fun getItemCount(): Int {
            return list1.size
        }

        override fun onBindViewHolder(holder: DeveloperViewHolder, position: Int) {
            holder.avatar.setImageDrawable(resources.getDrawable(R.mipmap.ic_launcher, null))
            holder.name.text = list1[position]["name"]
            holder.desc.text = list1[position]["desc"]
        }
    }

    inner class DeveloperViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val name: TextView = view.findViewById(R.id.name)
        val desc: TextView = view.findViewById(R.id.desc)
    }
}
