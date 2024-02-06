package com.lab.tb.distributed.chat.android.presentation.screen.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.lab.tb.distributed.chat.android.R
import com.lab.tb.distributed.chat.android.model.DraftAttachment
import com.lab.tb.distributed.chat.android.presentation.component.UserInput
import com.lab.tb.distributed.chat.android.presentation.component.createTempPictureUri
import com.lab.tb.distributed.model.AttachmentType
import com.linc.audiowaveform.AudioWaveform
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun ChatRoute(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onOpenDMChannelClick: (messageId: String, roomName: String) -> Unit = { _ , _  ->}
) {
    val chatUiState by viewModel.chatUiState.collectAsStateWithLifecycle()

    ChatScreen(
        modifier = modifier.fillMaxSize(),
        chatUiState = chatUiState,
        onBackClick = onBackClick,
        onSendClick = viewModel::sendMessage,
        onImagePickerResult = viewModel::addDraftAttachment,
        onContactPickerResult = viewModel::addDraftAttachment,
        onClearAttachmentClick = viewModel::clearDraftAttachmentList,
        onDeleteLocallyClick = viewModel::deleteMessage,
        onReplyClick = viewModel::saveReplyingToMessageId,
        onMarkAsUnreadClick = viewModel::markAsUnread,
        onOpenDMChannelClick = onOpenDMChannelClick,
        onClearReplyingClick = viewModel::saveReplyingToMessageId,
        onStartRecording = viewModel::startRecording,
        onFinishRecording = viewModel::stopRecording,
        onStartAudio = viewModel::startAudio,
        onStopAudio = viewModel::stopAudio,
    )
}

@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreen(
    modifier: Modifier = Modifier,
    chatUiState: ChatUiState,
    onBackClick: () -> Unit,
    onSendClick: (message: String, context: Context, draftAttachmentList: List<DraftAttachment>) -> Unit,
    onImagePickerResult: (DraftAttachment.Image) -> Unit,
    onContactPickerResult: (DraftAttachment.Contact) -> Unit,
    onClearAttachmentClick: () -> Unit,
    onDeleteLocallyClick: (messageId: String) -> Unit,
    onReplyClick: (messageId: String) -> Unit,
    onMarkAsUnreadClick: (messageId: String) -> Unit,
    onOpenDMChannelClick: (messageId: String, roomName: String) -> Unit = { _ , _ -> },
    onClearReplyingClick: () -> Unit,
    onStartRecording: () -> Unit,
    onFinishRecording: () -> Unit,
    onStartAudio: (attachmentId: String, completion: () -> Unit) -> Unit,
    onStopAudio: (attachmentId: String) -> Unit,
    ) {
    val context = LocalContext.current
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    var showBottomSheet by remember { mutableStateOf(false) }
    var cameraPhotoUri by remember { mutableStateOf(value = Uri.EMPTY) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                onImagePickerResult(DraftAttachment.Image(cameraPhotoUri))
            }
        }
    )

    val customContactPicker = object : ActivityResultContract<Void?, Uri?>() {
        override fun createIntent(context: Context, input: Void?): Intent {
            return Intent().apply {
                data = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                action = Intent.ACTION_PICK
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
        }
    }

    val contactLauncher = rememberLauncherForActivityResult(
        contract = customContactPicker,
        onResult = { uri ->
            uri?.let { contactUri ->
                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Email.ADDRESS,
                    ContactsContract.Contacts.DISPLAY_NAME
                )
                val cursor = context.contentResolver.query(
                    contactUri, projection,
                    null, null, null
                )
                cursor?.let { cursor1 ->
                    if (cursor1.moveToFirst()) {
                        val phoneIndex =
                            cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val emailIndex =
                            cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                        val nameIndex =
                            cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

                        val phoneNumber = cursor1.getString(phoneIndex)
                        val emailAddress = cursor1.getString(emailIndex)
                        val displayName = cursor1.getString(nameIndex)
                        onContactPickerResult(DraftAttachment.Contact(displayName, phoneNumber, emailAddress))
                    }
                    cursor1.close()
                }
            }
        }
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { nonNullUri ->
                onImagePickerResult(DraftAttachment.Image(nonNullUri))
            }
        })

    val contactAttachmentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()) {
    }

    Scaffold(
        topBar = {
            if (chatUiState is ChatUiState.Success) {
                ChatTopBar(username = chatUiState.roomName, onBackClick = onBackClick)
            }
        },
        // Exclude ime and navigation bar padding so this can be added by the UserInput composable
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        ChatBody(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            chatUiState = chatUiState,
            onSendClick = onSendClick,
            onAddAttachmentClick = { showBottomSheet = true },
            onClearAttachmentClick = onClearAttachmentClick,
            onDeleteLocallyClick = onDeleteLocallyClick,
            onReplyClick = onReplyClick,
            onMarkAsUnreadClick = onMarkAsUnreadClick,
            onOpenDMChannelClick = onOpenDMChannelClick,
            onClearReplyingClick = onClearReplyingClick,
            onStartRecording = onStartRecording,
            onFinishRecording = onFinishRecording,
            onStartAudio = onStartAudio,
            onStopAudio = onStopAudio,
            onContactAttachmentClick = { attachmentId, fileName ->
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, Uri.parse("${context.filesDir}/contact/$attachmentId/$fileName"))
                    type = "*/*"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(sendIntent)
            }
        )
    }

    if (showBottomSheet) {
        AttachmentBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            onTypeSelected = { type ->
                when (type) {
                    AttachmentSheetSection.CAMERA -> {
                        cameraPhotoUri = context.createTempPictureUri()
                        cameraLauncher.launch(cameraPhotoUri)
                    }

                    AttachmentSheetSection.PHOTO_LIBRARY -> {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }

                    AttachmentSheetSection.CONTACT -> {
                        contactLauncher.launch()
                    }

                    else -> {}
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    username: String,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                text = username,
            )
        },
        navigationIcon = {
            Row(
                modifier = Modifier.clickable { onBackClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Normal,
                        fontSize = 30.sp
                    ),
                    text = "<",
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp
                    ),
                    text = "Back",
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    )
}

