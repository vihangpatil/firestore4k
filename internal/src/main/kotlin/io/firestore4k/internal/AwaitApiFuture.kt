package io.firestore4k.internal

import com.google.api.core.ApiFuture
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ExecutionException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Awaits for completion of [ApiFuture] without blocking a thread.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this function
 * stops waiting for the completion stage and immediately resumes with [CancellationException][kotlinx.coroutines.CancellationException].
 *
 * Ref: https://github.com/Kotlin/kotlinx.coroutines/blob/master/integration/kotlinx-coroutines-jdk8/src/future/Future.kt
 */
suspend fun <T> ApiFuture<T>.await(): T {

    // fast path when CompletableFuture is already done (does not suspend)
    if (isDone) {
        try {
            @Suppress("UNCHECKED_CAST", "BlockingMethodInNonBlockingContext")
            return get() as T
        } catch (e: ExecutionException) {
            throw e.cause ?: e // unwrap original cause from ExecutionException
        }
    }
    // slow path -- suspend
    return suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
        cont.invokeOnCancellation {
            cancel(false)
        }
        // ApiFuture::addListener
        addListener({
            if (isDone) {
                try {
                    cont.resume(get())
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            } else if (isCancelled) {
                cont.resumeWithException(CancellationException())
            }
        }, Runnable::run)
    }
}
