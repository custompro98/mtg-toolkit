package com.custompro98.mtgtoolkit.services

import android.content.Context
import android.graphics.BitmapFactory
import com.custompro98.mtgtoolkit.callbacks.ParsingCallback
import com.custompro98.mtgtoolkit.extensions.rotate
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
