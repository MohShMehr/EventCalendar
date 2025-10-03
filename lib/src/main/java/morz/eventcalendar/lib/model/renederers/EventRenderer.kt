package morz.eventcalendar.lib.model.renederers

import androidx.compose.runtime.Composable
import morz.eventcalendar.lib.model.events.CalendarEvent

interface EventRenderer<T : CalendarEvent> {
    val type: kotlin.reflect.KClass<T>
    @Composable
    fun Render(event: T)
}