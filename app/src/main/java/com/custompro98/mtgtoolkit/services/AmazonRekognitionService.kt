package com.custompro98.mtgtoolkit.services

import android.content.Context
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.rekognition.AmazonRekognitionClient
import com.amazonaws.services.rekognition.model.DetectTextRequest
import com.amazonaws.services.rekognition.model.Image
import com.custompro98.mtgtoolkit.callbacks.ParsingCallback
import java.io.File
import java.nio.ByteBuffer

class AmazonRekognitionService(image: File, private var context: Context) : ParsingService {
    private val byteBuffer: ByteBuffer
    private val awsCredentialsProvider: AWSCredentialsProvider
    private val amazonRekognitionClient: AmazonRekognitionClient

    init {
        this.byteBuffer = ByteBuffer.wrap(image.readBytes())
        this.awsCredentialsProvider = getAwsCredentialsProvider()
        this.amazonRekognitionClient = AmazonRekognitionClient(awsCredentialsProvider)
    }

    override fun parse(callback: ParsingCallback) {
        val detectTextRequest = DetectTextRequest()
                .withImage(Image()
                        .withBytes(byteBuffer))

        val detectTextResult = amazonRekognitionClient.detectText(detectTextRequest)
        val probableCardName = detectTextResult.textDetections.find { textDetection ->
            textDetection.detectedText.length > 1
        }

        callback.onParsed(probableCardName?.detectedText ?: "No card title found")
    }

    private fun getAwsCredentialsProvider(): AWSCredentialsProvider {
        return CognitoCachingCredentialsProvider(
                context,
                "us-east-2:1c8019f2-99e6-481e-934c-94fcaaacaf72",
                Regions.US_EAST_2
        )
    }
}