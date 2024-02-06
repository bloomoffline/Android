package com.lab.tb.distributed.chat.android.presentation.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lab.tb.distributed.chat.android.R
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    modifier: Modifier = Modifier,
    roomName: String,
    replyingContent: Pair<String, String>?,
    attachmentCount: Int,
    onSendClick: (String) -> Unit,
    onAddAttachmentClick: () -> Unit,
    onClearAttachmentClick: () -> Unit,
    onClearReplyingClick: () -> Unit,
    onStartRecording: () -> Unit,
    onFinishRecording: () -> Unit,
    resetScroll: () -> Unit = {},
) {
    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }
    var isRecordingMessage by remember { mutableStateOf(false) }
    val swipeOffset = remember { mutableStateOf(0f) }

    Surface(tonalElevation = 2.dp, contentColor = MaterialTheme.colorScheme.secondary) {
        Column(modifier = modifier) {
            replyingContent?.let { nonNullReplyingContent ->
                val displayName = nonNullReplyingContent.first
                val messageContent = nonNullReplyingContent.second
                Reply(displayName, messageContent, onClearReplyingClick)
            }
            if (attachmentCount > 0) {
                Attachment(attachmentCount, onClearAttachmentClick)
            }
            Row(
                modifier = modifier
                    .height(72.dp)
                    .wrapContentHeight()
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onAddAttachmentClick() },
                    painter = painterResource(id = R.drawable.ic_add_attachments),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
                UserInputText(
                    modifier = Modifier.weight(1f),
                    roomName = roomName,
                    textFieldValue = textState,
                    onTextChanged = { textState = it },
                    // Close extended selector if text field receives focus
                    onTextFieldFocused = { focused ->
                        if (focused) {
                            resetScroll()
                        }
                        textFieldFocusState = focused
                    },
                    focusState = textFieldFocusState
                )

                if (textState.text.isEmpty() && attachmentCount == 0) {
                    RecordButton(
                        recording = isRecordingMessage,
                        swipeOffset = { swipeOffset.value },
                        onSwipeOffsetChange = { offset -> swipeOffset.value = offset },
                        onStartRecording = {
                            val consumed = !isRecordingMessage
                            isRecordingMessage = true
                            onStartRecording()
                            consumed
                        },
                        onFinishRecording = {
                            // handle end of recording
                            isRecordingMessage = false
                            onFinishRecording()
                        },
                        onCancelRecording = {
                            isRecordingMessage = false
                        },
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                onSendClick(textState.text)
                                textState = TextFieldValue()
                                textFieldFocusState = false
                            },
                        painter = painterResource(id = R.drawable.ic_message_send),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
private fun Reply(displayName: String, messageContent: String, onClearReplyingClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .height(25.dp),
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = "Replying to $displayName: $messageContent",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )
        Icon(
            modifier = Modifier
                .size(20.dp)
                .clickable { onClearReplyingClick() },
            imageVector = Icons.Outlined.Cancel,
            contentDescription = "Remove replying",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun Attachment(attachmentCount: Int, onClearAttachmentClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .height(25.dp),
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = "$attachmentCount attachment",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )
        Icon(
            modifier = Modifier
                .size(20.dp)
                .clickable { onClearAttachmentClick() },
            imageVector = Icons.Outlined.Cancel,
            contentDescription = "Remove attachment",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun RecordButton(
    recording: Boolean,
    swipeOffset: () -> Float,
    onSwipeOffsetChange: (Float) -> Unit,
    onStartRecording: () -> Boolean,
    onFinishRecording: () -> Unit,
    onCancelRecording: () -> Unit,
) {
    val transition = updateTransition(targetState = recording, label = "record")
    val scale = transition.animateFloat(
        transitionSpec = { spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow) },
        label = "record-scale",
        targetValueByState = { rec -> if (rec) 2f else 1f }
    )
    val containerAlpha = transition.animateFloat(
        transitionSpec = { tween(1000) },
        label = "record-scale",
        targetValueByState = { rec -> if (rec) 1f else 0f }
    )

    Box {
        // Background during recording
        Box(
            Modifier
                .matchParentSize()
                .aspectRatio(1f)
                .graphicsLayer {
                    alpha = containerAlpha.value
                    scaleX = scale.value; scaleY = scale.value
                }
                .clip(CircleShape)
                .background(Color.Red)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_record),
            contentDescription = "Record message",
            modifier = Modifier
                .sizeIn(minWidth = 56.dp, minHeight = 6.dp)
                .padding(18.dp)
                .clickable { }
                .voiceRecordingGesture(
                    horizontalSwipeProgress = swipeOffset,
                    onSwipeProgressChanged = onSwipeOffsetChange,
                    onStartRecording = onStartRecording,
                    onFinishRecording = onFinishRecording,
                    onCancelRecording = onCancelRecording,
                )
        )
    }
}

private val ChatBubbleShape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp)

@ExperimentalFoundationApi
@Composable
private fun UserInputText(
    modifier: Modifier,
    roomName: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .background(MaterialTheme.colorScheme.secondary),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            shape = ChatBubbleShape,
            color = MaterialTheme.colorScheme.secondary,
        ) {
            Box(Modifier.fillMaxSize()) {
                UserInputTextField(
                    Modifier,
                    roomName,
                    textFieldValue,
                    onTextChanged,
                    onTextFieldFocused,
                    keyboardType,
                    focusState
                )
            }
        }
    }
}

