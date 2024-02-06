package com.lab.tb.distributed.chat.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import com.lab.tb.distributed.chat.android.presentation.app.DistributedChatAppState
import com.lab.tb.distributed.chat.android.presentation.screen.chat.chatScreen
import com.lab.tb.distributed.chat.android.presentation.screen.chat.navigateToChat
import com.lab.tb.distributed.chat.android.presentation.screen.messages.messagesScreen
import com.lab.tb.distributed.chat.android.presentation.screen.network.networkRoute
import com.lab.tb.distributed.chat.android.presentation.screen.network.networkScreen
import com.lab.tb.distributed.chat.android.presentation.screen.profile.profileScreen

@Composable
fun DistributedChatAppNavHost(
    appState: DistributedChatAppState,
    modifier: Modifier = Modifier,
    startDestination: String = networkRoute,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        messagesScreen(onMessageClick = { message ->
            val messageId = message.message?.id
            messageId?.let { nonNullMessageId ->
                navController.navigateToChat(nonNullMessageId, message.name)
            }
        })
        networkScreen()
        profileScreen()
        chatScreen(
            onBackClick = { navController.popBackStack() },
            onOpenDMChannelClick =  { id, name ->
                navController.navigateToChat(id, name)
            })
    }
}