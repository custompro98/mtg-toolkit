package com.custompro98.mtgtoolkit.asyncTasks

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.View
import com.custompro98.mtgtoolkit.activities.MainActivity
import com.custompro98.mtgtoolkit.enums.ServiceName
import com.custompro98.mtgtoolkit.services.AmazonRekognitionService
import com.custompro98.mtgtoolkit.services.MLKitService
import com.custompro98.mtgtoolkit.services.ParsingService
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

@SuppressLint("StaticFieldLeak")
class ParseImageTask(private var activity: MainActivity, imagePath: String, serviceToUse: ServiceName) : AsyncTask<Void, Void, Unit>() {
    private val image: File
    private val parsingService: ParsingService?

    init {
        this.image = File(imagePath)
        this.parsingService = when (serviceToUse) {
            ServiceName.MLKIT -> MLKitService(image, activity.applicationContext)
            ServiceName.REKOGNITION -> AmazonRekognitionService(image, activity.applicationContext)
            ServiceName.NONE -> null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()

        activity.cardName.visibility = View.INVISIBLE
        activity.progressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg p0: Void?) {
        try {
            parsingService?.parse { cardName ->
                FetchCardTask(activity, cardName).execute()
                activity.runOnUiThread {
                    activity.cardName.text = cardName
                    activity.progressBar.visibility = View.INVISIBLE
                    activity.cardName.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            image.delete()
        }
    }
}