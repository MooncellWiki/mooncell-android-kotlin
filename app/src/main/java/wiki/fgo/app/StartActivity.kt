package wiki.fgo.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import androidx.appcompat.app.AppCompatActivity


class StartActivity : AppCompatActivity() {
    private var mainHandler: Handler? = null
    private var workHandler: Handler? = null
    private var mHandlerThread: HandlerThread? = null
    private var time = 4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler()
        val intent = Intent(
            this@StartActivity,
            MainActivity::class.java
        )
        //跳转主页
        startActivity(intent)
        finish()

        //创建后台计时线程
        initBackground()
        workHandler!!.sendEmptyMessage(0x121)
    }

    private fun initBackground() {
        mHandlerThread = HandlerThread("countTime")

        //启动新线程
        mHandlerThread!!.start()
        workHandler = object : Handler(mHandlerThread!!.looper) {
            //消息处理的操作
            override fun handleMessage(msg: Message) {
                //设置了两种消息处理操作,通过msg来进行识别
                when (msg.what) {
                    0x121 -> {
                        if (time > 0) {

                            //通过主线程Handler.post方法进行在主线程的UI更新操作
                            //可能有人看到new了一个Runnable就以为是又开了一个新线程
                            //事实上并没有开启任何新线程，只是使run()方法体的代码抛到与mHandler相关联的线程中执行，我们知道mainHandler是与主线程关联的，所以更新TextView组件依然发生在主线程
                            mainHandler!!.post {
                                time--
                            }
                            val msg2 =
                                workHandler!!.obtainMessage()
                            msg2.what = 0x121
                            workHandler!!.sendMessageDelayed(msg2, 1000)
                        }
                        if (time == 0) {
                            val localIntent = Intent(
                                this@StartActivity,
                                MainActivity::class.java
                            )
                            //跳转主页
                            startActivity(localIntent)
                            finish()
                            return
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        workHandler!!.removeCallbacksAndMessages(null)
        mainHandler!!.removeCallbacksAndMessages(null)
    }
}