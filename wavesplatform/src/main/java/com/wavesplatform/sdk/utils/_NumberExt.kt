package com.wavesplatform.sdk.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.unscaledLong(decimals: Int): Long {
    return setScale(decimals, RoundingMode.HALF_EVEN).unscaledValue().toLong()
}
