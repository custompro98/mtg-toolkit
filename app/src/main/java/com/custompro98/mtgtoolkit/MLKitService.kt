package com.custompro98.mtgtoolkit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import java.io.File

class MLKitService(image: File, private var context: Context) : ParsingService {
    private val firebaseVisionImage: FirebaseVisionImage
    private val firebaseVisionTextRecognizer: FirebaseVisionTextRecognizer

    init {
        val byteArray = image.readBytes()
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).rotate(90)
        this.firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)

        this.firebaseVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
    }

    override fun parse(callback: ParsingCallback) {
        firebaseVisionTextRecognizer
                .processImage(firebaseVisionImage)
                .addOnCompleteListener { firebaseVisionText ->
                    val result = when (firebaseVisionText.isSuccessful) {
                        true -> firebaseVisionText.result?.textBlocks?.first()?.text
                                ?: "No card title found"
                        false -> "No card title found"
                    }

                    firebaseVisionTextRecognizer.close()
                    callback.onParsed(result)
                }
    }
}

// Extension function to rotate a bitmap
fun Bitmap.rotate(degree: Int): Bitmap {
    // Initialize a new matrix
    val matrix = Matrix()

    // Rotate the bitmap
    matrix.postRotate(degree.toFloat())

    // Resize the bitmap
    val scaledBitmap = Bitmap.createScaledBitmap(
            this,
            width,
            height,
            true
    )

    // Create and return the rotated bitmap
    return Bitmap.createBitmap(
            scaledBitmap,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height,
            matrix,
            true
    )
}