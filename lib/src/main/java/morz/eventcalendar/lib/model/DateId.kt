package morz.eventcalendar.lib.model

import ir.huri.jcal.JalaliCalendar

data class DateId(
    val year: Int,
    val month: Int,
    val day: Int
)

fun JalaliCalendar.toDateId(): DateId {
    return DateId(
        year = this.year,
        month = this.month,
        day = this.day
    )
}

fun DateId.toJalaliCalendar(): JalaliCalendar {
    return JalaliCalendar(
        this.year,
        this.month,
        this.day
    )
}

fun emptyDateId(): DateId {
    return DateId(
        year = 0,
        month = 0,
        day = 0
    )
}
