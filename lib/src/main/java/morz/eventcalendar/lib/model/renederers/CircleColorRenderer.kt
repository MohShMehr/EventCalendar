package morz.eventcalendar.lib.model.renederers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import morz.eventcalendar.lib.model.events.CircleColorEvent

object CircleColorRenderer : EventRenderer<CircleColorEvent> {
    override val type = CircleColorEvent::class

    @Composable
    override fun Render(event: CircleColorEvent) {
        CircleColor(
            event
        )
    }
}

@Composable
fun CircleColor(
    event: CircleColorEvent
) {
    Box(
        modifier = Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(event.color)
    )
}

@Preview(showBackground = true)
@Composable
fun CircleColorPreview() {
    CircleColor(
        event = CircleColorEvent(color = Color(0xFF5BCD85))
    )
}