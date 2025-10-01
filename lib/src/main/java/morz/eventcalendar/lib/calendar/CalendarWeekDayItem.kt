package morz.eventcalendar.lib.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import morz.eventcalendar.lib.model.DayItem
import morz.eventcalendar.lib.model.EventDot


@Composable
fun CalendarWeekDayItem(
    dayItem: DayItem,
    onDayClick: () -> Unit,
    selectedColor: Color = Color(0xFF9C7DFF),
    holidayColor: Color = Color(0xFFCF3434),
    eventContent: @Composable (ColumnScope.() -> Unit) = {},
) {
    Card(
        modifier = Modifier
            .width(52.dp)
            .height(82.dp)
            .testTag("day-card-${dayItem.dayName}"),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (dayItem.isSelected) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .then(
                    if (dayItem.isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            color = selectedColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else if (dayItem.isHoliday) {
                        Modifier.border(
                            width = 2.dp,
                            color = holidayColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .clickable { onDayClick() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Day name
                Text(
                    text = dayItem.dayName,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Light,
                    color = if (dayItem.isSelected) selectedColor else Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date with holiday styling
                Text(
                    text = dayItem.date,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        dayItem.isSelected -> selectedColor
                        dayItem.isHoliday -> holidayColor
                        else -> Color.Gray
                    },
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column {
                    eventContent()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CalendarWeekDayItemPreview() {
    CalendarWeekDayItem(
        dayItem = DayItem(
            dayName = "شنبه",
            date = "12",
            isSelected = false,
            events = listOf()
        ),
        onDayClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun CalendarWeekDayItemSelectedPreview() {
    CalendarWeekDayItem(
        dayItem = DayItem(
            dayName = "شنبه",
            date = "12",
            isSelected = true,
            events = listOf()
        ),
        onDayClick = {},
    )
}


@Preview(showBackground = true)
@Composable
private fun CalendarWeekDayItemWithEventPreview() {
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
    CalendarWeekDayItem(
        dayItem = dayItem,
        onDayClick = {},
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            dayItem.events.take(3).forEach { event ->
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color(event.color))
                )
            }
        }
    }
}