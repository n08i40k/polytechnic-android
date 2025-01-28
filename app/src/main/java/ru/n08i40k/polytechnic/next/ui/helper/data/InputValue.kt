package ru.n08i40k.polytechnic.next.ui.helper.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class InputValue<T>(
    var value: T,
    val errorCheck: (T) -> Boolean = { false },
    private var checkNow: Boolean = false,
    var isError: Boolean = false,
) {

    init {
        if (checkNow)
            isError = isError or errorCheck(value)

        // проверки после it.apply {}
        checkNow = true
    }
}

@Composable
fun <T> rememberInputValue(
    defaultValue: T,
    checkNow: Boolean = false,
    errorCheck: (T) -> Boolean = { false }
): MutableState<InputValue<T>> {
    return remember { mutableStateOf(InputValue<T>(defaultValue, errorCheck, checkNow)) }
}