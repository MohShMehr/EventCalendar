package morz.eventcalendar.lib.model.events

import androidx.compose.runtime.Composable

data class CustomEvent(
    val content: @Composable (() -> Unit)
): CalendarEvent