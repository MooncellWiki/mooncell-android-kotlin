package wiki.fgo.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.net.Uri.decode
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.*
import android.webkit.*
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.lzf.easyfloat.interfaces.OnPermissionResult
import com.lzf.easyfloat.permission.PermissionUtils
import com.yhao.floatwindow.FloatWindow
import com.yhao.floatwindow.Screen
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.float_window.view.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.webview.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import wiki.fgo.app.HttpRequest.HttpUtil
import wiki.fgo.app.HttpRequest.HttpUtil.Companion.avatarUrlConcat
import wiki.fgo.app.HttpRequest.HttpUtil.Companion.urlConcat
import wiki.fgo.app.McWebview.WebviewInit
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var isHorizontal : Boolean = true

    private val sidebarFetchUrl =
        "https://fgo.wiki/api.php?action=parse&format=json&page=%E6%A8%A1%E6%9D%BF%3AMFSidebarAutoEvents/App&disablelimitreport=1"

    private val checkUpdateUrl =
        "https://fgo.wiki/images/wiki/merlin/client/update.json"

    var cookieMap = mutableMapOf<String, String>()

    var userName: String? = null

    var loggedUserId: String? = null

    var isFloatBallCreated: Boolean? = false

    private var cssLayer: String =
        "javascript:var style = document.createElement(\"style\");style.type = \"text/css\";style.innerHTML=\".minerva-footer{display:none;}\";style.id=\"addStyle\";document.getElementsByTagName(\"HEAD\").item(0).appendChild(style);"

    private var searchBaseUrl: String = "https://fgo.wiki/index.php?search="

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val MY_PERMISSIONS_MIPUSH_GROUP = 1

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_float -> {
            if (PermissionUtils.checkPermission(this)) {
                if (isFloatBallCreated == false) {
                    createFloatBall()
                    isFloatBallCreated = true
                }
                FloatWindow.get().hide()
                EasyFloat.with(this)
                    .setLayout(R.layout.float_window, OnInvokeView {
                        it.findViewById<WebView>(R.id.float_webView).setFloatWebView()
                        val url = webView.url
                        it.findViewById<WebView>(R.id.float_webView).loadUrl(url)
                        it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
                            EasyFloat.dismissAppFloat()
                            if (isFloatBallCreated == true) {
                                FloatWindow.get().show()
                                FloatWindow.destroy()
                                isFloatBallCreated = false
                            }
                        }
                        it.findViewById<CheckBox>(R.id.checkbox)
                            .setOnCheckedChangeListener { _, isChecked ->
                                EasyFloat.appFloatDragEnable(isChecked)
                            }
                        it.findViewById<ImageView>(R.id.ivFloatBallCheck).setOnClickListener {
                            EasyFloat.hideAppFloat()
                            FloatWindow.get().show()
                        }
                        val content = it.findViewById<RelativeLayout>(R.id.rlContent)
                        val params = content.layoutParams as FrameLayout.LayoutParams
                        it.findViewById<ScaleImage>(R.id.ivScale).onScaledListener =
                            object : ScaleImage.OnScaledListener {
                                override fun onScaled(x: Float, y: Float, event: MotionEvent) {
                                    params.width += x.toInt()
                                    params.height += y.toInt()
                                    content.layoutParams = params
                                }
                            }
                    })
                    .setShowPattern(ShowPattern.ALL_TIME)
                    .show()
            } else {
                AlertDialog.Builder(this)
                    .setMessage("若要使用悬浮窗功能，您需要授权Mooncell悬浮窗权限。")
                    .setPositiveButton("去开启") { _, _ ->
                        requestFloatPermission()
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .show()
            }
            true
        }

        R.id.action_login -> {
            webView.loadUrl("https://fgo.wiki/w/特殊:用户登录")
            true
        }

        R.id.action_notice -> {
            webView.loadUrl("https://fgo.wiki/w/特殊:通知")
            true
        }

        R.id.action_settings -> {
            webView.loadUrl("https://fgo.wiki/w/特殊:参数设置")
            true
        }

        R.id.action_about -> {
            webView.loadUrl("https://fgo.wiki/w/Mooncell:关于")
            true
        }

        R.id.action_exit -> {
            finish()
            true
        }

