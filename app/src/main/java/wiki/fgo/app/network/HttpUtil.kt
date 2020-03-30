package wiki.fgo.app.network

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpUtil {
    companion object {
        fun urlConcat(title: String): String {
            return "https://fgo.wiki/w/$title"
        }

        fun avatarUrlConcat(userId: String): String {
            return "http://avatar.mooncell.wiki/mc/$userId/original.png"
        }

        fun requestAvatarFromServer(loggedUserId: String?) {
            val avatarUrl = loggedUserId?.let { avatarUrlConcat(it) }
            var conn: HttpURLConnection? = null
            try {
                conn = URL(avatarUrl).openConnection() as HttpURLConnection
                conn.connect()
                conn.inputStream.use { input ->
                    BufferedOutputStream(FileOutputStream("./download.png")).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                conn?.disconnect()
            }
        }

        fun sendHttpRequest(address: String, callback: HttpCallback?) {
            Thread(Runnable {
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
                    callback?.onFinish(responseData.toString())
                }catch (ex: Exception) {
                    callback?.onError(ex)
                }finally {
                    connection?.disconnect()
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