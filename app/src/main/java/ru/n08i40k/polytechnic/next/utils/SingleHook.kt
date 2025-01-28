package ru.n08i40k.polytechnic.next.utils

@Suppress("unused")
class SingleHook<T> {
    private var _resolved: Boolean = false
    private val waiters: ArrayList<(T) -> Unit> = arrayListOf()

    val resolved get() = _resolved

    fun resolve(result: T) {
        if (_resolved)
            return

        _resolved = true
        waiters.forEach { it(result) }
        waiters.clear() // for fun :)
    }

    infix fun wait(waiter: (T) -> Unit): SingleHook<T> {
        waiters.add(waiter)

        return this
    }
}