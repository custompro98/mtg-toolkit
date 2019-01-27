package com.custompro98.mtgtoolkit

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.View
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.rekognition.AmazonRekognitionClient
import com.amazonaws.services.rekognition.model.DetectTextRequest
import com.amazonaws.services.rekognition.model.Image
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.nio.ByteBuffer

@SuppressLint("StaticFieldLeak")
class ParseImageTask(private var activity: MainActivity, private var imagePath: String) : AsyncTask<Void, Void, String>() {
    override fun onPreExecute() {
        super.onPreExecute()

        activity.textView.visibility = View.INVISIBLE
        activity.progressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg p0: Void?): String {
        val file = File(imagePath)
        val byteBuffer = ByteBuffer.wrap(file.readBytes())

        val awsCredentials = CognitoCachingCredentialsProvider(
                activity.applicationContext,
                "us-east-2:1c8019f2-99e6-481e-934c-94fcaaacaf72",
                Regions.US_EAST_2
        )

        val amazonRekognitionClient = AmazonRekognitionClient(awsCredentials)
        val detectTextRequest = DetectTextRequest()
                .withImage(Image()
                        .withBytes(byteBuffer))

        var returnValue: String

        try {
            val result = amazonRekognitionClient.detectText(detectTextRequest)
            val probableCardName = result.textDetections.find { textDetection ->
                textDetection.detectedText.length > 1
            }

            returnValue = probableCardName?.detectedText ?: "No card title found"
        } catch (e: Exception) {
            e.printStackTrace()

            returnValue = "Error!"
        } finally {
            file.delete()
        }

        return returnValue
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        activity.textView.text = result
        activity.progressBar.visibility = View.INVISIBLE
        activity.textView.visibility = View.VISIBLE
    }
}