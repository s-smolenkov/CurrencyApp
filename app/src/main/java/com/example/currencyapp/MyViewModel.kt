package com.example.currencyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(val repo: Repository): ViewModel() {
    private val _uiState = MutableLiveData<UIState>(UIState.Empty)
    val uiState: LiveData<UIState> = _uiState

    fun getData() {
        _uiState.value = UIState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val bitcoin = async { repo.getCurrencyByName("bitcoin") }.await()
                    val ounce = async { repo.getCurrencyByName("silver-ounce") }.await()
                    val dash = async { repo.getCurrencyByName("dash") }.await()
                    val binance = async { repo.getCurrencyByName("binance-coin") }.await()
                    var result = ""

                    if (bitcoin.isSuccessful) {
                        val data = bitcoin.body()?.data
                        result += "${data?.id} ${data?.rateUsd}\n"
                    } else _uiState.postValue(UIState.Error("Error Code ${bitcoin.code()}"))

                    if (ounce.isSuccessful) {
                        val data = ounce.body()?.data
                        result += "${data?.id} ${data?.rateUsd}\n"
                    } else _uiState.postValue(UIState.Error("Error Code ${ounce.code()}"))

                    if (dash.isSuccessful) {
                        val data = dash.body()?.data
                        result += "${data?.id} ${data?.rateUsd}\n"
                    } else _uiState.postValue(UIState.Error("Error Code ${dash.code()}"))

                    if (binance.isSuccessful) {
                        val data = binance.body()?.data
                        result += "${data?.id} ${data?.rateUsd}\n"
                    } else _uiState.postValue(UIState.Error("Error Code ${binance.code()}"))

                    _uiState.postValue(UIState.Result(result))

                } catch (e: Exception) {
                    _uiState.postValue(UIState.Error(e.localizedMessage))
                }
            }
        }
    }

    sealed class UIState {
        object Empty : UIState()
        object Processing : UIState()
        class Result(val title: String) : UIState()
        class Error(val description: String) : UIState()
    }
}
