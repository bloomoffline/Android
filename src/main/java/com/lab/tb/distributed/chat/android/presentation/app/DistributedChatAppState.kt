package com.lab.tb.distributed.chat.android.presentation.app

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.lab.tb.distributed.chat.android.navigation.TopLevelDestination
import com.lab.tb.distributed.chat.android.navigation.TopLevelDestination.NETWORK
import com.lab.tb.distributed.chat.android.navigation.TopLevelDestination.MESSAGES
import com.lab.tb.distributed.chat.android.navigation.TopLevelDestination.PROFILE
import com.lab.tb.distributed.chat.android.presentation.screen.messages.navigateToMessages
import com.lab.tb.distributed.chat.android.presentation.screen.network.navigateToNetwork
import com.lab.tb.distributed.chat.android.presentation.screen.profile.navigateToProfile

@Composable
fun rememberTextToImageAppState(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController(),
): DistributedChatAppState {
    return remember(
        navController,
        windowSizeClass
    ) {
        DistributedChatAppState(navController)
    }
}

@Stable
class DistributedChatAppState(val navController: NavHostController) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()


    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when re-selecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            NETWORK -> navController.navigateToNetwork(topLevelNavOptions)
            MESSAGES -> navController.navigateToMessages(topLevelNavOptions)
            PROFILE -> navController.navigateToProfile(topLevelNavOptions)
        }
    }
}