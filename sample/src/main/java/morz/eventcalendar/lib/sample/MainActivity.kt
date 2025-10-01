package morz.eventcalendar.lib.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import morz.eventcalendar.lib.calendar.CalendarEventsView
import morz.eventcalendar.lib.calendar.DayItemEventView
import morz.eventcalendar.lib.calendar.EventContent
import morz.eventcalendar.lib.calendar.rememberCalendarEventsState
import morz.eventcalendar.lib.sample.ui.theme.EventCalendarTheme
import morz.eventcalendar.lib.model.DayItem
import morz.eventcalendar.lib.model.EventDot
import morz.eventcalendar.lib.util.JalaliCalendarHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventCalendarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        CalendarEvents()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    EventCalendarTheme {
        CalendarEvents()
    }
}

@Composable
private fun CalendarEvents() {
    val monthDays  = JalaliCalendarHelper.buildMonthGrid(1404, 7).toMutableList()

    val weekDays = mutableListOf<DayItem>()
    weekDays.clear()
    val jalaliWeekDays = listOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"
    )

    for (day in 1..7) {
        val dayName = jalaliWeekDays[(day - 1) % 7] // rotate weekday names
        weekDays.add(
            DayItem(
                dayName = dayName,
                date = day.toString().padStart(2, '0'),
                isSelected = false,
                events = if (day % 2 == 0) listOf(
                    EventDot(color = 0xFF5BCD95),
                    EventDot(color = 0xFF5BCD85),
                    EventDot(color = 0xFFFF3304)
                ) else emptyList()
            )
        )
    }
    val weekEvContents: List<EventContent> = weekDays.map { dayItem ->
        if (dayItem.events.isNotEmpty()) {
            {
                DayItemEventView(dayItem, 2)
            }
        } else {
            { /* empty */ }
        }
    }
    val monthEvContents: List<EventContent> = monthDays.flatten().map { dayItem ->
        if (dayItem.events.isNotEmpty()) {
            {
                DayItemEventView(dayItem, 2)
            }
        } else {
            { /* empty */ }
        }
    }
    val calendarState = rememberCalendarEventsState(
        initialTabIndex = 1, // monthly by default
        onWeeklySelectedDateChange = {
            Log.d("CalendarEvents", "onWeeklySelectedDateChange")
        },
        onMonthlySelectedDateChange = {
            Log.d("CalendarEvents", "onMonthlySelectedDateChange")
        },
        onCurrentWeekChange = {
            Log.d("CalendarEvents", "onCurrentWeekChange")
        },
        onCurrentMonthChange = {
            Log.d("CalendarEvents", "onCurrentMonthChange")
        },
    )

    CalendarEventsView(
        state = calendarState,
        modifier = Modifier.fillMaxWidth(),
        weekEventContents = weekEvContents,
        monthEventContents = monthEvContents
    )
}