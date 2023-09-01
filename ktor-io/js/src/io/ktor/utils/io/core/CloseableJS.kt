package io.ktor.utils.io.core

public actual interface AutoCloseable {
    actual public fun close()
}

public actual interface Closeable : AutoCloseable {
    public actual override fun close()
}

@PublishedApi
internal actual fun Throwable.addSuppressedInternal(other: Throwable) {
}
