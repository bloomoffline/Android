package com.lab.tb.distributed.mapper.contracts

import com.lab.tb.distributed.database.models.ProfileDb
import com.lab.tb.distributed.mapper.base.IDbEntityMapper
import com.lab.tb.distributed.model.Profile

interface IProfileMapper: IDbEntityMapper<ProfileDb, Profile>