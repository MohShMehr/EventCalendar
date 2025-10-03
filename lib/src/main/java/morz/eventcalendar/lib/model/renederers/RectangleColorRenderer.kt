package morz.eventcalendar.lib.model.renederers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import morz.eventcalendar.lib.model.events.RectangleColorEvent

object RectangleColorRenderer : EventRenderer<RectangleColorEvent> {
    override val type = RectangleColorEvent::class

    @Composable
    override fun Render(event: RectangleColorEvent) {
        RectangleColor(event)
    }
}

@Composable
fun RectangleColor(
    event: RectangleColorEvent
) {
    Box(
        modifier = Modifier
            .size(4.dp)
            .clip(RectangleShape)
            .background(event.color)
    )
}

@Preview(showBackground = true)
@Composable
fun RectangleColorPreview() {
    RectangleColor(
        event = RectangleColorEvent(color = Color(0xFF5BCD85))
    )
}