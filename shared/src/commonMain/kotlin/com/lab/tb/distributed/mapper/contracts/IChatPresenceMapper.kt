package com.lab.tb.distributed.mapper.contracts

import com.lab.tb.distributed.database.models.ChatPresenceDb
import com.lab.tb.distributed.mapper.base.IDbEntityMapper
import com.lab.tb.distributed.model.ChatPresence

interface IChatPresenceMapper: IDbEntityMapper<ChatPresenceDb, ChatPresence>