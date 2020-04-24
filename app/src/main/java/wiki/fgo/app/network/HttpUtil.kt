package wiki.fgo.app.network

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import wiki.fgo.app.MediaStoreHandler
import wiki.fgo.app.MediaStoreHandler.Companion.addImageToGallery
import wiki.fgo.app.MediaStoreHandler.Companion.findActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder


class HttpUtil {
    companion object {
        fun urlConcat(title: String): String {
            return "https://fgo.wiki/w/$title"
        }

        fun avatarUrlConcat(userId: String): String {
            return "http://avatar.mooncell.wiki/mc/$userId/original.png"
        }

        fun saveImageFromServer(imgUrl: String, context: Context) {
            findActivity(context)?.let {
                MediaStoreHandler.checkPermissions(context, it)
            }
            Thread {
                val bitmap: Bitmap = Glide.with(context)
                    .asBitmap()
                    .load(imgUrl)
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
                val saveTo: String = URLDecoder.decode(imgUrl.split("/").last(), "UTF-8")
                addImageToGallery(bitmap, saveTo, context)
            }.start()
            Toast.makeText(context, "图片已保存", Toast.LENGTH_SHORT).show()
        }

        fun sendHttpRequest(address: String, callback: HttpCallback?) {
            Thread(Runnable {
                var connection: HttpURLConnection? = null
                try {
                    val url = URL(address)
                    connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000
                    connection.doInput = true
                    connection.doOutput = true
                    val inStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inStream))
                    val responseData = StringBuilder()
                    val allText = reader.use(BufferedReader::readText)
                    responseData.append(allText)
                    callback?.onFinish(responseData.toString())
                } catch (ex: Exception) {
                    callback?.onError(ex)
                } finally {
                    connection?.disconnect()
                }
            }).start()
        }


        fun sendOkHttpRequest(address: String, callback: Callback?) {
            var client = OkHttpClient()
            var request = Request.Builder().url(address).build()

            client.newCall(request).enqueue(callback)
        }
    }
}