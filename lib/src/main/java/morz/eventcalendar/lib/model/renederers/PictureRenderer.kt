package morz.eventcalendar.lib.model.renederers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import morz.eventcalendar.lib.model.events.PictureEvent

object PictureRenderer : EventRenderer<PictureEvent> {
    override val type = PictureEvent::class

    @Composable
    override fun Render(event: PictureEvent) {
        PictureEventContent(
            event
        )
    }
}

@Composable
private fun PictureEventContent(
    event: PictureEvent
) {
    Image(
        modifier = Modifier.size(30.dp),
        painter = event.painter,
        contentDescription = "Event Image",
    )
}

@Preview(showBackground = true)
@Composable
fun PictureEventPreview() {
    PictureEventContent(
        event = PictureEvent(painter = painterResource(android.R.drawable.ic_delete))
    )
}