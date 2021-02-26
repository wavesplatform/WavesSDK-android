package com.wavesplatform.sdk.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.unscaledLong(decimals: Int): Long {
    return setScale(decimals, RoundingMode.HALF_EVEN).unscaledValue().toLong()
}

fun BigDecimal.unscaledLong(priceDecimals: Int, amountDecimals: Int): Long {
    return setScale(8 + priceDecimals - amountDecimals, RoundingMode.HALF_EVEN)
        .unscaledValue().toLong()
}

fun BigDecimal?.toDoubleOrZero(): Double = this?.toDouble() ?: 0.0
