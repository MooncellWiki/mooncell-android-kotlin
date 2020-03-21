package wiki.fgo.app.httpRequest

interface HttpCallback {
    fun onFinish(responseData: String)
    fun onError(ex: Exception)
}