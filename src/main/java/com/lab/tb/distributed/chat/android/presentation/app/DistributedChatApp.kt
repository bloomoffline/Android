package com.lab.tb.distributed.chat.android.presentation.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.lab.tb.distributed.chat.android.navigation.DistributedChatAppNavHost
import com.lab.tb.distributed.chat.android.navigation.TopLevelDestination
import com.lab.tb.distributed.chat.android.presentation.component.DistributedChatAppNavigationBar
import com.lab.tb.distributed.chat.android.presentation.component.DistributedChatAppNavigationBarItem
import com.lab.tb.distributed.chat.android.presentation.component.DistributedChatAppTopBar
import com.lab.tb.distributed.chat.android.presentation.component.theme.DistributedChatAppBackground
import com.lab.tb.distributed.chat.android.presentation.screen.chat.chatRoute
import com.lab.tb.distributed.chat.android.presentation.screen.messages.messagesRoute

@Composable
fun DistributedChatApp(
    windowSizeClass: WindowSizeClass,
    appState: DistributedChatAppState = rememberTextToImageAppState(
        windowSizeClass = windowSizeClass,
    )
) {
    DistributedChatAppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                DistributedChatAppBottomBar(
                    destinations = appState.topLevelDestinations,
                    onNavigateToDestination = appState::navigateToTopLevelDestination,
                    currentDestination = appState.currentDestination,
                )
            }
        ) { paddingValues ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                DistributedChatAppTopBar()
                DistributedChatAppNavHost(appState = appState)
            }
        }
    }
}

@Composable
private fun DistributedChatAppBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    DistributedChatAppNavigationBar(
        modifier = modifier,
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            DistributedChatAppNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        painter = painterResource(destination.iconId),
                        "Google logo",
                    )
                },
                label = { Text(stringResource(destination.textId)) })
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        return if (it.route?.contains(
                chatRoute,
                true
            ) == true && destination == TopLevelDestination.MESSAGES
        ) {
            true
        } else {
            it.route?.contains(destination.name, true) ?: false
        }
    } ?: false