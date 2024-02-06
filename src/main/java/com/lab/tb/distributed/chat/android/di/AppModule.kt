package com.lab.tb.distributed.chat.android.di

import com.lab.tb.distributed.chat.android.data.channel.ChannelRepository
import com.lab.tb.distributed.chat.android.data.channel.ChannelRepositoryImpl
import com.lab.tb.distributed.chat.android.data.message.MessageRepository
import com.lab.tb.distributed.chat.android.data.message.MessageRepositoryImpl
import com.lab.tb.distributed.chat.android.data.profile.ProfileRepository
import com.lab.tb.distributed.chat.android.data.profile.ProfileRepositoryImpl
import com.lab.tb.distributed.chat.android.manager.RecordManager
import com.lab.tb.distributed.chat.android.manager.RecordManagerImpl
import org.koin.dsl.module
import com.lab.tb.distributed.chat.android.presentation.screen.chat.ChatViewModel
import com.lab.tb.distributed.chat.android.presentation.screen.messages.MessagesViewModel
import com.lab.tb.distributed.chat.android.presentation.screen.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named

val appModule = module {
    single(named("IODispatcher")) { Dispatchers.IO }

    viewModel{ ChatViewModel(get(), get(), get()) }
    viewModel{ MessagesViewModel(get()) }
    viewModel{ ProfileViewModel(get()) }

    single<RecordManager> { RecordManagerImpl(androidContext()) }

    single<MessageRepository> { MessageRepositoryImpl(get(), get(named("IODispatcher"))) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(named("IODispatcher"))) }
    single<ChannelRepository> { ChannelRepositoryImpl(get(named("IODispatcher"))) }

}