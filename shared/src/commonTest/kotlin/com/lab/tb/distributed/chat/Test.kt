package com.lab.tb.distributed.chat

import com.lab.tb.distributed.base.JsonUtils
import com.lab.tb.distributed.model.ChatMessage
import com.lab.tb.distributed.model.Message
import kotlinx.datetime.Clock
import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertTrue

class CommonGreetingTest {

    @Test
    fun testExample() {
        assertTrue(Greeting().greet().contains("Hello"), "Check 'Hello' is mentioned")
    }

    @Test
    fun testConvert() {
        val addChat = "{\"addedChatMessages\":[{\"id\":\"5AF6DE61-1248-4C90-9E6C-A747E225AB42\",\"author\":{\"id\":\"4A0E5FCB-F795-4C0A-AB3F-2803CC081F81\",\"name\":\"\",\"logicalClock\":2},\"timestamp\":1703087614.1196001,\"content\":{\"type\":\"text\",\"data\":\"Xin chào \"}}],\"id\":\"8DFA700E-AC46-4B0D-96ED-D859B5D4C800\",\"timestamp\":1703087614.1196189,\"sourceUserId\":\"4A0E5FCB-F795-4C0A-AB3F-2803CC081F81\",\"logicalClock\":2}"
        println(addChat)
        val chatMessage = JsonUtils.decodeFromString<Message>(addChat)
        println(chatMessage)
    }

    @Test
    fun testChatMessage() {
        val text = "{\"id\":\"5AF6DE61-1248-4C90-9E6C-A747E225AB42\",\"author\":{\"id\":\"4A0E5FCB-F795-4C0A-AB3F-2803CC081F81\",\"name\":\"\",\"logicalClock\":2},\"timestamp\":1703087614.1196001,\"content\":{\"type\":\"text\",\"data\":\"Xin chào \"}}"
        val chatMessage = JsonUtils.decodeFromString<ChatMessage>(text)
        val hello = JsonUtils.encodeToString(listOf(chatMessage, chatMessage))
        val decodeHello = JsonUtils.decodeFromString<List<ChatMessage>>(hello)
        println(hello)
        println("------------------")
        println(decodeHello)
//        println("------------------")
//        println(chatMessage)
    }

    @Test
    fun testConvertPre() {
        val text = "{\"id\":\"DFF5DDFB-6231-4637-AC57-30A66B8D997D\",\"timestamp\":1703087595.8081751,\"updatedPresences\":[{\"status\":\"Online\",\"user\":{\"id\":\"4A0E5FCB-F795-4C0A-AB3F-2803CC081F81\",\"name\":\"\",\"logicalClock\":0},\"info\":\"\"}],\"sourceUserId\":\"4A0E5FCB-F795-4C0A-AB3F-2803CC081F81\",\"logicalClock\":0}"
        val chatMessage = JsonUtils.decodeFromString<Message>(text)
        println(chatMessage)

        val next = "{\"id\":\"226F7337-A513-4B76-8FAF-843B1D2A18DE\",\"timestamp\":1.704032136116E12,\"sourceUserId\":\"A3C4B499-5BD8-4101-A1DF-1140D438A4CB\",\"updatedPresences\":[{\"user\":{\"id\":\"A3C4B499-5BD8-4101-A1DF-1140D438A4CB\",\"logicalClock\":0},\"status\":\"Online\",\"info\":\"\",\"id\":\"A3C4B499-5BD8-4101-A1DF-1140D438A4CB\"}],\"logicalClock\":0}"
    }

    @Test
    fun aaa() {
        val hello = "{\"addedChatMessages\":[{\"id\":\"5E033A1E-574F-48B5-9D35-4173E7EBDB14\",\"author\":{\"id\":\"0EE8E648-5D87-41C7-9B2B-FC9A7ABD0D88\",\"name\":\"\",\"logicalClock\":48},\"timestamp\":1703863954.3952179,\"content\":{\"type\":\"text\",\"data\":\"Hello\"},\"channel\":{\"type\":\"dm\",\"data\":\"0EE8E648-5D87-41C7-9B2B-FC9A7ABD0D88\"}}],\"id\":\"99BD7237-72EC-4328-9E0A-BEE4BD8CC21D\",\"timestamp\":1703863954.395257,\"sourceUserId\":\"0EE8E648-5D87-41C7-9B2B-FC9A7ABD0D88\",\"logicalClock\":48}"
        val chatMessage = JsonUtils.decodeFromString<Message>(hello)
        println(chatMessage)
    }
}