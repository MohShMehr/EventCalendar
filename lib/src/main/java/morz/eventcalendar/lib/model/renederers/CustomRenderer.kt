package morz.eventcalendar.lib.model.renederers

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import morz.eventcalendar.lib.model.events.CustomEvent

object CustomRenderer : EventRenderer<CustomEvent> {
    override val type = CustomEvent::class

    @Composable
    override fun Render(event: CustomEvent) {
        CustomContent(
            event
        )
    }
}

@Composable
private fun CustomContent(
    event: CustomEvent
) {
    Column {
        event.content()
    }
}

@Preview(showBackground = true)
@Composable
fun CustomContentPreview() {
    CustomContent(
        event = CustomEvent {
            Text(
                text = "Custom event"
            )
        }
    )
}