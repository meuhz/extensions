package com.example.demo.model

import java.math.BigDecimal

interface SimplePriceListener {
    fun onPriceChanged(price: BigDecimal)
}
