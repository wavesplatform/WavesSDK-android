/*
 * Created by Eduard Zaydel on 27/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.model

import com.wavesplatform.sdk.keeper.interfaces.KeeperTransaction

data class KeeperProcessData(val actionType: KeeperActionType,
                             val dApp: DApp,
                             val transaction: KeeperTransaction?)