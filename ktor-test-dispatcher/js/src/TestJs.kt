/*
* Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/

package io.ktor.test.dispatcher

import kotlinx.coroutines.*
import kotlin.coroutines.*
import kotlin.js.Promise

public actual class TestSuspendReturnType internal constructor(executor: ((Unit) -> Unit, (Throwable) -> Unit) -> Unit) : Promise<Unit>(executor) {
    internal fun then(
        onFulfilled: (Unit) -> TestSuspendReturnType,
        onRejected: (Throwable) -> TestSuspendReturnType
    ): TestSuspendReturnType = GlobalScope.async {
        val result = runCatching { this@TestSuspendReturnType.asDeferred().await() }
        if (result.isSuccess) {
            onFulfilled(Unit)
        } else {
            onRejected(result.exceptionOrNull()!!)
        }.await()
    }.asPromiseOfUnit()
}

public fun Deferred<Unit>.asPromiseOfUnit(): TestSuspendReturnType {
    val promise = TestSuspendReturnType { resolve, reject ->
        invokeOnCompletion {
            val e = getCompletionExceptionOrNull()
            if (e != null) {
                reject(e)
            } else {
                resolve(getCompleted())
            }
        }
    }
    promise.asDynamic().deferred = this
    return promise
}

/**
 * Test runner for js suspend tests.
 */
@OptIn(DelicateCoroutinesApi::class)
public actual fun testSuspend(
    context: CoroutineContext,
    timeoutMillis: Long,
    block: suspend CoroutineScope.() -> Unit
): TestSuspendReturnType = GlobalScope.async(block = {
    withTimeout(timeoutMillis, block)
}, context = context).asPromiseOfUnit()
