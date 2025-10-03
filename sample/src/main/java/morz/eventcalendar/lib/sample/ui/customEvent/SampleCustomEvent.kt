package morz.eventcalendar.lib.sample.ui.customEvent

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

@Composable
fun CustomEventView() {
    val colors = arrayOf(0xFF7D5260, 0xFF625b71, 0xFF6650a4)
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color(color))
            )
        }
    }
}

@Preview(
    locale = "FA",
    showBackground = true
)
@Composable
private fun DayItemEventPreview() {
    Column {
        CustomEventView()
    }
}