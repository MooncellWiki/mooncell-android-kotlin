package wiki.fgo.app.HttpRequest

interface HttpCallback {
    fun onFinish(responseData: String)
    fun onError(ex: Exception)
}