package com.custompro98.mtgtoolkit.extensions

import android.graphics.Bitmap
import android.graphics.Matrix

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
