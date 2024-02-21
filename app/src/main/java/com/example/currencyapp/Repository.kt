package com.example.currencyapp

import retrofit2.Response

class Repository (private val client: ApiClient) {
    suspend fun getCurrencyByName(name:String):Response<BitcoinResponse> {
        val apiInterface = client.client.create(ApiInterface::class.java)
        return apiInterface.getCryptoByName(name)
    }
}