@Composable
private fun BoxScope.UserInputTextField(
    modifier: Modifier = Modifier,
    roomName: String,
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onTextFieldFocused: (Boolean) -> Unit,
    keyboardType: KeyboardType,
    focusState: Boolean,
) {
    var lastFocusState by remember { mutableStateOf(false) }
    BasicTextField(
        value = textFieldValue,
        onValueChange = { onTextChanged(it) },
        modifier = modifier
            .padding(start = 20.dp)
            .align(Alignment.CenterStart)
            .onFocusChanged { state ->
                if (lastFocusState != state.isFocused) {
                    onTextFieldFocused(state.isFocused)
                }
                lastFocusState = state.isFocused
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Send
        ),
        maxLines = 1,
        cursorBrush = SolidColor(LocalContentColor.current),
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface)
    )

    val disableContentColor =
        MaterialTheme.colorScheme.onSurfaceVariant
    if (textFieldValue.text.isEmpty() && !focusState) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 15.dp),
            text = "Message ${roomName}...",
            style = MaterialTheme.typography.bodyLarge.copy(color = disableContentColor)
        )
    }
}

private fun Modifier.voiceRecordingGesture(
    horizontalSwipeProgress: () -> Float,
    onSwipeProgressChanged: (Float) -> Unit,
    onStartRecording: () -> Boolean = { false },
    onFinishRecording: () -> Unit = {},
    onCancelRecording: () -> Unit = {},
    swipeToCancelThreshold: Dp = 200.dp,
    verticalThreshold: Dp = 80.dp,
): Modifier = this
    .pointerInput(Unit) {
        var offsetY = 0f
        var dragging = false
        val swipeToCancelThresholdPx = swipeToCancelThreshold.toPx()
        val verticalThresholdPx = verticalThreshold.toPx()

        detectDragGesturesAfterLongPress(
            onDragStart = {
                onSwipeProgressChanged(0f)
                offsetY = 0f
                dragging = true
                onStartRecording()
            },
            onDragCancel = {
                onCancelRecording()
                dragging = false
            },
            onDragEnd = {
                if (dragging) {
                    onFinishRecording()
                }
                dragging = false
            },
            onDrag = { change, dragAmount ->
                if (dragging) {
                    onSwipeProgressChanged(horizontalSwipeProgress() + dragAmount.x)
                    offsetY += dragAmount.y
                    val offsetX = horizontalSwipeProgress()
                    if (
                        offsetX < 0 &&
                        abs(offsetX) >= swipeToCancelThresholdPx &&
                        abs(offsetY) <= verticalThresholdPx
                    ) {
                        onCancelRecording()
                        dragging = false
                    }
                }
            }
        )
    }