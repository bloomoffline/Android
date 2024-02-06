package com.lab.tb.distributed.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val Main: CoroutineDispatcher = Dispatchers.Main

actual val Background: CoroutineDispatcher = Dispatchers.Default

actual val Custom: CoroutineDispatcher = Dispatchers.Default
