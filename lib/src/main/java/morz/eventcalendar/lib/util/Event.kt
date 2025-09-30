package morz.eventcalendar.lib.util

import android.graphics.Color
import java.time.LocalDateTime

/**
 * Represents a calendar event
 * @param id Unique identifier for the event
 * @param title Event title
 * @param description Event description
 * @param startDateTime Start date and time
 * @param endDateTime End date and time
 * @param color Color for display
 * @param type Type of event (task, reminder, custom)
 * @param isAllDay Whether this is an all-day event
 */
data class Event(
    val id: String,
    val title: String,
    val description: String? = null,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val color: Color,
    val type: EventType = EventType.CUSTOM,
    val isAllDay: Boolean = false,
    val relatedTaskId: String? = null
)

enum class EventType(val label: String) {
    TASK("وظیفه"),
    REMINDER("یادآوری"),
    CUSTOM("سفارشی")
}
