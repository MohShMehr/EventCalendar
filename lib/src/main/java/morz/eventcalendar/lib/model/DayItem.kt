package morz.eventcalendar.lib.model


/**
 * Represents a day item in the calendar view
 * @param dayName Persian name of the day (e.g., "شنبه", "یکشنبه")
 * @param date Persian numeral representation of the date
 * @param isSelected Whether this day is currently selected
 * @param events List of event indicators for this day
 */
data class DayItem(
    val dayName: String,
    val date: String,
    val isSelected: Boolean = false,
    val events: List<EventDot> = emptyList(),
    val isHoliday: Boolean = false,
)


