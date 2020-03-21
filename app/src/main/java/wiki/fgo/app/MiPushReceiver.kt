//package wiki.fgo.app
//
//import android.text.TextUtils
//
//
//class DemoMessageReceiver : PushMessageReceiver() {
//    private var mRegId: String? = null
//    private val mResultCode: Long = -1
//    private val mReason: String? = null
//    private val mCommand: String? = null
//    private var mMessage: String? = null
//    private var mTopic: String? = null
//    private var mAlias: String? = null
//    private var mUserAccount: String? = null
//    private var mStartTime: String? = null
//    private var mEndTime: String? = null
//    fun onReceivePassThroughMessage(context: Context?, message: MiPushMessage) {
//        mMessage = message.getContent()
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic()
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias()
//        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
//            mUserAccount = message.getUserAccount()
//        }
//    }
//
//    fun onNotificationMessageClicked(context: Context?, message: MiPushMessage) {
//        mMessage = message.getContent()
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic()
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias()
//        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
//            mUserAccount = message.getUserAccount()
//        }
//    }
//
//    fun onNotificationMessageArrived(context: Context?, message: MiPushMessage) {
//        mMessage = message.getContent()
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic()
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias()
//        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
//            mUserAccount = message.getUserAccount()
//        }
//    }
//
//    fun onCommandResult(context: Context?, message: MiPushCommandMessage) {
//        val command: String = message.getCommand()
//        val arguments: List<String> = message.getCommandArguments()
//        val cmdArg1 =
//            if (arguments != null && arguments.size > 0) arguments[0] else null
//        val cmdArg2 =
//            if (arguments != null && arguments.size > 1) arguments[1] else null
//        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
//            if (message.getResultCode() === ErrorCode.SUCCESS) {
//                mRegId = cmdArg1
//            }
//        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
//            if (message.getResultCode() === ErrorCode.SUCCESS) {
//                mAlias = cmdArg1
//            }
//        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
//            if (message.getResultCode() === ErrorCode.SUCCESS) {
//                mAlias = cmdArg1
//            }
//        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
//            if (message.getResultCode() === ErrorCode.SUCCESS) {
//                mTopic = cmdArg1
//            }
//        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
//            if (message.getResultCode() === ErrorCode.SUCCESS) {
//                mTopic = cmdArg1
//            }
//        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
//            if (message.getResultCode() === ErrorCode.SUCCESS) {
//                mStartTime = cmdArg1
//                mEndTime = cmdArg2
//            }
//        }
//    }
//
//    fun onReceiveRegisterResult(context: Context?, message: MiPushCommandMessage) {
//        val command: String = message.getCommand()
//        val arguments: List<String> = message.getCommandArguments()
//        val cmdArg1 =
//            if (arguments != null && arguments.size > 0) arguments[0] else null
//        val cmdArg2 =
//            if (arguments != null && arguments.size > 1) arguments[1] else null
//        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
//            if (message.getResultCode() === ErrorCode.SUCCESS) {
//                mRegId = cmdArg1
//            }
//        }
//    }
//}