package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.model.EditingState
import com.example.processor.ImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ImageSaver {

    suspend fun saveToGallery(
        context: Context,
        sourceBitmap: Bitmap,
        state: EditingState,
        quality: Int = 95
    ): Uri? = withContext(Dispatchers.IO) {
        val editedBitmap = ImageProcessor.applyAdjustments(sourceBitmap, state)
        val filename = "SumitEditor_${System.currentTimeMillis()}.jpg"

        val imageUri: Uri?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Sumit iPhone Editor")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            imageUri = context.contentResolver.insert(collection, values)

            if (imageUri != null) {
                context.contentResolver.openOutputStream(imageUri)?.use { out ->
                    editedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                }

                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(imageUri, values, null, null)
            }
        } else {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val editorFolder = File(picturesDir, "Sumit iPhone Editor").apply { mkdirs() }
            val file = File(editorFolder, filename)

            FileOutputStream(file).use { out ->
                editedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DATA, file.absolutePath)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }

        withContext(Dispatchers.Main) {
            if (imageUri != null) {
                Toast.makeText(context, "Saved to Pictures/Sumit iPhone Editor!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Failed to save image.", Toast.LENGTH_SHORT).show()
            }
        }

        imageUri
    }
}