//TODO
//        R.id.action_favorite -> {
//            if (!isChecked) {
//                my_toolbar.menu.findItem(R.id.action_favorite).icon =
//                    ContextCompat.getDrawable(this, R.drawable.ic_action_favorite)
//                isChecked = true
//                Snackbar.make(webView, "收藏成功", Snackbar.LENGTH_SHORT).show()
//            } else {
//                my_toolbar.menu.findItem(R.id.action_favorite).icon =
//                    ContextCompat.getDrawable(this, R.drawable.ic_action_favorite_empty)
//                isChecked = false
//                Snackbar.make(webView, "取消收藏", Snackbar.LENGTH_SHORT).show()
//            }
//            true
//        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.edit_menu, menu)
        if (userName != null) {
            if (menu != null) {
                menu.findItem(R.id.action_notice).isVisible = true
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @SuppressLint("RtlHardcoded")
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack() && !drawer_layout.isDrawerOpen(
                Gravity.LEFT
            )
        ) {
            webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("RtlHardcoded")
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.LEFT)) {
            drawer_layout.closeDrawer(Gravity.LEFT)
        }
        if (!m_search_view.isIconified) {
            m_search_view.isIconified = true;
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_page -> closeDrawerAfterClick(item)
            R.id.svt_overview -> closeDrawerAfterClick(item)
            R.id.ce_overview -> closeDrawerAfterClick(item, "礼装图鉴")
            R.id.cc_overview -> closeDrawerAfterClick(item)
            R.id.weekly_mission -> closeDrawerAfterClick(item, "御主任务/周常")
            R.id.new_cards -> closeDrawerAfterClick(item, "模板:新增卡牌")
            R.id.simulate_gacha -> closeDrawerAfterClick(item, "抽卡模拟器")
            R.id.quest -> closeDrawerAfterClick(item)
            R.id.enemy_overview -> closeDrawerAfterClick(item)
            R.id.items_overview -> closeDrawerAfterClick(item)
            R.id.skill_overview -> closeDrawerAfterClick(item)
            R.id.mst_equip -> closeDrawerAfterClick(item)
            R.id.clothes_overview -> closeDrawerAfterClick(item)
            R.id.music_overview -> closeDrawerAfterClick(item)
            R.id.cv_overview -> closeDrawerAfterClick(item)
            R.id.illust_overview -> closeDrawerAfterClick(item)
            R.id.jp_client_dl -> closeDrawerAfterClick(item, "Mooncell:Jpclient")
            R.id.sponsor -> closeDrawerAfterClick(
                item,
                "Mooncell:如何帮助我们完善网站#除了贡献内容外，您还可以资助我们改善服务器资源"
            )
            R.id.faq -> closeDrawerAfterClick(item, "Mooncell:求助与建议")
            R.id.comment -> closeDrawerAfterClick(item, "Mooncell:评论须知")
            else -> activityClickListener(item)
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        setSupportActionBar(findViewById(R.id.my_toolbar))
        readLogUserPreference()
        initDrawer()
        setDrawer()
        swipeLayout.setColorSchemeResources(R.color.colorPrimary)
        sendRequestWithOkHttp(sidebarFetchUrl, 1)
        sendRequestWithOkHttp(checkUpdateUrl, 2)
        setQueryListener()
        supportActionBar?.setDisplayShowTitleEnabled(false)
        loadWebView()
    }

    override fun onResume() {
        super.onResume()
        //1 = 注册失败(没网);1 = 广播还没注册
        if (BaseActivity.pushState == 1 && BaseActivity.broadcastNet_State == 1) {
            println("在首页注册广播")
            val msg = BaseActivity.AppHandler!!.obtainMessage()
            msg.what = 2
            BaseActivity.AppHandler!!.sendMessage(msg)
        } else {
            println("不用在首页注册广播")
        }
    }

    override fun onPause() {
        super.onPause()
        //2 = 广播已注册
        if (BaseActivity.broadcastNet_State == 2) {
            println("在首页注销广播")
            val msg = BaseActivity.AppHandler!!.obtainMessage()
            msg.what = 3
            BaseActivity.AppHandler!!.sendMessage(msg)
        } else {
            println("不用在首页注销广播")
        }
    }

    private fun loadWebView() {
        val mainUrl = "https://fgo.wiki/index.php?title=首页&mobileaction=toggle_view_mobile"
        val cacheDirPath = cacheDir.path
        WebviewInit.setWebView(webView, cacheDirPath)
        webView.loadUrl(mainUrl)
        WebView.setWebContentsDebuggingEnabled(true)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                swipeLayout.setProgressViewEndTarget(false, 250)
                swipeLayout.isRefreshing = true
                super.onPageStarted(view, url, favicon)
                webView.loadUrl(cssLayer)
                swipeLayout.isRefreshing = false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl(cssLayer)
                try {
                    val cookieManager: CookieManager = CookieManager.getInstance()
                    if (cookieManager.getCookie(url) == null) {
                        println("cookie is null")
                    } else {
                        val cookieStr: String = cookieManager.getCookie(url)
                        val temp: List<String> = cookieStr.split(";")
                        for (ar1 in temp) {
                            val temp1 = ar1.split("=").toTypedArray()
                            cookieMap[temp1[0].replace(" ", "")] = temp1[1]
                        }
                        userName = cookieMap["my_wiki_fateUserName"]
                        loggedUserId = cookieMap["my_wiki_fateUserID"]
                        if (userName != null) {
                            try {
                                nav_header_title.text = decode(userName).toString()
                                writeLogUserPreference()
                            } catch (e: IllegalStateException) {
                                nav_header_title.text = decode("岸波白野").toString()
                                e.printStackTrace()
                            }
                        }
                        invalidateOptionsMenu()
                    }
                } catch (e: IllegalStateException) {
                    println("处理 IllegalStateException")
                    e.printStackTrace()
                }
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (Uri.parse(url).host == "fgo.wiki") {
                    // This is my web site, so do not override; let my WebView load the page
                    return false
                }
                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    startActivity(this)
                }
                return true
            }
        }

        swipeLayout.setOnRefreshListener {
            webView.reload()
            webView.loadUrl(cssLayer)
        }

        webView.webChromeClient = object : WebChromeClient() {
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
    }

    private fun setQueryListener() {
        m_search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchUrl = searchBaseUrl + query.toString()
                webView.loadUrl(searchUrl)
                m_search_view.setQuery("", false)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun initDrawer() {
        val headView: View = nav_view.inflateHeaderView(R.layout.nav_header)
        val headIv: ImageView = headView.findViewById(R.id.imageView) as ImageView
        headIv.setOnClickListener {
            if (loggedUserId !== null) {
                headIv.minimumHeight = 220
                headIv.minimumWidth = 220
                Glide.with(this)
                    .load(loggedUserId?.let { it1 -> avatarUrlConcat(it1) })
                    .transition(withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(headIv)
            }
        }
        if (loggedUserId !== null) {
            headIv.minimumHeight = 180
            headIv.minimumWidth = 180
            Glide.with(this)
                .load(loggedUserId?.let { it1 -> avatarUrlConcat(it1) })
                .transition(withCrossFade())
                .into(headIv)
        } else {
            Glide.with(this)
                .load(avatarUrlConcat("1145141919810"))
                .transition(withCrossFade())
                .into(headIv)
        }
    }

    private fun setDrawer() {
        nav_view.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            my_toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    @SuppressLint("RtlHardcoded")
    private fun closeDrawerAfterClick(item: MenuItem, custom: String? = null) {
        if (custom != null) {
            drawer_layout.closeDrawer(Gravity.LEFT)
            webView.loadUrl(urlConcat(custom))
        } else {
            drawer_layout.closeDrawer(Gravity.LEFT)
            webView.loadUrl(urlConcat(item.title.toString()))
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun activityClickListener(item: MenuItem) {
        drawer_layout.closeDrawer(Gravity.LEFT)
        webView.loadUrl(urlConcat(item.title.toString()))
    }

    private fun showSidebarResponse(stringList: List<String>) {
        runOnUiThread {
            val menu: Menu = nav_view.menu
            val subMenu: SubMenu = menu.addSubMenu(1, 1, 0, "当前活动")
            for (i in stringList) {
                val subStringList = i.split(",")
                subMenu.add(subStringList.elementAt(0))
            }
        }
    }

    fun parseSidebarJsonWithJsonObject(jsonData: String) {
        try {
            val json: JsonElement = Gson().fromJson(jsonData)
            val sourceText = json["parse"]["text"]["*"].toString()
            var result: String? = null
            val pattern = "<p>(.*)</p>"
            val matcher: Matcher = Pattern.compile(pattern).matcher(sourceText)

            Log.e("debug", json["parse"]["text"]["*"].toString())
            if (matcher.find()) {
                result = matcher.group(1)?.toString()
            }
            val resultArray = result?.replace("<br />\\n", "")?.split("<br />")
            if (resultArray != null) {
                return showSidebarResponse(resultArray)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun parseUpdateJsonWithJsonObject(jsonData: String) {
        try {
            val json: JsonElement = Gson().fromJson(jsonData)
            Log.e("debug", json.toString())

            val remoteVersionCode = json["remoteVersionCode"].toString().toInt()
            val localVersionCode = BuildConfig.VERSION_CODE
            val title = json["title"]
            val description = json["description"]

            if (remoteVersionCode > localVersionCode) {
                runOnUiThread {
                    AlertDialog.Builder(this)
                        .setTitle(title.asString)
                        .setMessage(description.asString)
                        .setPositiveButton("去更新") { _, _ ->
                            webView.loadUrl("https://fgo.wiki/w/Mooncell:Appclient")
                        }
                        .setNegativeButton("取消") { _, _ -> }
                        .show()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun sendRequestWithOkHttp(url: String, type: Int) {
        Thread(Runnable {
            try {
                HttpUtil.sendOkHttpRequest(
                    url,
                    object : Callback {

                        override fun onResponse(call: Call?, response: Response?) {
                            val responseData = response?.body()?.string()

                            when (type) {
                                1 -> parseSidebarJsonWithJsonObject(responseData!!)
                                2 -> parseUpdateJsonWithJsonObject(responseData!!)
                            }
                        }

                        override fun onFailure(call: Call?, e: IOException?) {
                            e?.printStackTrace()
                        }
                    })
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }).start()
    }

    private fun checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_PHONE_STATE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    MY_PERMISSIONS_MIPUSH_GROUP
                )
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    MY_PERMISSIONS_MIPUSH_GROUP
                )
            }
        } else {
            // Permission has already been granted
        }
    }

    private fun requestFloatPermission() {
        PermissionUtils.requestPermission(this, object : OnPermissionResult {
            override fun permissionResult(isOpen: Boolean) {
                Log.e("debug", isOpen.toString())
            }
        })
    }

    private fun writeLogUserPreference() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.local_log_userId), loggedUserId)
            apply()
        }
    }

    private fun readLogUserPreference() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val logDefaultValue = resources.getString(R.string.local_log_userId_default)
        loggedUserId = sharedPref.getString(getString(R.string.local_log_userId), logDefaultValue)
    }

    private fun createFloatBall() {
        val floatBall = ImageView(applicationContext)
        floatBall.setImageResource(R.mipmap.ic_float)

        FloatWindow
            .with(applicationContext)
            .setView(floatBall)
            .setWidth(150)
            .setHeight(Screen.width, 0.2f)
            .setX(300)
            .setY(Screen.height, 0.3f)
            .setDesktopShow(true)
            .build()

        floatBall.setOnClickListener {
            EasyFloat.showAppFloat()
            FloatWindow.get().hide()
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.setFloatWebView() {
    val settingsFloat = float_webView.settings
    settingsFloat.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    settingsFloat.javaScriptEnabled = true
    settingsFloat.setAppCacheEnabled(true)
    settingsFloat.cacheMode = WebSettings.LOAD_DEFAULT
    settingsFloat.setSupportZoom(false)
    settingsFloat.builtInZoomControls = false
    settingsFloat.displayZoomControls = false
    settingsFloat.blockNetworkImage = false
    settingsFloat.loadsImagesAutomatically = true
    settingsFloat.userAgentString =
        settingsFloat.userAgentString + " mooncellApp/" + BuildConfig.VERSION_NAME
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        settingsFloat.safeBrowsingEnabled = true
    }
    settingsFloat.useWideViewPort = true
    settingsFloat.loadWithOverviewMode = true
    settingsFloat.javaScriptCanOpenWindowsAutomatically = true
    settingsFloat.domStorageEnabled = true
    settingsFloat.setSupportMultipleWindows(true)
    settingsFloat.loadWithOverviewMode = true
    settingsFloat.setGeolocationEnabled(true)
    settingsFloat.allowFileAccess = true
    settingsFloat.javaScriptCanOpenWindowsAutomatically = true
    settingsFloat.setSupportMultipleWindows(true)
    float_webView.fitsSystemWindows = true

    float_webView.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (Uri.parse(url).host == "fgo.wiki") {
                return false
            }
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                Log.e("debug", "overwriteURL")
            }
            return true
        }
    }

    float_webView.webChromeClient = object : WebChromeClient() {
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
}
