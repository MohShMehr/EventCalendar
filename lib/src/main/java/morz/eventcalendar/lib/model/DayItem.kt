package morz.eventcalendar.lib.model

import morz.eventcalendar.lib.util.FormatHelper


/**
 * Represents a day item in the calendar view.
 *
 * @param dayName Persian name of the day (e.g., "شنبه", "یکشنبه").
 * @param dateId Jalali date.
 * @param isSelected Whether this day is currently selected.
 * @param isHoliday Whether this day is a holiday.
 * @param dayNumber Persian numeral representation of the date.
 */
data class DayItem(
    val dayName: String,
    val dateId: DateId,
    val isSelected: Boolean = false,
    val isHoliday: Boolean = false,
){
    val dayNumber: String
        get() = FormatHelper.toPersianNumber(dateId.day.toString())
            .takeIf { dateId.day != 0  && dateId.month != 0 && dateId.year != 0 } ?: ""
}


