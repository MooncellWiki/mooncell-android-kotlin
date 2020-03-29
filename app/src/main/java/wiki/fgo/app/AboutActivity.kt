package wiki.fgo.app

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener(fun(_: View) {
            finish()
        })
        val list = findViewById<RecyclerView>(R.id.licenses_list)
        list.adapter = MyAdapter(data)
        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(MyItemDecoration())
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
}
