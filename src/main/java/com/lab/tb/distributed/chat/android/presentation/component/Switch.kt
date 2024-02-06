package com.lab.tb.distributed.chat.android.presentation.component

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * Distributed Chat App Switch.
 *
 * @param modifier Modifier to be applied to the switch.
 * @param isChecked Current value of the slider.
 * @param onCheckedChange Will be called when the user uncheck / check the switch.
 */
@Composable
fun DistributedChatAppSwitch(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        modifier = modifier,
        checked = isChecked,
        onCheckedChange = onCheckedChange
    )
}

@Preview
@Composable
fun DistributedChatAppSwitchUncheckedPreview() {
    DistributedChatAppSwitch(
        isChecked = false,
        onCheckedChange = {},
    )
}

@Preview
@Composable
fun DistributedChatAppSwitchCheckedPreview() {
    DistributedChatAppSwitch(
        isChecked = true,
        onCheckedChange = {},
    )
}