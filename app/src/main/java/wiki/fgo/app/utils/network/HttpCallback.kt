package wiki.fgo.app.utils.network

interface HttpCallback {
    fun onFinish(responseData: String)
    fun onError(ex: Exception)
}