@Composable
fun ChatBody(
    modifier: Modifier = Modifier,
    chatUiState: ChatUiState,
    onSendClick: (message: String, context: Context, draftAttachmentList: List<DraftAttachment>) -> Unit,
    onAddAttachmentClick: () -> Unit,
    onClearAttachmentClick: () -> Unit,
    onDeleteLocallyClick: (messageId: String) -> Unit,
    onReplyClick: (messageId: String) -> Unit,
    onMarkAsUnreadClick: (messageId: String) -> Unit,
    onOpenDMChannelClick: (messageId: String, roomName: String) -> Unit = { _ , _ -> },
    onClearReplyingClick: () -> Unit,
    onStartRecording: () -> Unit,
    onFinishRecording: () -> Unit,
    onStartAudio: (attachmentId: String, completion: () -> Unit) -> Unit,
    onStopAudio: (attachmentId: String) -> Unit,
    onContactAttachmentClick: (attachmentId: String, fileName: String) -> Unit,
    ) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var replyingState: Pair<String, String>? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    Column(modifier) {
        if (chatUiState is ChatUiState.Success) {
            MessageList(
                chatMessageBubbleList = chatUiState.chatMessageBubbleList,
                modifier = Modifier.weight(1f),
                scrollState = scrollState,
                onDeleteLocallyClick = onDeleteLocallyClick,
                onReplyClick = { messageId, name, content ->
                    replyingState = Pair(name, content)
                    onReplyClick(messageId)
                },
                onMarkAsUnreadClick = onMarkAsUnreadClick,
                onOpenDMChannelClick = onOpenDMChannelClick,
                onStartAudio = onStartAudio,
                onStopAudio = onStopAudio,
                onContactAttachmentClick = onContactAttachmentClick,
            )

            UserInput(
                modifier = Modifier.imePadding(),
                roomName = chatUiState.roomName,
                replyingContent = replyingState,
                attachmentCount = chatUiState.draftAttachmentList.size,
                onSendClick = { message ->
                    onSendClick(message, context, chatUiState.draftAttachmentList)
                },
                onAddAttachmentClick = onAddAttachmentClick,
                onClearAttachmentClick = onClearAttachmentClick,
                onClearReplyingClick = {
                    replyingState = null
                    onClearReplyingClick()
               },
                onStartRecording = onStartRecording,
                onFinishRecording = onFinishRecording,
                resetScroll = { scope.launch { scrollState.scrollToItem(chatUiState.chatMessageBubbleList.size) } },
            )
        }
    }
}

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    chatMessageBubbleList: List<MessageBubbleUiState>,
    scrollState: LazyListState,
    onDeleteLocallyClick: (messageId: String) -> Unit,
    onReplyClick: (messageId: String, displayName: String, messageContent: String) -> Unit,
    onMarkAsUnreadClick: (messageId: String) -> Unit,
    onOpenDMChannelClick: (messageId: String, roomName: String) -> Unit = { _ , _ -> },
    onStartAudio: (attachmentId: String, completion: () -> Unit) -> Unit,
    onStopAudio: (attachmentId: String) -> Unit,
    onContactAttachmentClick: (attachmentId: String, fileName: String) -> Unit,
    ) {
    if (chatMessageBubbleList.isNotEmpty()) {
        LaunchedEffect(chatMessageBubbleList) { scrollState.animateScrollToItem(chatMessageBubbleList.size - 1) }
        Box(modifier.background(MaterialTheme.colorScheme.surface)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = scrollState
            ) {
                items(chatMessageBubbleList) { message ->
                    MessageItem(
                        messageBubbleUiState = message,
                        onDeleteLocallyClick = onDeleteLocallyClick,
                        onReplyClick = onReplyClick,
                        onMarkAsUnreadClick = onMarkAsUnreadClick,
                        onOpenDMChannelClick = onOpenDMChannelClick,
                        onStartAudio = onStartAudio,
                        onStopAudio = onStopAudio,
                        onContactAttachmentClick = onContactAttachmentClick
                    )
                }
            }
        }
    } else {
        Box(
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface))
    }
}

