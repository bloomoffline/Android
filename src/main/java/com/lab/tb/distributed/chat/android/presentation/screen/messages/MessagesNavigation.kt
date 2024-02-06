package com.lab.tb.distributed.chat.android.presentation.screen.messages

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lab.tb.distributed.model.MessageView

const val messagesRoute = "messages_route"

fun NavController.navigateToMessages(navOptions: NavOptions? = null) {
    this.navigate(messagesRoute, navOptions)
}

fun NavGraphBuilder.messagesScreen(
    onMessageClick: (messageView: MessageView) -> Unit = {}
) {
    composable(route = messagesRoute) {
        MessagesRoute(onMessageClick = onMessageClick)
    }
}