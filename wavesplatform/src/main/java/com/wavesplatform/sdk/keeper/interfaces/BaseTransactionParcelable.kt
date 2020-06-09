/*
 * Created by Eduard Zaydel on 5/9/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.interfaces

import android.os.Parcel

interface BaseTransactionParcelable {
    fun writeBaseToParcel(parcel: Parcel)
    fun readBaseFromParcel(parcel: Parcel)
}
