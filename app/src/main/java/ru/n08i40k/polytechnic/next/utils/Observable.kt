package ru.n08i40k.polytechnic.next.utils

import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference
import java.util.UUID

@Suppress("unused")
class Observable<T> {
    class Subscriber<T>(
        private val _observable: WeakReference<Observable<T>>,
        val uuid: UUID,

        private val _next: (T) -> Unit,
        private val _close: (() -> Unit)? = null,

        val context: Context
    ) {
        class LifecycleObserver<T>(val subscriber: Subscriber<T>) :
            DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)

                when (val observable = subscriber.observable) {
                    null -> subscriber.destroy()
                    else -> observable.unsubscribe(subscriber.uuid)
                }
            }
        }

        val lifecycleObserver = LifecycleObserver(this)

        val observable get() = _observable.get()

        var invalid = false

        init {
            if (context is LifecycleOwner)
                context.lifecycle.addObserver(lifecycleObserver)

            Log.d("Subscriber", "New subscriber $uuid!")
        }

        fun destroy() {
            Log.d("Subscriber", "Destroying subscriber...")

            if (context is LifecycleOwner)
                context.lifecycle.removeObserver(lifecycleObserver)

            invalid = true
        }

        fun next(value: T) {
            if (invalid)
                throw IllegalStateException("Subscriber is invalid.")

            if (observable == null)
                throw NullPointerException("Observable is null.")

            Log.d("Subscriber", "Invoking $uuid!")
            _next(value)
        }

        fun close() {
            if (invalid)
                throw IllegalStateException("Subscriber is invalid.")

            if (observable == null)
                throw NullPointerException("Observable is null.")

            Log.d("Subscriber", "Closing $uuid!")
            _close?.invoke()

            destroy()
        }
    }

    private val subscribers: HashMap<UUID, Subscriber<T>> = hashMapOf()

    fun next(result: T) {
        subscribers.values.forEach { it.next(result) }
    }

    fun close() {
        subscribers.values.forEach { it.close() }
        subscribers.clear()
    }

    fun subscribe(context: Context, next: (T) -> Unit, close: (() -> Unit)? = null): Subscriber<T> {
        val uuid = UUID.randomUUID()
        val subscriber = Subscriber<T>(WeakReference(this), uuid, next, close, context)

        subscribers.put(uuid, subscriber)

        return subscriber
    }

    fun unsubscribe(uuid: UUID) {
        subscribers.remove(uuid)?.destroy()
    }
}