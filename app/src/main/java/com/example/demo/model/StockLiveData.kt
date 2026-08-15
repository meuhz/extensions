package com.example.demo.model

import androidx.lifecycle.LiveData
import java.math.BigDecimal

class StockLiveData(symbol: String) : LiveData<BigDecimal>(BigDecimal(0)) {
    private val stockManager: StockManager = StockManager(symbol)

    private val listener: SimplePriceListener = object : SimplePriceListener {
        override fun onPriceChanged(price: BigDecimal) {
            setValue(price)
        }
    }

    override fun onActive() {
        stockManager.requestPriceUpdate(listener)
    }

    override fun onInactive() {
        stockManager.removeUpdate(listener)
    }
}