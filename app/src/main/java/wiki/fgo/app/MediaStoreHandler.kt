package wiki.fgo.app

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class MediaStoreHandler {
    companion object {
        fun addImageToGallery(saveImage: Bitmap, saveName: String, context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver

                val imagesCollection =
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                Log.e("test", imagesCollection.toString())

                val imagesDetail = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, saveName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/Mooncell"
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val imagesContentUri = resolver.insert(imagesCollection, imagesDetail)

                if (imagesContentUri != null) {
                    resolver.openFileDescriptor(imagesContentUri, "w", null).use { pfd ->
                        if (pfd != null) {
                            writeImageData(pfd.fileDescriptor, saveImage)
                        }
                    }
                }
                imagesDetail.clear()
                imagesDetail.put(MediaStore.Audio.Media.IS_PENDING, 0)
                if (imagesContentUri != null) {
                    resolver.update(imagesContentUri, imagesDetail, null, null)
                }
            }
        }

        private fun writeImageData(
            fileDescriptor: FileDescriptor,
            writeImage: Bitmap
        ) {
            val fos = FileOutputStream(fileDescriptor)
            try {
                writeImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                fos.close()
            }
        }

        private fun readData(fileDescriptor: FileDescriptor): String? {
            val fis = FileInputStream(fileDescriptor)
            val b = ByteArray(1024)
            var read: Int
            var content: String? = null
            try {
                while (fis.read(b).also { read = it } != -1) {
                    content = String(b, 0, read)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return content
        }
    }
}