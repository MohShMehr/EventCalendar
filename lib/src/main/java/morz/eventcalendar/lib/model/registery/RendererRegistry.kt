package morz.eventcalendar.lib.model.registery

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import morz.eventcalendar.lib.model.events.CalendarEvent
import morz.eventcalendar.lib.model.renederers.EventRenderer

class RendererRegistry(renderers: Set<EventRenderer<out CalendarEvent>>) {
    private val byType = renderers.associateBy { it.type }
    @Composable
    fun Render(event: CalendarEvent) {
        val renderer = byType[event::class] ?: return Text("")
        @Suppress("UNCHECKED_CAST")
        (renderer as EventRenderer<CalendarEvent>).Render(event)
    }
}