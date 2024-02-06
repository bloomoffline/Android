package com.lab.tb.distributed.mapper.contracts

import com.lab.tb.distributed.database.models.ChatUserDb
import com.lab.tb.distributed.mapper.base.IDbEntityMapper
import com.lab.tb.distributed.model.ChatUser

interface IChatUserMapper : IDbEntityMapper<ChatUserDb, ChatUser>