@Composable
fun MessageItem(
    messageBubbleUiState: MessageBubbleUiState,
    onDeleteLocallyClick: (messageId: String) -> Unit,
    onReplyClick: (messageId: String, displayName: String, messageContent: String) -> Unit,
    onMarkAsUnreadClick: (messageId: String) -> Unit,
    onOpenDMChannelClick: (messageId: String, roomName: String) -> Unit = { _ , _ -> },
    onStartAudio: (attachmentId: String, completion: () -> Unit) -> Unit,
    onStopAudio: (attachmentId: String) -> Unit,
    onContactAttachmentClick: (attachmentId: String, fileName: String) -> Unit,
    ) {
    Column(
        modifier = Modifier
            .padding(horizontal = 25.dp)
    ) {
        MessageBubble(
            messageBubbleUiState = messageBubbleUiState,
            onDeleteLocallyClick = onDeleteLocallyClick,
            onReplyClick = onReplyClick,
            onMarkAsUnreadClick = onMarkAsUnreadClick,
            onOpenDMChannelClick = onOpenDMChannelClick,
            onStartAudio = onStartAudio,
            onStopAudio = onStopAudio,
            onContactAttachmentClick = onContactAttachmentClick
        )
        Spacer(modifier = Modifier.height(15.dp))
    }
}

