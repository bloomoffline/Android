package com.lab.tb.distributed.chat.android.presentation.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lab.tb.distributed.ChatController
import com.lab.tb.distributed.chat.android.R
import com.lab.tb.distributed.chat.android.model.BloomSettingType
import com.lab.tb.distributed.chat.android.presentation.component.DistributedChatAppSwitch
import com.lab.tb.distributed.chat.android.presentation.component.SectionHeader
import com.lab.tb.distributed.model.ChatStatus
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val profileUiState by viewModel.profileUiState.collectAsStateWithLifecycle()

    ProfileScreen(
        modifier = modifier.fillMaxSize(),
        profileUiState = profileUiState,
        onBloomSettingChange = viewModel::updateBloomSettings,
        onUserNameChanged = viewModel::updateName,
        onBioChanged = viewModel::updateUserBio,
        onActivityStatusClick = viewModel::updateActivityStatus
    )
}

@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileUiState: ProfileUiState,
    onActivityStatusClick: (chatStatus: ChatStatus) -> Unit = {},
    onBloomSettingChange: (bloomSettingType: BloomSettingType, value: Int) -> Unit = { _, _ -> },
    onUserNameChanged: (name: String) -> Unit = {},
    onBioChanged: (bio: String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(horizontal = 25.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (profileUiState is ProfileUiState.Success) {
            ProfileInformationSection(
                userInformation = profileUiState.userInformation,
                onUserNameChanged = onUserNameChanged,
                onBioChanged = onBioChanged
            )
            ActivityStatusSection(
                selectedChatStatus = profileUiState.chatStatus,
                onActivityStatusClick = onActivityStatusClick
            )
            BloomSettingsSection(
                bloomSettingsMap = profileUiState.bloomSettingsMap,
                onBloomSettingChange = onBloomSettingChange,
            )
        }
    }
}

@Composable
private fun ProfileInformationSection(
    modifier: Modifier = Modifier,
    userInformation: Pair<String, String>,
    onUserNameChanged: (name: String) -> Unit,
    onBioChanged: (bio: String) -> Unit
) {
    val username = userInformation.first
    val bio = userInformation.second
    Column {
        SectionHeader(stringResource(id = R.string.profile))
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(65.dp),
                painter = painterResource(R.drawable.ic_user_white),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                BasicTextField(
                    value = username,
                    onValueChange = onUserNameChanged,
                    maxLines = 1,
                    cursorBrush = SolidColor(LocalContentColor.current),
                    textStyle =  TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))
                BasicTextField(
                    value = bio,
                    onValueChange = onBioChanged,
                    maxLines = 1,
                    cursorBrush = SolidColor(LocalContentColor.current),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp
                    ),
                )
            }
        }
    }
}

@Composable
private fun ActivityStatusSection(
    selectedChatStatus: ChatStatus,
    onActivityStatusClick: (chatStatus: ChatStatus) -> Unit
) {
    Column {
        SectionHeader(stringResource(id = R.string.activity_status))
        for (status in ChatStatus.entries) {
            ActivityStatusItem(
                chatStatus = status,
                isSelected = status == selectedChatStatus,
                onActivityStatusClick = onActivityStatusClick
            )
        }
    }
}

@Composable
private fun BloomSettingsSection(
    modifier: Modifier = Modifier,
    bloomSettingsMap: Map<BloomSettingType, Int>,
    onBloomSettingChange: (bloomSettingType: BloomSettingType, value: Int) -> Unit = { _, _ -> },
) {
    Column {
        SectionHeader(stringResource(id = R.string.bloom_settings))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = modifier.fillMaxWidth()
        ) {
            Column {
                Box(
                    modifier = Modifier.padding(start = 16.dp),
                ) {
                    Column {
                        for (settingItem in BloomSettingType.entries) {
                            val settingValue = bloomSettingsMap[settingItem]
                            BloomSettingItem(
                                title = stringResource(settingItem.titleTextId),
                                explanation = settingItem.explanationTextId?.let { stringResource(it) },
                                trailingContent = {
                                    if (settingItem == BloomSettingType.BLOOM_MONITORING_INTERVAL) {
                                        BasicTextField(
                                            value = "$settingValue",
                                            onValueChange = { value ->
                                                if (value.isDigitsOnly() && value.isNotEmpty() && value.length < 5) {
                                                    onBloomSettingChange(settingItem, value.toInt())
                                                }
                                            },
                                            modifier = Modifier
                                                .width(60.dp),
                                            maxLines = 1,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    cursorBrush = SolidColor(LocalContentColor.current),
                                            textStyle =  TextStyle(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center,
                                                ),
                                        )
                                    } else {
                                        DistributedChatAppSwitch(
                                            modifier = Modifier.padding(end = 8.dp),
                                            isChecked = settingValue == 1,
                                            onCheckedChange = { isChecked ->
                                                onBloomSettingChange(settingItem, if (isChecked) 1 else 0)
                                            }
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BloomSettingItem(
    modifier: Modifier = Modifier,
    title: String,
    explanation: String? = null,
    trailingContent: @Composable (() -> Unit)
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(8f)
            ) {
                Text(
                    title,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp
                    ),
                )
                explanation?.let { nonNullExplanation ->
                    Text(
                        nonNullExplanation,
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        ),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                trailingContent()
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .height(0.8.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun ActivityStatusItem(
    modifier: Modifier = Modifier,
    chatStatus: ChatStatus,
    isSelected: Boolean,
    onActivityStatusClick: (chatStatus: ChatStatus) -> Unit
) {
    val chatStatusImageResource = when (chatStatus) {
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
                .clickable { onActivityStatusClick(chatStatus) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(chatStatusImageResource),
                contentDescription = "Chat Status",
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = chatStatus.value,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
            )
            if (isSelected) {
                Image(
                    painterResource(R.drawable.ic_checkmark_white),
                    contentDescription = "",
                )
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .height(0.8.dp)
                .fillMaxWidth()
        )
    }
}




