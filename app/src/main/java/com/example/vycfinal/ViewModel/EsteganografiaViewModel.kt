package com.example.vycfinal.ViewModel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.UUID

class EsteganografiaViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    fun signOut() {
        auth.signOut()
    }

    fun uploadToStorage(uri: Uri, context: Context, type: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val uniqueImageName = UUID.randomUUID()
        val spaceRef: StorageReference = storageRef.child("images/$uniqueImageName.jpg")
        val byteArray: ByteArray? = context.contentResolver
            .openInputStream(uri)
            ?.use { it.readBytes() }
        byteArray?.let {
            val uploadTask = spaceRef.putBytes(byteArray)
            uploadTask.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Upload failed",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnSuccessListener {
                Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun hideTextInImage(bitmap: Bitmap, context: Context, text: String) {
        val binaryText = text.toByteArray(Charsets.UTF_8).joinToString("") { byte ->
            byte.toString(2).padStart(8, '0')
        } + "00000000"

        if (binaryText.length > bitmap.width * bitmap.height) {
            throw IllegalArgumentException("The text is too large to hide in the given image.")
        }

        var textIndex = 0
        val mutableImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until mutableImage.height) {
            for (x in 0 until mutableImage.width) {
                if (textIndex >= binaryText.length) break

                val pixel = mutableImage.getPixel(x, y)
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF

                val lsb = binaryText[textIndex].toString().toInt()
                val newBlue = (blue and 0xFE) or lsb

                val newPixel = (0xFF shl 24) or (red shl 16) or (green shl 8) or newBlue
                mutableImage.setPixel(x, y, newPixel)

                textIndex++
            }
            if (textIndex >= binaryText.length) break
        }

        saveImageToGallery(context, mutableImage)
    }

    private fun saveImageToGallery(context: Context, bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "processed_image_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
            outputStream?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                Toast.makeText(context, "Imagen guardada en la galería.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "Error al guardar la imagen.", Toast.LENGTH_SHORT).show()
        }
    }

    fun revealTextFromImage(bitmap: Bitmap): String {
        val binaryText = StringBuilder()

        loop@ for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                val blue = pixel and 0xFF
                val lsb = blue and 0x01

                binaryText.append(lsb)

                if (binaryText.length % 8 == 0 && binaryText.endsWith("00000000")) {
                    break@loop
                }
            }
        }

        val bytes = binaryText
            .substring(0, binaryText.length - 8)
            .chunked(8)
            .map { it.toInt(2).toByte() }
            .toByteArray()

        return String(bytes, Charsets.UTF_8)
    }
}