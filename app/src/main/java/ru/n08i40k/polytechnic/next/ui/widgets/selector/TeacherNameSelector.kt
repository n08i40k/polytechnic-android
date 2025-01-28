package ru.n08i40k.polytechnic.next.ui.widgets.selector

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleGetTeacherNames

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TeacherNameSelector(
    value: String? = "Фамилия И.О.",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    readOnly: Boolean = false,
) {
    ListSelector(
        value,
        onValueChange,
        modifier,
        readOnly,
        stringResource(R.string.teacher_name),
        { Icon(Icons.Filled.Person, stringResource(R.string.cd_user_icon)) },
        isError
    ) {
        val context = LocalContext.current
        var entries by remember { mutableStateOf<ArrayList<String>?>(null) }

        LaunchedEffect(context) {
            ScheduleGetTeacherNames(
                listener = {
                    entries = it.names
                    it(entries!!.getOrNull(0))
                },
                errorListener = {
                    entries = arrayListOf()
                    it(null)
                }
            ).send(context)
        }

        entries
    }
}