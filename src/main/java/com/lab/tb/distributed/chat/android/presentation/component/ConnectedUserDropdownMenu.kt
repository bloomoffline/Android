package com.lab.tb.distributed.chat.android.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lab.tb.distributed.chat.android.R

@Composable
fun ConnectedUserDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSendPrivateMessageClick: () -> Unit,
    onCopyUserBloomIDClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.send_private_message)) },
            trailingIcon = {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_messages),
                    contentScale = ContentScale.Fit,
                    contentDescription = "Send Private Message"
                )
            },
            onClick = {
                onDismissRequest()
                onSendPrivateMessageClick()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.copy_user_bloom_id)) },
            trailingIcon = {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_copy),
                    contentScale = ContentScale.Fit,
                    contentDescription = "Copy User Bloom ID"
                )
            },
            onClick = {
                onDismissRequest()
                onCopyUserBloomIDClick()
            }
        )
    }
}