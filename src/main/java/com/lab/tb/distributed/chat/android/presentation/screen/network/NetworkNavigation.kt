package com.lab.tb.distributed.chat.android.presentation.screen.network

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val networkRoute = "network_route"

fun NavController.navigateToNetwork(navOptions: NavOptions? = null) {
    this.navigate(networkRoute, navOptions)
}

fun NavGraphBuilder.networkScreen() {
    composable(route = networkRoute) {
        NetworkRoute()
    }
}