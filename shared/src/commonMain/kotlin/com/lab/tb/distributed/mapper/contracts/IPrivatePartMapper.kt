package com.lab.tb.distributed.mapper.contracts

import com.lab.tb.distributed.database.models.PrivatePartDb
import com.lab.tb.distributed.mapper.base.IDbEntityMapper
import com.lab.tb.distributed.model.PrivatePart

interface IPrivatePartMapper: IDbEntityMapper<PrivatePartDb, PrivatePart>