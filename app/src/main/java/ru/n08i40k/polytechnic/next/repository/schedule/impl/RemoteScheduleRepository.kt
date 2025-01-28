package ru.n08i40k.polytechnic.next.repository.schedule.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.app.AppContainer
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleGet
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleGetTeacher
import ru.n08i40k.polytechnic.next.network.tryFuture
import ru.n08i40k.polytechnic.next.repository.schedule.ScheduleRepository
import ru.n08i40k.polytechnic.next.utils.MyResult

class RemoteScheduleRepository(private val container: AppContainer) : ScheduleRepository {
    private val context get() = container.context

    override suspend fun getGroup(): MyResult<GroupOrTeacher> =
        withContext(Dispatchers.IO) {
            val response = tryFuture(context) {
                ScheduleGet(
                    container,
                    it,
                    it
                )
            }

            when (response) {
                is MyResult.Success -> MyResult.Success(response.data.group)
                is MyResult.Failure -> response
            }
        }

    override suspend fun getTeacher(name: String): MyResult<GroupOrTeacher> =
        withContext(Dispatchers.IO) {
            val response = tryFuture(context) {
                ScheduleGetTeacher(
                    container,
                    name,
                    it,
                    it
                )
            }

            when (response) {
                is MyResult.Failure -> response
                is MyResult.Success -> MyResult.Success(response.data.teacher)
            }
        }
}