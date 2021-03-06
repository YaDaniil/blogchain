package com.yadaniil.blogchain.data.api.models.cryptocompare

import com.google.gson.annotations.SerializedName

/**
 * Created by danielyakovlev on 9/20/17.
 */

class MinersResponse(
        @SerializedName("Response") val response: String,
        @SerializedName("Message") val message: String,
        @SerializedName("Type") val type: Int,
        @SerializedName("MiningData") val idToMiner: Map<String, Miner>
)