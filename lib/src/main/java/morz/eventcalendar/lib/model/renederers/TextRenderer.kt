package morz.eventcalendar.lib.model.renederers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import morz.eventcalendar.lib.model.events.TextEvent

object TextRenderer : EventRenderer<TextEvent> {
    override val type = TextEvent::class

    @Composable
    override fun Render(event: TextEvent) {
        TextEventContent(
            event
        )
    }
}

@Composable
private fun TextEventContent(
    event: TextEvent
) {
    Text(
        fontSize = 6.sp,
        text = event.title
    )
}

@Preview(showBackground = true)
@Composable
fun TextEventContentPreview() {
    TextEventContent(
        event = TextEvent(title = "A")
    )
}