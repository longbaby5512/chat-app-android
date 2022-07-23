package com.karry.chatapp.utils.extentions

import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SocketIOConnectionException: IOException()

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun Socket.connectAwait(): Socket {
    if (connected()) return this
    return suspendCancellableCoroutine { continuation ->
        once(Socket.EVENT_CONNECT) {
            if (continuation.isActive) {
                continuation.resume(this, null)
            }
        }
        once(Socket.EVENT_CONNECT_ERROR) {
            continuation.resumeWithException(SocketIOConnectionException())
        }
        connect()
        continuation.invokeOnCancellation { disconnect() }
    }
}

suspend fun Socket.emitAwait(event: String, vararg args: Any): Array<out Any> = suspendCoroutine { continuation ->
    emit(event, args) { result ->
        continuation.resume(result)
    }
}

suspend fun Emitter.onceAwait(event: String): Array<out Any> {
    return suspendCancellableCoroutine { cont ->
        val listener: Emitter.Listener = Emitter.Listener {
            if (cont.isActive)
                cont.resume(it)
        }
        cont.invokeOnCancellation { off(event, listener) }
        once(event, listener)
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("BlockingMethodInNonBlockingContext")
fun Emitter.onFlow(event: String): Flow<Array<out Any>> = callbackFlow {
    val listener = object : Emitter.Listener {
        override fun call(vararg args: Any) {
            trySendBlocking(args).onFailure {
                off(event, this)
            }
        }
    }

    on(event, listener)

    awaitClose { off(event, listener) }
}