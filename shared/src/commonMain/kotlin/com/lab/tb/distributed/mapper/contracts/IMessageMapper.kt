package com.lab.tb.distributed.mapper.contracts

import com.lab.tb.distributed.database.models.MessageDb
import com.lab.tb.distributed.mapper.base.IDbEntityMapper
import com.lab.tb.distributed.model.Message

interface IMessageMapper: IDbEntityMapper<MessageDb, Message>