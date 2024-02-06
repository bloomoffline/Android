package com.lab.tb.distributed.database

import com.lab.tb.distributed.database.models.BleSettingsDb
import com.lab.tb.distributed.database.models.ChatPresenceDb
import com.lab.tb.distributed.database.models.ChatUserDb
import com.lab.tb.distributed.database.models.MessageDb
import com.lab.tb.distributed.database.models.PrivatePartDb
import com.lab.tb.distributed.database.models.ProfileDb
import com.lab.tb.distributed.database.models.PublicPartDb
import com.lab.tb.distributed.model.BleSettings
import com.lab.tb.distributed.model.ChatPresence
import com.lab.tb.distributed.model.ChatUser
import com.lab.tb.distributed.model.Message
import com.lab.tb.distributed.model.PrivatePart
import com.lab.tb.distributed.model.Profile
import com.lab.tb.distributed.model.PublicPart
import kotlin.reflect.KClass
object DbConst {
    val mapper = mapOf<KClass<*>, KClass<*>>(
        BleSettings::class to BleSettingsDb::class,
        Profile::class to ProfileDb::class,
        ChatUser::class to ChatUserDb::class,
        ChatPresence::class to ChatPresenceDb::class,
        Message::class to MessageDb::class,
        PrivatePart::class to PrivatePartDb::class,
        PublicPart::class to PublicPartDb::class
    )

    val uniqueKey = "ID" // For Unique Table
}