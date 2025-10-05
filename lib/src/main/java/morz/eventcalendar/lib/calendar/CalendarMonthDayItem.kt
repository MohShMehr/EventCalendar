package morz.eventcalendar.lib.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import morz.eventcalendar.lib.model.DateId
import morz.eventcalendar.lib.model.DayItem


@Composable
fun CalendarMonthDayItem(
    dayItem: DayItem,
    onDayClick: () -> Unit,
    selectedColor: Color,
    holidayColor: Color,
    dayColor: Color,
    eventContent: @Composable (ColumnScope.() -> Unit) = {},
) {
    if (dayItem.dayNumber.isNotEmpty()) {
        Card(
            modifier = Modifier
                .size(36.dp) // Keep original compact size
                .testTag("monthly-day-${dayItem.dayNumber}"),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (dayItem.isSelected) 6.dp else 2.dp
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
                                shape = RoundedCornerShape(8.dp)
                            )
                        } else if (dayItem.isHoliday) {
                            Modifier.border(
                                width = 2.dp,
                                color = holidayColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onDayClick() }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Date with holiday styling
                    Text(
                        text = dayItem.dayNumber,
                        fontSize = 12.sp, // Keep original font size
                        fontWeight = FontWeight.Medium,
                        color = when {
                            dayItem.isSelected -> selectedColor
                            dayItem.isHoliday -> holidayColor
                            else -> dayColor
                        },
                        textAlign = TextAlign.Center
                    )

                    // Event dots
                    Column {
                        eventContent()
                    }
                }
            }
        }
    } else {
        // No need for Spacer since we're using LazyVerticalGrid with proper empty cells
        // The empty cells are handled by the grid layout itself
    }
}


@Preview(
    locale = "FA",
    showBackground = true)
@Composable
private fun CalendarMonthDayItemPreview() {
    CalendarMonthDayItem(
        dayItem = DayItem(
            dayName = "شنبه",
            dateId = DateId(1402, 1, 12),
            isSelected = false,
        ),
        selectedColor = Color(0xFF9C7DFF),
        holidayColor = Color(0xFFCF3434),
        dayColor = Color.Black,
        onDayClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun CalendarMonthDayItemSelectedPreview() {
    CalendarMonthDayItem(
        dayItem = DayItem(
            dayName = "شنبه",
            dateId = DateId(1402, 1, 12),
            isSelected = true,
        ),
        selectedColor = Color(0xFF9C7DFF),
        holidayColor = Color(0xFFCF3434),
        dayColor = Color.Black,
        onDayClick = {},
    )
}


@Preview(showBackground = true)
@Composable
private fun CalendarMonthDayItemWithEventPreview() {

    val dayItem = DayItem(
        dayName = "شنبه",
        dateId = DateId(1402, 1, 12),
        isSelected = false,
    )
    val colors = arrayOf(0xFF7D5260, 0xFF625b71, 0xFF6650a4)
    CalendarMonthDayItem(
        dayItem = dayItem,
        onDayClick = {},
        selectedColor = Color(0xFF9C7DFF),
        holidayColor = Color(0xFFCF3434),
        dayColor = Color.Black
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
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
}