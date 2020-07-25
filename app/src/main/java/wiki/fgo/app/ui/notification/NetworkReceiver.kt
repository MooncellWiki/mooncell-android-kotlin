package wiki.fgo.app.ui.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import wiki.fgo.app.ui.activity.BaseActivity

class NetworkReceiver : BroadcastReceiver() {
    private val isConn = false
    override fun onReceive(context: Context, intent: Intent) {
        println("网络状态发生变化")

        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            //获取WIFI连接的信息
            val wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            //获取移动数据连接的信息
            val dataNetworkInfo =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (wifiNetworkInfo.isConnected or dataNetworkInfo.isConnected) {
                println("有网了")

                //立即回调注册
                println("回调立即注册推送服务")
                val msg = BaseActivity.AppHandler!!.obtainMessage()
                msg.what = 0
                BaseActivity.AppHandler!!.sendMessage(msg)
            } else {
                println("没网了")
                BaseActivity.pushState = 1
            }

            //API大于23时使用下面的方式进行网络监听
        } else {
            println("API level 大于23")
            //获得ConnectivityManager对象
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            //获取所有网络连接的信息
            val networks = connMgr.allNetworks
            //用于存放网络连接信息
            val sb = StringBuilder()
            //通过循环将网络信息逐个取出来
            for (i in networks.indices) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                val networkInfo = connMgr.getNetworkInfo(networks[i])
                if (networkInfo.isConnected) {
                    //立即回调注册
                    println("回调立即注册推送服务")
                    val msg = BaseActivity.AppHandler!!.obtainMessage()
                    msg.what = 0
                    BaseActivity.AppHandler!!.sendMessage(msg)
                    break
                } else {
                    println("没网了")
                    //没网时设置推送服务状态量
                    BaseActivity.pushState = 1
                }
            }
        }
    }
}