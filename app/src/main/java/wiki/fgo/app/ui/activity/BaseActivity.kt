package wiki.fgo.app.ui.activity

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.Process
import com.xiaomi.mipush.sdk.MiPushClient
import wiki.fgo.app.BuildConfig
import wiki.fgo.app.ui.notification.NetworkReceiver


//主要要继承Application
class BaseActivity : Application() {
    //网络监听变量
    var netWorkStateReceiver: NetworkReceiver? = null

    //回调线程
    var mHandlerThread: HandlerThread? = null

    //为了提高推送服务的注册率，我建议在Application的onCreate中初始化推送服务
    //你也可以根据需要，在其他地方初始化推送服务
    override fun onCreate() {
        super.onCreate()

        //创建回调线程
        initBackground()
        if (shouldInit()) {
            //注册推送服务
            //注册成功后会向DemoMessageReceiver发送广播
            // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
            MiPushClient.registerPush(
                this,
                APP_ID,
                APP_KEY
            )
        }
    }

    //创建回调线程
    private fun initBackground() {
        //通过实例化mHandlerThread从而创建新线程
        mHandlerThread = HandlerThread("handlerThread")
        //启动新线程
        mHandlerThread!!.start()
        //消息处理的操作
        AppHandler =
            object : Handler(mHandlerThread!!.looper) {
                override fun handleMessage(msg: Message) {

                    //0=立即注册推送服务,1=延时注册推送服务,2=注册广播,3=注销广播
                    when (msg.what) {
                        0 -> {
                            println("立即注册推送服务")
                            startPush()
                        }
                        1 -> {
                            println("正在回调延时(1分钟)注册推送服务....")
                            try {
                                Thread.sleep(10000)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            println("延时完毕,开始重新注册推送服务")
                            startPush()
                        }
                        2 -> {
                            println("正在回调注册广播...")
                            regBroadcastReceiver()
                        }
                        3 -> {
                            println("正在回调注销广播...")
                            unRegBroadcastReceiver()
                        }
                        else -> {
                        }
                    }
                }
            }
    }

    //通过判断手机里的所有进程是否有这个App的进程
    //从而判断该App是否有打开
    private fun shouldInit(): Boolean {

        //通过ActivityManager我们可以获得系统里正在运行的activities
        //包括进程(Process)等、应用程序/包、服务(Service)、任务(Task)信息。
        val am =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos =
            am.runningAppProcesses
        val mainProcessName = packageName

        //获取本App的唯一标识
        val myPid = Process.myPid()
        //利用一个增强for循环取出手机里的所有进程
        for (info in processInfos) {
            //通过比较进程的唯一标识和包名判断进程里是否存在该App
            if (info.pid == myPid && mainProcessName == info.processName) {
                return true
            }
        }
        return false
    }

    //注册推送服务
    fun startPush() {
        if (shouldInit() && pushState != 3) {
            println("注册推送服务")
            MiPushClient.registerPush(
                this,
                APP_ID,
                APP_KEY
            )
        }
    }

    //注册广播
    fun regBroadcastReceiver() {
        netWorkStateReceiver = NetworkReceiver()
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(netWorkStateReceiver, filter)
        println("注册广播成功")
        broadcastNet_State = 2
    }

    //注销广播
    fun unRegBroadcastReceiver() {
        try {
            unregisterReceiver(netWorkStateReceiver)
            println("注销广播成功")
            broadcastNet_State = 1
        }
        catch (e: IllegalArgumentException) {
            println("注销广播失败")
            e.printStackTrace()
        }
    }

    // 程序终止的时候执行
    override fun onTerminate() {
        println("程序终止的时候执行")
        releaseHandler()
        super.onTerminate()
    }

    //释放线程,防止内存泄露
    private fun releaseHandler() {
        AppHandler!!.removeCallbacksAndMessages(null)
    }

    companion object {

        private const val APP_ID = BuildConfig.MI_PUSH_APP_ID

        private const val APP_KEY = BuildConfig.MI_PUSH_APP_KEY

        //推送服务状态量
        var pushState = 1

        //网络检测注册广播状态量
        var broadcastNet_State = 1
        var AppHandler: Handler? = null
    }
}