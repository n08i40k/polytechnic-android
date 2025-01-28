package ru.n08i40k.polytechnic.next.repository.schedule.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonTime
import ru.n08i40k.polytechnic.next.model.LessonType
import ru.n08i40k.polytechnic.next.model.SubGroup
import ru.n08i40k.polytechnic.next.repository.schedule.ScheduleRepository
import ru.n08i40k.polytechnic.next.utils.MyResult
import ru.n08i40k.polytechnic.next.utils.now

private fun genLocalDateTime(hour: Int, minute: Int): Instant {
    return LocalDateTime(2024, 1, 1, hour, minute, 0, 0).toInstant(TimeZone.currentSystemDefault())
}

private fun genBreak(start: Instant, end: Instant): Lesson {
    return Lesson(
        type = LessonType.BREAK,
        defaultRange = null,
        name = null,
        time = LessonTime(
            start,
            end
        ),
        subGroups = listOf(),
        group = null
    )
}

class MockScheduleRepository : ScheduleRepository {
    companion object {
        val exampleGroup = GroupOrTeacher(
            name = "ИС-214/23", days = arrayListOf(
                Day(
                    name = "Понедельник",
                    date = LocalDateTime.now().toInstant(TimeZone.currentSystemDefault()),
                    lessons = listOf(
                        Lesson(
                            type = LessonType.ADDITIONAL,
                            defaultRange = null,
                            name = "Линейка",
                            time = LessonTime(
                                genLocalDateTime(8, 30),
                                genLocalDateTime(8, 40),
                            ),
                            subGroups = listOf(),
                            group = null
                        ),
                        genBreak(
                            genLocalDateTime(8, 40),
                            genLocalDateTime(8, 45),
                        ),
                        Lesson(
                            type = LessonType.ADDITIONAL,
                            defaultRange = null,
                            name = "Разговор о важном",
                            time = LessonTime(
                                genLocalDateTime(8, 45),
                                genLocalDateTime(9, 15),
                            ),
                            subGroups = listOf(),
                            group = null
                        ),
                        genBreak(
                            genLocalDateTime(9, 15),
                            genLocalDateTime(9, 25),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultRange = listOf(1, 1),
                            name = "МДК.05.01 Проектирование и дизайн информационных систем",
                            time = LessonTime(
                                genLocalDateTime(9, 25),
                                genLocalDateTime(10, 45),
                            ),
                            subGroups = listOf(
                                SubGroup(
                                    teacher = "Ивашова А.Н.",
                                    number = 1,
                                    cabinet = "43"
                                )
                            ),
                            group = null
                        ),
                        genBreak(
                            genLocalDateTime(10, 45),
                            genLocalDateTime(10, 55),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultRange = listOf(2, 2),
                            name = "Основы проектирования баз данных",
                            time = LessonTime(
                                genLocalDateTime(10, 55),
                                genLocalDateTime(12, 15),
                            ),
                            subGroups = listOf(
                                SubGroup(
                                    teacher = "Чинарева Е.А.",
                                    number = 1,
                                    cabinet = "21"
                                ),
                                SubGroup(
                                    teacher = "Ивашова А.Н.",
                                    number = 2,
                                    cabinet = "44"
                                ),
                            ),
                            group = null
                        ),
                        genBreak(
                            genLocalDateTime(12, 15),
                            genLocalDateTime(12, 35),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultRange = listOf(3, 3),
                            name = "Операционные системы и среды",
                            time = LessonTime(
                                genLocalDateTime(12, 35),
                                genLocalDateTime(13, 55),
                            ),
                            subGroups = listOf(
                                SubGroup(
                                    teacher = "Сергачева А.О.",
                                    number = 1,
                                    cabinet = "42"
                                ),
                                SubGroup(
                                    teacher = "Воронцева Н.В.",
                                    number = 2,
                                    cabinet = "41"
                                ),
                            ),
                            group = null
                        ),
                    ),
                    street = "Железнодорожная 13",
                )
            )
        )

        val exampleTeacher = GroupOrTeacher(
            name = "Хомченко Н.Е.", days = arrayListOf(
                Day(
                    name = "Понедельник",
                    date = LocalDateTime.now().toInstant(TimeZone.currentSystemDefault()),
                    lessons = listOf(
                        Lesson(
                            type = LessonType.ADDITIONAL,
                            defaultRange = null,
                            name = "Линейка",
                            time = LessonTime(
                                genLocalDateTime(8, 30),
                                genLocalDateTime(8, 40),
                            ),
                            subGroups = listOf(),
                            group = "ИС-214/23"
                        ),
                        genBreak(
                            genLocalDateTime(8, 40),
                            genLocalDateTime(8, 45),
                        ),
                        Lesson(
                            type = LessonType.ADDITIONAL,
                            defaultRange = null,
                            name = "Разговор о важном",
                            time = LessonTime(
                                genLocalDateTime(8, 45),
                                genLocalDateTime(9, 15),
                            ),
                            subGroups = listOf(),
                            group = "ИС-214/23"
                        ),
                        genBreak(
                            genLocalDateTime(9, 15),
                            genLocalDateTime(9, 25),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultRange = listOf(1, 1),
                            name = "МДК.05.01 Проектирование и дизайн информационных систем",
                            time = LessonTime(
                                genLocalDateTime(9, 25),
                                genLocalDateTime(10, 45),
                            ),
                            subGroups = listOf(
                                SubGroup(
                                    teacher = "Ивашова А.Н.",
                                    number = 1,
                                    cabinet = "43"
                                )
                            ),
                            group = "ИС-214/23"
                        ),
                        genBreak(
                            genLocalDateTime(10, 45),
                            genLocalDateTime(10, 55),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultRange = listOf(2, 2),
                            name = "Основы проектирования баз данных",
                            time = LessonTime(
                                genLocalDateTime(10, 55),
                                genLocalDateTime(12, 15),
                            ),
                            subGroups = listOf(
                                SubGroup(
                                    teacher = "Чинарева Е.А.",
                                    number = 1,
                                    cabinet = "21"
                                ),
                                SubGroup(
                                    teacher = "Ивашова А.Н.",
                                    number = 2,
                                    cabinet = "44"
                                ),
                            ),
                            group = "ИС-214/23"
                        ),
                        genBreak(
                            genLocalDateTime(12, 15),
                            genLocalDateTime(12, 35),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultRange = listOf(3, 3),
                            name = "Операционные системы и среды",
                            time = LessonTime(
                                genLocalDateTime(12, 35),
                                genLocalDateTime(13, 55),
                            ),
                            subGroups = listOf(
                                SubGroup(
                                    teacher = "Сергачева А.О.",
                                    number = 1,
                                    cabinet = "42"
                                ),
                                SubGroup(
                                    teacher = "Воронцева Н.В.",
                                    number = 2,
                                    cabinet = "41"
                                ),
                            ),
                            group = "ИС-214/23"
                        ),
                    ),
                    null
                )
            )
        )
    }

    private var updateCounter: Int = 0

    override suspend fun getGroup(): MyResult<GroupOrTeacher> {
        return withContext(Dispatchers.IO) {
            delay(1500)
            if (updateCounter++ % 3 == 0) MyResult.Failure()
            else MyResult.Success(exampleGroup)
        }
    }

    override suspend fun getTeacher(name: String): MyResult<GroupOrTeacher> {
        return withContext(Dispatchers.IO) {
            delay(1500)
            if (updateCounter++ % 3 == 0) MyResult.Failure()
            else MyResult.Success(exampleTeacher)
        }
    }
}