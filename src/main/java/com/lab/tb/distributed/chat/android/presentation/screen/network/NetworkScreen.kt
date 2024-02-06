package com.lab.tb.distributed.chat.android.presentation.screen.network

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lab.tb.distributed.ChatController
import com.lab.tb.distributed.chat.android.R
import com.lab.tb.distributed.chat.android.presentation.component.ConnectedUserItem
import com.lab.tb.distributed.chat.android.presentation.component.SectionHeader
import com.lab.tb.distributed.model.ChatChannelEnum
import com.lab.tb.distributed.model.ChatPresence

@Composable
fun NetworkRoute(modifier: Modifier = Modifier) {
    NetworkScreen(modifier = modifier.fillMaxSize())
}

@Composable
private fun NetworkScreen(
    modifier: Modifier = Modifier,
    onConnectedUserClick: (user: ChatPresence) -> Unit = {}
) {
    val chatController = remember { ChatController.getInstance() }
    val nearbyUserCount = chatController.nearbyUsers.collectAsState(initial = emptyMap()).value.size
    val reachableUser = chatController.connectedUser.collectAsState(initial = emptyMap()).value

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 25.dp),
    ) {
        item {
            NearbyUsersSection(
                reachableUserCount = reachableUser.size,
                nearbyUserCount = nearbyUserCount
            )
            SectionHeader(stringResource(id = R.string.connected_users))
        }

        itemsIndexed(reachableUser.toList()) { index, user ->
            ConnectedUserItem(
                user = user.second,
                isTheFirstItem = index == 0,
                isTheLastItem = index == reachableUser.size - 1,
                onUserLongClick = {
                    chatController.createMessageRoom(ChatChannelEnum.DM, dmUserId = it.user.id)
                },
                onCopyUserBloomIDClick = {
                    // todo:
                }
            )
        }
    }
}

@Composable
private fun NearbyUsersSection(
    modifier: Modifier = Modifier,
    reachableUserCount: Int,
    nearbyUserCount: Int
) {
    Column {
        SectionHeader(stringResource(id = R.string.network))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(R.drawable.ic_bloom_white),
                    contentDescription = "White Bloom Icon",
                )
                Text(
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    ),
                    text = stringResource(
                        R.string.reachable_users_nearby_users,
                        reachableUserCount,
                        nearbyUserCount
                    ),
                )
            }
        }
    }
}