private val ChatBubbleShape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    messageBubbleUiState: MessageBubbleUiState,
    onDeleteLocallyClick: (messageId: String) -> Unit,
    onReplyClick: (messageId: String, displayName: String, messageContent: String) -> Unit,
    onMarkAsUnreadClick: (messageId: String) -> Unit,
    onOpenDMChannelClick: (messageId: String, roomName: String) -> Unit = { _ , _ -> },
    onStartAudio: (attachmentId: String, completion: () -> Unit) -> Unit,
    onStopAudio: (attachmentId: String) -> Unit,
    onContactAttachmentClick: (attachmentId: String, fileName: String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var attachmentId by remember { mutableStateOf("") }

    var isAudioPlaying by remember { mutableStateOf(false) }

    val authorColor = if (messageBubbleUiState.isMe) {
        Color(0x80FFFFFF)

    } else {
        MaterialTheme.colorScheme.onSecondary
    }

    val backgroundBubbleColor = if (messageBubbleUiState.isMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    val horizontalAlignment = if (messageBubbleUiState.isMe) {
        Alignment.End
    } else {
        Alignment.Start
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Surface(
            modifier = Modifier.combinedClickable(onClick = {}, onLongClick = { expanded = true }),
            color = backgroundBubbleColor,
            shape = ChatBubbleShape
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 8.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 15.dp
                )
            ) {
                Text(
                    style = TextStyle(
                        color = authorColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    text = messageBubbleUiState.authorName,
                )

                if (messageBubbleUiState.content.isNotEmpty()) {
                    Text(
                        text = messageFormatter(text = messageBubbleUiState.content, primary = messageBubbleUiState.isMe),
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    )
                }

                messageBubbleUiState.messageContentList.forEach { content ->
                attachmentId = content.attachmentId
                when (content) {
                    is MessageAttachmentUiState.Image -> {
                        Spacer(modifier = Modifier.height(4.dp))
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.size(250.dp),
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = rememberAsyncImagePainter(content.url),
                                contentScale = ContentScale.Crop,
                                contentDescription = "Attached image"
                            )
                        }
                    }

                    is MessageAttachmentUiState.VoiceNote -> {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.width(150.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        if (isAudioPlaying) {
                                            onStopAudio(content.attachmentId)
                                        } else {
                                            onStartAudio(content.attachmentId) {
                                                isAudioPlaying = false
                                            }
                                        }
                                        isAudioPlaying = !isAudioPlaying
                                    },
                                imageVector = if (isAudioPlaying)  Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Audio button",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            if (content.amplitudeList.isNotEmpty() && !content.amplitudeList.all { it == 0 }) {
                                AudioWaveform(
                                    modifier = Modifier.weight(1f),
                                    amplitudes = content.amplitudeList,
                                    waveformBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
                                    onProgressChange = {}
                                )
                            }
                        }
                    }

                    is MessageAttachmentUiState.Contact -> {
                        Row(
                            modifier = Modifier.clickable {
                               onContactAttachmentClick(content.attachmentId, content.fileName)
                            },
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = Icons.Default.Person,
                                contentDescription = "Person icon",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = content.fileName,
                                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                            )
                        }
                    }

                    else -> {}
                }
                }
            }
        }
    }

    MessageBubbleDropdownMenu(
        messageBubbleUiState = messageBubbleUiState, expanded = expanded,
        attachmentId = attachmentId,
        onDismissRequest = { expanded = false },
        onDeleteLocallyClick = onDeleteLocallyClick,
        onReplyClick = onReplyClick,
        onMarkAsUnreadClick = onMarkAsUnreadClick,
        onOpenDMChannelClick = onOpenDMChannelClick,
    )
}

