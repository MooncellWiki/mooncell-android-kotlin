package wiki.fgo.app.ui.notification

import android.content.Context
import android.os.Message
import com.xiaomi.mipush.sdk.*
import wiki.fgo.app.ui.activity.BaseActivity

class MiPushBroadcast : PushMessageReceiver() {
    //透传消息到达客户端时调用
    //作用：可通过参数message从而获得透传消息，具体请看官方SDK文档
    override fun onReceivePassThroughMessage(
        context: Context,
        message: MiPushMessage
    ) {

        //打印消息方便测试
        println("透传消息到达了")
        println("透传消息是$message")
    }

    //通知消息到达客户端时调用
    //注：应用在前台时不弹出通知的通知消息到达客户端时也会回调函数
    //作用：通过参数message从而获得通知消息，具体请看官方SDK文档
    override fun onNotificationMessageArrived(
        context: Context,
        message: MiPushMessage
    ) {
        //打印消息方便测试
        println("通知消息到达了")
        println("通知消息是$message")
    }

    //用户手动点击通知栏消息时调用
    //注：应用在前台时不弹出通知的通知消息到达客户端时也会回调函数
    //作用：1. 通过参数message从而获得通知消息，具体请看官方SDK文档
    //2. 设置用户点击消息后打开应用 or 网页 or 其他页面
    override fun onNotificationMessageClicked(
        context: Context,
        message: MiPushMessage
    ) {

        //打印消息方便测试
        println("用户点击了通知消息")
        println("通知消息是$message")
        println("点击后,会进入应用")
    }

    //用来接收客户端向服务器发送命令后的响应结果。
    override fun onCommandResult(
        context: Context,
        message: MiPushCommandMessage
    ) {
        val command = message.command
        //        System.out.println(command );
        if (MiPushClient.COMMAND_REGISTER == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {
                //打印信息便于测试注册成功与否
//                System.out.println("注册成功");
            } else {
//                System.out.println("注册失败");
            }
        }
    }

    //用于接收客户端向服务器发送注册命令后的响应结果。
    override fun onReceiveRegisterResult(
        context: Context,
        message: MiPushCommandMessage
    ) {
        val command = message.command
        println("开始注册$command")
        if (MiPushClient.COMMAND_REGISTER == command) {
            if (message.resultCode == ErrorCode.SUCCESS.toLong()) {

                //打印日志：注册成功
                println("推送服务注册成功")
                //广播是否已经注册了,2代表已注册
                if (BaseActivity.broadcastNet_State === 2) {

                    //通过回调注销广播
                    val msg: Message? = BaseActivity.AppHandler?.obtainMessage()
                    if (msg != null) {
                        msg.what = 3
                    }
                    BaseActivity.AppHandler?.sendMessage(msg)

                    //设置状态量:3 = 推送服务注册成功;1 = 广播还未注册(已被注销)
                    BaseActivity.pushState = 3
                    BaseActivity.broadcastNet_State = 1
                } else {
                    println("结束")
                    BaseActivity.pushState = 3
                    BaseActivity.broadcastNet_State = 1
                }
            } else {
                //打印日志：注册失败
                println("注册失败")
                BaseActivity.pushState = 2
                //通过回调延时注册广播
                println("回调延时注册广播")
                val msg: Message? = BaseActivity.AppHandler?.obtainMessage()
                if (msg != null) {
                    msg.what = 1
                }
                BaseActivity.AppHandler?.sendMessage(msg)
            }
        } else {
            println("其他情况" + message.reason)
        }
    }
}