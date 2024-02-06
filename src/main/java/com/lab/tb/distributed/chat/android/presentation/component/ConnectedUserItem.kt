package com.lab.tb.distributed.chat.android.presentation.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lab.tb.distributed.chat.android.R
import com.lab.tb.distributed.model.ChatPresence
import com.lab.tb.distributed.model.ChatStatus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConnectedUserItem(
    modifier: Modifier = Modifier,
    user: ChatPresence,
    isTheFirstItem: Boolean,
    isTheLastItem: Boolean,
    onUserClick: (user: ChatPresence) -> Unit = {},
    onUserLongClick: (user: ChatPresence) -> Unit = {},
    onSendPrivateMessageClick: () -> Unit = {},
    onCopyUserBloomIDClick: () -> Unit = {}
) {

    ///
    var expanded by remember { mutableStateOf(false) }

    ///

    val itemCornerShape = if (isTheFirstItem && isTheLastItem) {
        RoundedCornerShape(12)
    } else if (isTheFirstItem) {
        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    } else if (isTheLastItem) {
        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
    } else {
        RoundedCornerShape(0.dp)
    }
    val itemTopPadding = if (isTheFirstItem) {
        20.dp
    } else {
        10.dp
    }
    val itemBottomPadding = if (isTheLastItem) {
        20.dp
    } else {
        0.dp
    }
    val chatStatusImageResource = when (user.status) {
        ChatStatus.Online -> {
            R.drawable.ic_dot_online
        }

        ChatStatus.Busy -> {
            R.drawable.ic_dot_busy
        }

        ChatStatus.Emergency -> {
            R.drawable.ic_dot_emergency
        }
    }
    Card(
        shape = itemCornerShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onUserClick(user)
                },
                onLongClick = {
                    expanded = true
                    onUserLongClick(user)
                })
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(
                    horizontal = 20.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ConnectedUserDropdownMenu(
                expanded,
                onDismissRequest = { expanded = false },
                onSendPrivateMessageClick = onSendPrivateMessageClick,
                onCopyUserBloomIDClick = onCopyUserBloomIDClick
            )
            Image(
                painter = painterResource(chatStatusImageResource),
                contentDescription = "Status Image Resource",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            Text(
                user.user.displayName ?: "",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .padding(start = 12.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

        }
        if (!isTheLastItem) {
            Divider(
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .height(1.dp)
                    .fillMaxWidth()
            )
        }
    }
}