@Composable
private fun MessageBubbleDropdownMenu(messageBubbleUiState: MessageBubbleUiState,
                                      attachmentId: String,
                                      expanded: Boolean, onDismissRequest: () -> Unit,
                                      onDeleteLocallyClick: (messageId: String) -> Unit,
                                      onReplyClick: (messageId: String, displayName: String, messageContent: String) -> Unit,
                                      onMarkAsUnreadClick: (messageId: String) -> Unit,
                                      onOpenDMChannelClick: (messageId: String, roomName: String) -> Unit = { _ , _ -> }) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.delete_locally)) },
            trailingIcon = {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_delete),
                    contentScale = ContentScale.Fit,
                    contentDescription = "Delete locally"
                )
            },
            onClick = {
                onDeleteLocallyClick(messageBubbleUiState.id)
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.reply)) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Filled.Reply,
                    contentDescription = "Reply",
                )
            },
            onClick = {
                onReplyClick(messageBubbleUiState.id, messageBubbleUiState.authorName, messageBubbleUiState.content)
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.mark_as_unread)) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Filled.Circle,
                    contentDescription = "Mark as Unread",
                )
            },
            onClick = {
                onMarkAsUnreadClick(messageBubbleUiState.id)
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.share_text)) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Filled.IosShare,
                    contentDescription = "Share Text",
                )
            },
            onClick = {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, messageBubbleUiState.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
                onDismissRequest()
            }
        )

        if (attachmentId.isNotEmpty()) {
            val attachment = messageBubbleUiState.messageContentList.firstOrNull { it.attachmentId == attachmentId }
            if (attachment != null) {
                var content = ""
                when (attachment) {
                    is MessageAttachmentUiState.Contact -> {
                        content = attachment.fileName
                    }
                    is MessageAttachmentUiState.VoiceNote -> {
                        content = "voiceNote.m4a"
                    }

                    else -> {}
                }

                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.share_file, content)) },
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Filled.IosShare,
                            contentDescription = "Share File",
                        )
                    },
                    onClick = {
                        val filePath = File(context.filesDir, "contact/$attachmentId/$content")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", filePath)
                        ShareCompat.IntentBuilder(context)
                            .setStream(uri)
                            .setType("*/*")
                            .startChooser()
                        onDismissRequest()
                    }
                )
            }

        }

        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.copy_text)) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Outlined.FileCopy,
                    contentDescription = "Copy Text",
                )
            },
            onClick = {
                clipboardManager.setText(AnnotatedString(messageBubbleUiState.content))
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.copy_message_id)) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Outlined.FileCopy,
                    contentDescription = "Copy Message ID",
                )
            },
            onClick = {
                clipboardManager.setText(AnnotatedString((messageBubbleUiState.id)))
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.copy_message_url)) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Filled.FileCopy,
                    contentDescription = "Copy Message URL",
                )
            },
            onClick = {
                clipboardManager.setText(AnnotatedString(("bloomoffline:///message/${messageBubbleUiState.id}")))
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.copy_author_id)) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Outlined.FileCopy,
                    contentDescription = "Copy Author ID",
                )
            },
            onClick = {
                clipboardManager.setText(AnnotatedString((messageBubbleUiState.authorId)))
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.copy_author_name)) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Outlined.FileCopy,
                    contentDescription = "Copy Author Name",
                )
            },
            onClick = {
                clipboardManager.setText(AnnotatedString((messageBubbleUiState.authorName)))
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.open_dm_channel)) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Outlined.AlternateEmail,
                    contentDescription = "Open DM channel",
                )
            },
            onClick = {
                onOpenDMChannelClick(messageBubbleUiState.id, messageBubbleUiState.authorName)
                onDismissRequest()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentBottomSheet(
    onDismissRequest: () -> Unit,
    onTypeSelected: (type: AttachmentSheetSection) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(),
        dragHandle = {}
    ) {
        LazyColumn(modifier = Modifier.padding(horizontal = 25.dp)) {
            items(AttachmentSheetSection.entries) { type ->
                AttachmentItem(type = type, onTypeSelected = { selectedType ->
                    onTypeSelected(selectedType)
                    onDismissRequest()
                })
            }
        }
    }
}

@Composable
fun AttachmentItem(
    modifier: Modifier = Modifier,
    type: AttachmentSheetSection,
    onTypeSelected: (type: AttachmentSheetSection) -> Unit
) {
    val itemCornerShape = when (type) {
        AttachmentSheetSection.ADD_ATTACHMENT -> RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        )

        AttachmentSheetSection.FILE -> RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
        AttachmentSheetSection.CANCEL -> RoundedCornerShape(12.dp)
        else -> RoundedCornerShape(0.dp)
    }

    Column {
        if (type == AttachmentSheetSection.CANCEL) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
            )
        }

        Card(
            shape = itemCornerShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = modifier
                .fillMaxWidth()
                .clickable(type != AttachmentSheetSection.ADD_ATTACHMENT) { onTypeSelected(type) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                when (type) {
                    AttachmentSheetSection.ADD_ATTACHMENT -> {
                        Text(
                            text = type.content,
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            ),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Large size content will take times to reach users.\n" +
                                    "For now, we recommend to share only text, voice notes, \n" +
                                    "contact, and low size images.",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontWeight = FontWeight.Normal,
                                fontSize = 10.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    else -> {
                        Text(
                            text = type.content,
                            style = TextStyle(
                                color = if (type != AttachmentSheetSection.CANCEL) MaterialTheme.colorScheme.primary else Color.Red,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        )
                    }

                }
            }

            if (type != AttachmentSheetSection.FILE && type != AttachmentSheetSection.CANCEL) {
                Divider(
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .height(0.8.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

enum class AttachmentSheetSection(val content: String) {
    ADD_ATTACHMENT("Add Attachment"),
    PHOTO_LIBRARY("Photo Library"),
    CAMERA("Camera"),
    CONTACT("Contact"),
    FILE("File"),
    CANCEL("Cancel")
}