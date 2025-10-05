package morz.eventcalendar.lib.util

import ir.huri.jcal.JalaliCalendar
import morz.eventcalendar.lib.model.DayItem
import morz.eventcalendar.lib.model.emptyDateId
import morz.eventcalendar.lib.model.toDateId
import morz.eventcalendar.lib.model.toJalaliCalendar

/**
 * Utility class for JalaliCalendar operations
 */
object JalaliCalendarHelper {

    /**
     * Get all days of a month in Jalali calendar
     * @param year Jalali year
     * @param month Jalali month (1-12)
     * @return List of weeks, each week is a list of DayItem
     */
    fun buildMonthGrid(year: Int, month: Int): List<List<DayItem>> {

        val first = JalaliCalendar(year, month, 1)
        val monthLen = first.monthLength
        val startOffset = when (first.dayOfWeek) {
            7 -> 0; 1 -> 1; 2 -> 2; 3 -> 3; 4 -> 4; 5 -> 5; 6 -> 6; else -> 0
        }

        val weeks = mutableListOf<List<DayItem>>()
        var cur = mutableListOf<DayItem>()

        repeat(startOffset) { cur += DayItem("", emptyDateId(), false) } // leading blanks

        for (d in 1..monthLen) {
            val date = JalaliCalendar(year, month, d)
            val dayName = getDayName(date.dayOfWeek)
            cur += DayItem(dayName, date.toDateId(), false)
            if (cur.size == 7) {
                weeks += cur; cur = mutableListOf()
            }
        }

        while (cur.size in 1..6) cur += DayItem("", emptyDateId(), false) // trailing blanks
        if (cur.isNotEmpty()) weeks += cur

        return weeks
    }

    /**
     * Builds a list of 7 DayItem objects representing the week of the given date.
     * The week starts on Saturday and ends on Friday, adjusting across month/year boundaries.
     *
     * @param anchor JalaliCalendar date to anchor the week
     * @return List of DayItem for each day in the week (Saturday–Friday)
     */
    internal fun buildWeekDays(anchor: JalaliCalendar): List<DayItem> {

        val (saturday, _) = computeJalaliWeekBounds(anchor)
        val out = ArrayList<DayItem>(7)

        repeat(7) { i ->
            val baseLen = JalaliCalendar(saturday.year, saturday.month, 1).monthLength
            val day = saturday.day + i
            val date = if (day > baseLen) {
                val adj = day - baseLen
                if (saturday.month == 12) JalaliCalendar(saturday.year + 1, 1, adj)
                else JalaliCalendar(saturday.year, saturday.month + 1, adj)
            } else {
                JalaliCalendar(saturday.year, saturday.month, day)
            }

            out += DayItem(
                dayName = getDayName(date.dayOfWeek),
                dateId = date.toDateId(),
                isSelected = isSameDate(date, anchor),
                isHoliday = isFridaySimple(date)
            )
        }
        return out
    }

    /**
     * Get current Jalali date
     * @return JalaliCalendar for today
     */
    internal fun getCurrentDate(): JalaliCalendar {
        return JalaliCalendar()
    }

    /**
     * Get Persian day name
     * @param dayOfWeek Day of week (1-7, where 7 is Saturday)
     * @return Persian day name
     */
    internal fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            7 -> "شنبه"
            1 -> "یکشنبه"
            2 -> "دوشنبه"
            3 -> "سه‌شنبه"
            4 -> "چهارشنبه"
            5 -> "پنج‌شنبه"
            6 -> "جمعه"
            else -> ""
        }
    }

    /**
     * Convert Jalali date to readable Persian string
     * @param jalali JalaliCalendar instance
     * @return Formatted Persian date string
     */
    internal fun formatPersianDate(jalali: JalaliCalendar): String {
        val day = FormatHelper.toPersianNumber(jalali.day.toString())
        val month = jalali.monthString
        val year = FormatHelper.toPersianNumber(jalali.year.toString())
        return "$day $month $year"
    }

    /**
     * Check if a date is the same as another date
     * @param date1 First JalaliCalendar instance
     * @param date2 Second JalaliCalendar instance
     * @return true if both dates are the same
     */
    internal fun isSameDate(date1: JalaliCalendar, date2: JalaliCalendar): Boolean {
        return date1.year == date2.year && date1.month == date2.month && date1.day == date2.day
    }

    /**
     * Check if a date is Friday based on the actual day of week
     * This method only marks actual Fridays as holidays
     * @param jalali JalaliCalendar instance to check
     * @return true if the date is Friday
     */
    internal fun isFridaySimple(jalali: JalaliCalendar): Boolean {
        return jalali.dayOfWeek == 6
    }

    /**
     * Computes the start (Saturday) and end (Friday) of the Jalali week
     * for the given date, handling month and year boundaries.
     *
     * @param j JalaliCalendar date to calculate week bounds from
     * @return Pair of JalaliCalendar: first = Saturday (start), second = Friday (end)
     */
    internal fun computeJalaliWeekBounds(j: JalaliCalendar): Pair<JalaliCalendar, JalaliCalendar> {
        // Saturday as week start (7 in your lib)
        val offset = when (j.dayOfWeek) {
            7 -> 0; 1 -> 1; 2 -> 2; 3 -> 3; 4 -> 4; 5 -> 5; 6 -> 6; else -> 0
        }
        val saturday = if (j.day - offset <= 0) {
            if (j.month == 1) {
                val prevLen = JalaliCalendar(j.year - 1, 12, 1).monthLength
                JalaliCalendar(j.year - 1, 12, prevLen + (j.day - offset))
            } else {
                val prevLen = JalaliCalendar(j.year, j.month - 1, 1).monthLength
                JalaliCalendar(j.year, j.month - 1, prevLen + (j.day - offset))
            }
        } else {
            JalaliCalendar(j.year, j.month, j.day - offset)
        }
        val fridayDay = saturday.day + 6
        val friday = if (fridayDay > JalaliCalendar(saturday.year, saturday.month, 1).monthLength) {
            val adj = fridayDay - JalaliCalendar(saturday.year, saturday.month, 1).monthLength
            if (saturday.month == 12) JalaliCalendar(saturday.year + 1, 1, adj)
            else JalaliCalendar(saturday.year, saturday.month + 1, adj)
        } else {
            JalaliCalendar(saturday.year, saturday.month, fridayDay)
        }
        return saturday to friday
    }

    /**
     * Generates a formatted week title (e.g., "فروردین 29 - اردیبهشت 4") based on the first
     * and last valid days in the week, considering potential month/year boundaries.
     *
     * @param weekDays List of DayItem representing the week (Saturday to Friday)
     * @return Formatted week title, or an empty string if no valid days are found
     */
    internal fun weekTitleFrom(weekDays: List<DayItem>): String {
        if (weekDays.isEmpty()) return ""
        return "${formatPersianDate(weekDays.first().dateId.toJalaliCalendar())} " +
                "- ${formatPersianDate(weekDays.last().dateId.toJalaliCalendar())}"
    }

}

