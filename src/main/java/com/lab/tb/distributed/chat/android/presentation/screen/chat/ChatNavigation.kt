package com.lab.tb.distributed.chat.android.presentation.screen.chat

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lab.tb.distributed.chat.android.presentation.screen.messages.messagesRoute
import java.net.URLDecoder

private val URL_CHARACTER_ENCODING = Charsets.UTF_8.name()

internal const val messageIdArg = "messageIdArg"
internal const val roomNameArg = "roomNameArg"

const val chatRoute = "chat_route"

class ChatArgs(val messageId: String, val roomName: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                URLDecoder.decode(checkNotNull(savedStateHandle[messageIdArg]), URL_CHARACTER_ENCODING),
                URLDecoder.decode(checkNotNull(savedStateHandle[roomNameArg]), URL_CHARACTER_ENCODING),
                )
}

fun NavController.navigateToChat(messageId: String, roomName: String) {
    this.navigate("$chatRoute/$messageId/$roomName") {
        popUpTo(messagesRoute)
        launchSingleTop = true
    }
}

fun NavGraphBuilder.chatScreen(onBackClick: () -> Unit = {}, onOpenDMChannelClick: (messageId: String, roomName: String) -> Unit = { _ , _  ->}) {
    composable(
        route = "$chatRoute/{$messageIdArg}/{$roomNameArg}",
        arguments = listOf(
            navArgument(messageIdArg) { type = NavType.StringType },
            navArgument(roomNameArg) { type = NavType.StringType },
            )
    ) {
        ChatRoute(onBackClick = onBackClick, onOpenDMChannelClick = onOpenDMChannelClick)
    }
}