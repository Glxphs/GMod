package me.glxphs.gmod.features

import com.google.gson.Gson
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

object MythicWeightsLoader {
    lateinit var mythicWeights: MythicWeights

    fun fetchWeights() {
        val url = "https://raw.githubusercontent.com/RawFish69/Nori/main/data/mythic_weights.json"
        val httpClient = HttpClientBuilder.create().build()
        val httpGet = HttpGet(url)
        val response = httpClient.execute(httpGet)
        val jsonString = EntityUtils.toString(response.entity)
        val gson = Gson()
        mythicWeights = gson.fromJson(jsonString, MythicWeights::class.java)
        httpClient.close()
    }

    fun getWeightsMap(): Map<String, Map<String, Double>> {
        if (!::mythicWeights.isInitialized) return emptyMap()

        val dataJson = mythicWeights.Data
        val dataMap = mutableMapOf<String, MutableMap<String, Double>>()
        for ((outerKey, innerMap) in dataJson) {
            val mutableInnerMap = mutableMapOf<String, Double>()
            for ((innerKey, innerValue) in innerMap) {
                mutableInnerMap[innerKey] = innerValue.toDouble()
            }
            dataMap[outerKey] = mutableInnerMap
        }
        return dataMap
    }

    fun getUnidMap(): Map<String, String> {
        if (!::mythicWeights.isInitialized) return emptyMap()
        return mythicWeights.unid
    }

    data class MythicWeights(
        val Data: Map<String, Map<String, String>>,
        val unid: Map<String, String>
    )
}