package morz.eventcalendar.lib.model.events

import androidx.compose.ui.graphics.painter.Painter

data class PictureEvent(
    val painter: Painter
) : CalendarEvent