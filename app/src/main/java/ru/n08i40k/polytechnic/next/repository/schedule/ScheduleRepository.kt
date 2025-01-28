package ru.n08i40k.polytechnic.next.repository.schedule

import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.utils.MyResult

interface ScheduleRepository {
    suspend fun getGroup(): MyResult<GroupOrTeacher>

    suspend fun getTeacher(name: String): MyResult<GroupOrTeacher>
}