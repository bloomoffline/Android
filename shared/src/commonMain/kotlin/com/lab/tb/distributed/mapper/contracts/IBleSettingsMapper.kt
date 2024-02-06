
package com.lab.tb.distributed.mapper.contracts

import com.lab.tb.distributed.database.models.BleSettingsDb
import com.lab.tb.distributed.mapper.base.IDbEntityMapper
import com.lab.tb.distributed.model.BleSettings

interface IBleSettingsMapper: IDbEntityMapper<BleSettingsDb, BleSettings>