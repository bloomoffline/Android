package com.lab.tb.distributed.model

private const val separator: Char = ':'
private const val userIdSeparator: Char = ','

//sealed class ChatChannel {
//    data class Room(val name: String) : ChatChannel() {
//        override fun toString(): String = "room$separator$name"
//    }
//
//    data class DM(val userIds: Set<String>) : ChatChannel() {
//        override fun toString(): String =
//            "dm$separator${userIds.joinToString(separator = userIdSeparator.toString()) { it }}"
//    }
//
//    companion object {
//        fun parse(str: String): ChatChannel {
//            val split = str.split(separator, limit = 2)
//            require(split.size == 2) { throw IllegalArgumentException("Could not parse $str") }
//
//            return createChannel(split[0], split[1])
//        }
//
//        private fun createChannel(type: String, data: String): ChatChannel {
//            return when (type) {
//                "room" -> Room(data)
//                "dm" -> {
//                    val userIds = data.split(userIdSeparator).map { raw ->
//                        val userId = raw
//                        requireNotNull(userId) { throw IllegalArgumentException("Invalid UUID: $data") }
//                        userId
//                    }.toSet()
//                    DM(userIds)
//                }
//
//                else -> throw IllegalArgumentException("Unknown channel type $type")
//            }
//        }
//    }
//}
