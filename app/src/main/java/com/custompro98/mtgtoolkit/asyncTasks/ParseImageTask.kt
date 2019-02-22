package com.custompro98.mtgtoolkit.asyncTasks

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.View
import com.custompro98.mtgtoolkit.activities.MainActivity
import com.custompro98.mtgtoolkit.enums.ServiceName
import com.custompro98.mtgtoolkit.services.AmazonRekognitionService
import com.custompro98.mtgtoolkit.services.MLKitService
import com.custompro98.mtgtoolkit.services.ParsingService
import com.squareup.picasso.Picasso
import io.magicthegathering.kotlinsdk.api.MtgCardApiClient
import io.magicthegathering.kotlinsdk.model.card.MtgCard
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
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
                GlobalScope.launch(Dispatchers.Main) {
                    val card = withContext(Dispatchers.IO) { fetchCard(cardName) }
                    Picasso.get().load(card?.imageUrl).into(activity.imageView)
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

    private fun fetchCard(cardName: String): MtgCard? {
        val pageSize = 1
        val page = 1

        var returnValue: MtgCard? = null

        val cardsResponse: Response<List<MtgCard>> = getCardByName(cardName, pageSize, page)
        val cards = cardsResponse.body()

        if (cards != null && cards.size > 0) {
            returnValue = cards.first()
        }

        return returnValue
    }

    private fun getCardByName(exactName: String, pageSize: Int, page: Int): Response<List<MtgCard>> {
        return MtgCardApiClient.getCardsByExactName(exactName, pageSize, page)
    }
}