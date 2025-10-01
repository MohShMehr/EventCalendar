package morz.eventcalendar.lib.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import morz.eventcalendar.lib.model.DayItem
import morz.eventcalendar.lib.model.EventDot

@Composable
fun DayItemEventView(dayItem: DayItem, numberOfEventsToShow: Int){
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        dayItem.events.take(numberOfEventsToShow).forEach { event ->
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color(event.color))
            )
        }
    }
}

@Preview(
    locale = "FA",
    showBackground = true)
@Composable
private fun DayItemEventPreview() {
    val dayItem = DayItem(
        dayName = "شنبه",
        date = "12",
        isSelected = false,
        events = listOf(
            EventDot(color = 0xFF5BCD85),
            EventDot(color = 0xFF5BCD85),
            EventDot(color = 0xFFFF0004)
        )
    )
    Column {

        DayItemEventView(dayItem, 3)
    }
}