package morz.eventcalendar.lib.model

/**
 * Represents an event indicator dot in the calendar
 * @param color Color of the event dot
 * @param eventType Type of event for different styling
 * @param priority Priority level for task events
 * @param count Number of events on this day (for multiple events)
 */
data class EventDot(
    val color: Long,
    val eventType: EventType = EventType.CUSTOM,
    val count: Int = 1
)


