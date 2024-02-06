
package com.lab.tb.distributed.mapper

import com.lab.tb.distributed.database.models.MessageDb
import com.lab.tb.distributed.mapper.base.IDbEntityMapper
import com.lab.tb.distributed.mapper.impl.BleSettingsMapper
import com.lab.tb.distributed.mapper.impl.ChatPresenceMapper
import com.lab.tb.distributed.mapper.impl.ChatUserMapper
import com.lab.tb.distributed.mapper.impl.MessageMapper
import com.lab.tb.distributed.mapper.impl.PrivatePartMapper
import com.lab.tb.distributed.mapper.impl.ProfileMapper
import com.lab.tb.distributed.mapper.impl.PublicPartMapper
import com.lab.tb.distributed.model.BleSettings
import com.lab.tb.distributed.model.ChatPresence
import com.lab.tb.distributed.model.ChatUser
import com.lab.tb.distributed.model.Message
import com.lab.tb.distributed.model.PrivatePart
import com.lab.tb.distributed.model.Profile
import com.lab.tb.distributed.model.PublicPart

class MapperManager {

    companion object {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified E> getEntityToDbMapper(): IDbEntityMapper<Any, E> {
            return when (E::class) {
                BleSettings::class -> BleSettingsMapper()
                Profile::class -> ProfileMapper()
                ChatUser::class -> ChatUserMapper()
                ChatPresence::class -> ChatPresenceMapper()
                Message::class -> MessageMapper()
                PublicPart::class -> PublicPartMapper()
                PrivatePart::class -> PrivatePartMapper()
                else -> throw Exception("No mapper found for ${E::class}")
            } as IDbEntityMapper<Any, E>
        }
    }
}