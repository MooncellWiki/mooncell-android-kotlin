package wiki.fgo.app.httpRequest

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HTTPUtil {
    companion object {
        fun sendHttpRequest(address: String, callback: HttpCallback?) {
            Thread(object : Runnable{
                override fun run() {
                    var connection: HttpURLConnection? = null
                    try {
                        var url = URL(address)
                        connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.connectTimeout = 8000
                        connection.readTimeout = 8000
                        connection.doInput = true
                        connection.doOutput = true
                        var inStream = connection.inputStream
                        var reader = BufferedReader(InputStreamReader(inStream))
                        var responseData = StringBuilder()
                        var allText = reader.use(BufferedReader::readText)
                        responseData.append(allText)
                        if(callback!=null) {
                            callback.onFinish(responseData.toString())
                        }
                    }catch (ex: Exception) {
                        if(callback!=null) {
                            callback.onError(ex)
                        }
                    }finally {
                        if(connection!=null) {
                            connection.disconnect()
                        }
                    }
                }
            }).start()
        }


        fun sendOkHttpRequest(address: String, callback: Callback?) {
            var client = OkHttpClient()
            var request = Request.Builder().
            url(address).
            build()

            client.newCall(request).enqueue(callback)
        }
    }
}