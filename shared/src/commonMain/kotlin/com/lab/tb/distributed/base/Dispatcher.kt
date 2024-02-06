

package com.lab.tb.distributed.base

import kotlinx.coroutines.CoroutineDispatcher

internal expect val Main: CoroutineDispatcher

internal expect val Background: CoroutineDispatcher

expect val Custom: CoroutineDispatcher
