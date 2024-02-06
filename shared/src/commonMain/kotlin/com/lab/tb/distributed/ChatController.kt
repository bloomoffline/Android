package com.lab.tb.distributed

import com.lab.tb.distributed.base.JsonUtils
import com.lab.tb.distributed.base.Timer
import com.lab.tb.distributed.base.UUID
import com.lab.tb.distributed.database.DatabaseHelper
import com.lab.tb.distributed.database.DbConst.uniqueKey
import com.lab.tb.distributed.entities.ApplicationContext
import com.lab.tb.distributed.helper.AttachmentFileHelper
import com.lab.tb.distributed.helper.DiscoveryUserHelper
import com.lab.tb.distributed.model.AttachmentType
import com.lab.tb.distributed.model.ChatAttachment
import com.lab.tb.distributed.model.ChatChannelEnum
import com.lab.tb.distributed.model.ChatChannelSerialized
import com.lab.tb.distributed.model.ChatMessage
import com.lab.tb.distributed.model.ChatMessageContent
import com.lab.tb.distributed.model.ChatPresence
import com.lab.tb.distributed.model.ChatUser
import com.lab.tb.distributed.model.Message
import com.lab.tb.distributed.model.MessageView
import com.lab.tb.distributed.model.NearbyUser
import com.lab.tb.distributed.model.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ChatController {
    companion object {
        private val instance = ChatController()
        fun getInstance(): ChatController {
            return instance
        }

        private const val GLOBAL_ROOM = "global"
    }

    private val transport = BloomService.getInstance().getBleTransport()

    val database = DatabaseHelper.instance

    var profile = database.get<Profile>(uniqueKey) ?: Profile(ChatPresence(ChatUser())).also {
        database.set(it)
    }

    private val _nearbyUsers = MutableStateFlow<Map<String, NearbyUser>>(emptyMap())
    val nearbyUsers: StateFlow<Map<String, NearbyUser>> get() = _nearbyUsers

    private val _connectedUser = MutableStateFlow<Map<String, ChatPresence>>(emptyMap())
    val connectedUser: StateFlow<Map<String, ChatPresence>> get() = _connectedUser
    private val _roomMessage = MutableStateFlow<Map<String, MessageView>>(emptyMap())

    val roomMessage: StateFlow<Map<String, MessageView>> get() = _roomMessage
    var receivedMessage: ((displayName: String, content: String) -> Unit)? = null
    lateinit var context: ApplicationContext


    fun initialize (context: ApplicationContext) {
//        profile.me.publicPart = publicPart

        this.context = context
        initMessage()
//        initConnectedUser()
        transport?.onReceive { response ->
            val message = JsonUtils.decodeFromString<Message>(response)
            println()
            message.updatedPresences?.forEach { presence ->
//                database.set(presence)
                _connectedUser.update {
                    it + (presence.user.id to presence)
                }
            }

            if (!message.addedChatMessages.isNullOrEmpty()) {
                _roomMessage.update {
                    it + doLogicRoomMessage(message)
                }
            }


        }

        registerNearbyUser()
    }

    fun doRegisterPushLocalNoti(event: (displayName: String, content: String) -> Unit) {
        receivedMessage = event
    }

    private fun initMessage() {
        val messages = database.getAll<Message>()
        messages.forEach { message ->
            val lastMessage = message.addedChatMessages?.lastOrNull()
            val roomMessage = mutableMapOf<String, MessageView>()
            roomMessage[message.id] = MessageView(
                name = if (message.chatChannel == ChatChannelEnum.ROOM) "#${message.id}" else lastMessage?.author?.displayName
                    ?: "",
                bio = "${lastMessage?.author?.displayName}: ${lastMessage?.content?.data ?: "Welcome to the chat room"}" ,
                chatChannel = message.chatChannel ?: ChatChannelEnum.ROOM,
                isNewNotification = message.isNewNotification ?: true,
                isPinned = message.isPin ?: false,
                message = message
            )

            _roomMessage.update {
                it + roomMessage
            }
        }
        if (messages.isEmpty()) {
            createMessageRoom(ChatChannelEnum.ROOM, GLOBAL_ROOM)
        }
    }

    fun sendMessage(messageId: String, message: String, replyToMessageId: String? = null, chatAttachmentList: ArrayList<ChatAttachment>? = null) {
        val roomMessage = database.get<Message>(messageId) ?: return
        val chatContent = ChatMessageContent(
            data = message,
            type = "text"
        )

        val chatChannelSerialized = if (messageId == GLOBAL_ROOM) {
            null
        } else if (roomMessage.chatChannel == ChatChannelEnum.ROOM) {
            ChatChannelSerialized(
                type = "room",
                data = roomMessage.id
            )
        } else {
            ChatChannelSerialized(
                type = "dm",
                data = profile.me.id + "," + messageId
            )
        }

        val chatMessage = ChatMessage(
            id = UUID.randomUUID(),
            timestamp = Clock.System.now().epochSeconds.toDouble(),
            author = profile.me,
            content = chatContent,
            channelValue = chatChannelSerialized,
            repliedToMessageId = replyToMessageId,
            attachments = chatAttachmentList
        )

        val messageObject = Message(
            addedChatMessages = arrayListOf(chatMessage),
            sourceUserId = profile.me.id,
            timestamp = Clock.System.now().epochSeconds.toDouble(),
            logicalClock = profile.me.logicalClock
        )

        val result = JsonUtils.encodeToString(messageObject)
        transport?.broadcast("$result\n")

        _roomMessage.update {
            it + doLogicRoomMessage(messageObject)
        }
    }

    fun deleteMessage(id: String,  messageId: String) {
        val message = database.get<Message>(id)
        message?.let { nonNullMessage ->
            nonNullMessage.addedChatMessages?.removeAll { nonNullMessage.id == messageId }
            database.set(nonNullMessage)
            _roomMessage.update { view ->
                view + doLogicRoomMessage(nonNullMessage)
            }
        }
    }

    fun deleteChannel(id: String) {
        val message = database.get<Message>(id)
        message?.let { nonNullMessage ->
            if (nonNullMessage.id == GLOBAL_ROOM) {
                nonNullMessage.addedChatMessages?.clear()
                database.set(nonNullMessage)
                _roomMessage.update { view ->
                    view + doLogicRoomMessage(nonNullMessage)
                }
            } else {
                database.delete<Message>(id)
                _roomMessage.update { view ->
                    view.minus(id)
                }
            }
        }
    }

    fun setPinChannel(id: String, isPinned: Boolean) {
        val message = database.get<Message>(id)
        message?.let { nonNullMessage ->
            nonNullMessage.isPin = isPinned
            database.set(nonNullMessage)
            _roomMessage.update { data ->
                data.mapValues {
                    if (it.key == id) {
                        it.value.copy(isPinned = isPinned)
                    } else {
                        it.value
                    }
                }
            }
        }
    }

    private fun doLogicRoomMessage(
        message: Message,
    ): Map<String, MessageView> {
        val roomMessage = mutableMapOf<String, MessageView>()
        message.addedChatMessages?.forEach { chatMessage ->
            if (chatMessage.channelValue?.data == null) {
                message.id = GLOBAL_ROOM
                val dbMessage = database.get<Message>(message.id) ?: Message(
                    id = GLOBAL_ROOM,
                    addedChatMessages = arrayListOf(),
                    isPin = true,
                    isNewNotification = false,
                    chatChannel = ChatChannelEnum.ROOM
                )

                if (dbMessage.addedChatMessages?.find { it.id == chatMessage.id } == null) {
                    chatMessage.attachments?.let {
                        it.forEach { attachment ->
                            attachment.content?.data?.let { data ->
                                AttachmentFileHelper.decodeToFile(
                                    attachment.id ?: "",
                                    AttachmentType.get(attachment.type),
                                    data,
                                    attachment.name ?: "UnknownFileType",
                                    context
                                )
                            }
                        }
                    }

                    dbMessage.addedChatMessages?.add(chatMessage)
                    database.set(dbMessage)

                    receivedMessage?.invoke(
                        GLOBAL_ROOM,
                        chatMessage.content?.data ?: ""
                    )
                }

                roomMessage[GLOBAL_ROOM] = MessageView(
                    "#$GLOBAL_ROOM",
                    bio = "${chatMessage.author?.displayName}: ${chatMessage.content?.data ?: ""}" ,
                    chatChannel = dbMessage.chatChannel ?: ChatChannelEnum.ROOM,
                    isNewNotification = dbMessage.isNewNotification ?: true,
                    isPinned = dbMessage.isPin ?: false,
                    message = dbMessage
                )
            } else if (chatMessage.channelValue?.type == "room") {
                message.id = chatMessage.channelValue?.data ?: ""
                val dbMessage = database.get<Message>(message.id) ?: Message(
                    id = chatMessage.channelValue?.data ?: "",
                    addedChatMessages = arrayListOf(),
                    chatChannel = ChatChannelEnum.ROOM
                )

                if (dbMessage.addedChatMessages?.find { it.id == chatMessage.id } == null) {
                    chatMessage.attachments?.let {
                        it.forEach { attachment ->
                            attachment.content?.data?.let { data ->
                                AttachmentFileHelper.decodeToFile(
                                    attachment.id ?: "",
                                    AttachmentType.get(attachment.type),
                                    data,
                                    attachment.name ?: "UnknownFileType",
                                    context
                                )
                            }
                        }
                    }

                    dbMessage.addedChatMessages?.add(chatMessage)
                    database.set(dbMessage)
                    receivedMessage?.invoke(
                        message.id,
                        chatMessage.content?.data ?: ""
                    )
                }

                roomMessage["${chatMessage.channelValue?.data}"] = MessageView(
                    "#${chatMessage.channelValue?.data}",
                    bio = "${chatMessage.author?.displayName}: ${chatMessage.content?.data ?: ""}" ,
                    chatChannel = ChatChannelEnum.ROOM,
                    isNewNotification = true,
                    isPinned = true,
                    message = dbMessage
                )
            } else if (chatMessage.channelValue?.type == "dm") {
                if (chatMessage.channelValue?.data == null) return@forEach
                val firstId = chatMessage.channelValue?.data?.substringBefore(",")
                val secondId = chatMessage.channelValue?.data?.substringAfterLast(",")

                if (firstId.isNullOrEmpty() && secondId.isNullOrEmpty()) return@forEach

                val authorId = if (firstId == profile.me.id) {
                    secondId
                } else if (secondId == profile.me.id){
                    firstId
                } else {
                    ""
                }

                if (authorId.isNullOrEmpty()) return@forEach

                val dbMessage = database.get<Message>(authorId) ?: Message(
                    id = authorId,
                    addedChatMessages = arrayListOf(),
                    chatChannel = ChatChannelEnum.DM
                )

                var result = ""

                if (dbMessage.addedChatMessages?.find { it.id == chatMessage.id } == null) {
                    chatMessage.attachments?.let {
                        if (firstId == profile.me.id) return@let
                        it.forEach { attachment ->
                            attachment.content?.data?.let { data ->
                                AttachmentFileHelper.decodeToFile(
                                    attachment.id ?: "",
                                    AttachmentType.get(attachment.type),
                                    data,
                                    attachment.name ?: "UnknownFileType",
                                    context
                                )
                            }
                        }
                    }

                    result = chatMessage.content?.data ?: ""

                    dbMessage.addedChatMessages?.add(chatMessage)
                    database.set(dbMessage)
                    receivedMessage?.invoke(
                        chatMessage.author?.displayName ?: "",
                        result
                    )
                }

                val presence = _connectedUser.value[authorId]
                presence?.user?.displayName

                roomMessage[authorId] = MessageView(
                    "${presence?.user?.displayName}",
                    bio = "${chatMessage.author?.displayName}: ${result}" ,
                    chatChannel = ChatChannelEnum.DM,
                    isNewNotification = true,
                    isPinned = true,
                    message = dbMessage
                )
            }
        }
        return roomMessage
    }

    private var timer = Timer()

    init {
        timer.schedule(100, 10000) {
            val presence = Message(
                updatedPresences = arrayListOf(profile.presence),
                sourceUserId = profile.me.id,
                timestamp = Clock.System.now().epochSeconds.toDouble(),
                logicalClock = profile.me.logicalClock
            )
            val result = JsonUtils.encodeToString(presence)
            transport?.broadcast("$result\n")
        }
    }

    fun createMessageRoom(
        channelType: ChatChannelEnum,
        roomName: String = "",
        dmUserId: String = ""
    ) {
        if (channelType == ChatChannelEnum.ROOM) {
            val messageDb = database.get<Message>(roomName)
            if (messageDb != null) {
                return
            }
            val message = Message(
                id = roomName,
                addedChatMessages = arrayListOf(),
                chatChannel = channelType,
                isPin = roomName == GLOBAL_ROOM,
            )
            val messageView = MessageView(
                "#$roomName",
                bio = "Welcome to the chat room",
                chatChannel = channelType,
                isNewNotification = true,
                isPinned = true,
                message = message
            )
            database.set(message)
            _roomMessage.update {
                it + (message.id to messageView)
            }
        } else if (channelType == ChatChannelEnum.DM) {
            val messageDb = database.get<Message>(dmUserId)
            if (messageDb != null) {
                return
            }

            val message = Message(
                id = dmUserId,
                addedChatMessages = arrayListOf(),
                chatChannel = ChatChannelEnum.DM,
            )
            val chatPresence = _connectedUser.value[dmUserId]
            val messageView = MessageView(
                name = chatPresence?.user?.displayName ?: "",
                bio = "Welcome to the chat DM",
                chatChannel = channelType,
                isNewNotification = true,
                isPinned = true,
                message = message
            )
            database.set(message)
            _roomMessage.update {
                it + (message.id to messageView)
            }
        }
    }

    private fun registerNearbyUser() {
        CoroutineScope(Dispatchers.IO).launch {
            DiscoveryUserHelper.collectNearbyUser(1_000L).collect() { nearbyUser ->
                _nearbyUsers.update {
                    if (nearbyUser.isVisible) {
                        it.plus(nearbyUser.id to nearbyUser)
                    } else {
                        it.minus(nearbyUser.id)
                    }
                }
            }
        }
    }

    private fun initConnectedUser() {
        val presences = database.getAll<ChatPresence>()
        presences.forEach { presence ->
            _connectedUser.update {
                it + (presence.user.id to presence)
            }
        }
    }
}
