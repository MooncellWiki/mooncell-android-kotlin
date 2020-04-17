package wiki.fgo.app

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*


class MediaStoreHandler {
    companion object {
        private const val MY_PERMISSIONS_STORAGE_GROUP = 100

        fun addImageToGallery(saveImage: Bitmap, saveName: String, context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver

                val imagesCollection =
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

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
            } else {
                //for api version <= 28
                findActivity(context)?.let {
                    checkPermissions(context, it)
                }

                val storedImagePath: File = generateImagePath(saveName)

                if (!compressAndSaveImage(storedImagePath, saveImage)) {
                    Log.e("error","error occurred in save image")
                }

                addImageToGalleryLowOS(context.contentResolver, "png", storedImagePath)

//                MediaStore.Images.Media.insertImage(resolver, saveImage, saveName, "")
            }
        }

        private fun generateImagePath(title: String): File {
            return File(getImagesDirectory(), title)
        }

        private fun getImagesDirectory(): File? {
            val file =
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .toString() + File.separator + "/Mooncell"
                )
            if (!file.mkdirs() && !file.isDirectory) {
                Log.e("mkdir", "Directory not created")
            }
            return file
        }

        private fun compressAndSaveImage(file: File?, bitmap: Bitmap): Boolean {
            var result = false
            try {
                val fos = FileOutputStream(file)
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos).also { result = it }) {
                    Log.w("image manager", "Compression success")
                }
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return result
        }

        private fun addImageToGalleryLowOS(
            cr: ContentResolver,
            imgType: String,
            filepath: File
        ): Uri? {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "player")
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "player")
            values.put(MediaStore.Images.Media.DESCRIPTION, "")
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/$imgType")
            values.put(MediaStore.Images.Media.DATA, filepath.toString())
            return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }

        private fun checkPermissions(context: Context, activity: Activity) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        MY_PERMISSIONS_STORAGE_GROUP
                    )
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        MY_PERMISSIONS_STORAGE_GROUP
                    )
                }
            }
        }

        @Nullable
        fun findActivity(context: Context): Activity? {
            if (context is Activity) {
                return context
            }
            return if (context is ContextWrapper) {
                findActivity(context.baseContext)
            } else {
                null
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