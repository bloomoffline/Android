package com.lab.tb.distributed.helper

import com.lab.tb.distributed.model.NearbyUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.onEach
import kotlin.native.concurrent.ThreadLocal


typealias DiscoveryUser = (NearbyUser) -> Unit
@ThreadLocal
object DiscoveryUserHelper {

    private var onDiscoveryUser: DiscoveryUser? = null
    private fun setEvent(onDiscoveryUser: DiscoveryUser?) {
        this.onDiscoveryUser = onDiscoveryUser
    }

    private fun clearEvent() {
        this.onDiscoveryUser = null
    }

    fun collectNearbyUser(period: Long): Flow<NearbyUser> {
        return callbackFlow {
            setEvent(
                onDiscoveryUser = {
                    trySend(it)
                }
            )

            this.awaitClose {
                clearEvent()
            }
            close()
        }
            .cancellable()
            .onEach { delay(period) }
    }

    fun notifyOnlineSensor(nearbyUser: NearbyUser) {
        onDiscoveryUser?.invoke(nearbyUser)
    }
}