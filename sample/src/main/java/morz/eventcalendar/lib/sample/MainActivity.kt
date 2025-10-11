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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import morz.eventcalendar.lib.calendar.CalendarEventsView
import morz.eventcalendar.lib.calendar.rememberCalendarEventsState
import morz.eventcalendar.lib.model.DateId
import morz.eventcalendar.lib.model.events.CalendarEvent
import morz.eventcalendar.lib.model.events.CircleColorEvent
import morz.eventcalendar.lib.model.events.CustomEvent
import morz.eventcalendar.lib.model.events.PictureEvent
import morz.eventcalendar.lib.model.events.RectangleColorEvent
import morz.eventcalendar.lib.sample.ui.customEvent.CustomEventView
import morz.eventcalendar.lib.sample.ui.theme.EventCalendarTheme

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
    //you can get all calendar event clicks here
    val calendarState = rememberCalendarEventsState(
        initialTabIndex = 1,
        onWeeklySelectedDateChange = {
            Log.d("CalendarEvents", "get selected date in weekly calendar")
        },
        onMonthlySelectedDateChange = {
            Log.d("CalendarEvents", "get selected date in monthly calendar")
        },
        onCurrentWeekChange = {
            Log.d("CalendarEvents", "get current date in next or previous week")
        },
        onCurrentMonthChange = {
            Log.d("CalendarEvents", "get current date in next or previous month")
        },
    )

    val eventImage = painterResource(android.R.drawable.ic_delete)



    //you can also update weekly calendar events by your data here
    LaunchedEffect(calendarState.weekState.weeklyCurrentDate) {


        val date = calendarState.weekState.weeklyCurrentDate
        //in weekly calendar for displaying events you can use every child of CalendarEvent or make your custom event view
        val weekEventsMap: Map<DateId, CalendarEvent> = mapOf(
            DateId(date.year, date.month, date.day + 1) to CircleColorEvent(
                color = Color(0xFF5BCD85)
            ),
            DateId(date.year, date.month, date.day) to RectangleColorEvent(
                color = Color(0xFF9C27B0)
            ),
            DateId(date.year, date.month, date.day - 1) to PictureEvent(
                painter = eventImage
            ),
            DateId(date.year, date.month, date.day - 2) to CustomEvent(
                content = { CustomEventView() }
            )
        )

        calendarState.weekState.updateEvents(weekEventsMap)
    }

    //you can also update monthly calendar events by your data here
    LaunchedEffect(calendarState.monthState.monthlyCurrentDate) {


        val date = calendarState.monthState.monthlyCurrentDate
        //in monthly calendar for displaying events you can use every child of CalendarEvent or make your custom event view
        val weekEventsMap: Map<DateId, CalendarEvent> = mapOf(
            DateId(date.year, date.month, date.day + 1) to CircleColorEvent(
                color = Color(0xFF5BCD85)
            ),
            DateId(date.year, date.month, date.day) to RectangleColorEvent(
                color = Color(0xFF9C27B0)
            ),
            DateId(date.year, date.month, date.day - 1) to PictureEvent(
                painter = eventImage
            ),
            DateId(date.year, date.month, date.day - 2) to CustomEvent(
                content = { CustomEventView() }
            )
        )

        calendarState.monthState.updateEvents(weekEventsMap)
    }

    //customize your calendar
    Column(
        modifier = Modifier
                .fillMaxSize()
            .padding(10.dp)
    ) {
        CalendarEventsView(
            state = calendarState,
            modifier = Modifier.fillMaxWidth(),
            tabBorderColor = Color(0xFF5E5E5E),
            tabSelectedColor = Color(0xFF964747),
            selectedDayColor = Color(0xFF3B6232),
            holidayDayColor = Color(0xFFFF0000),
            dayColor = Color(0xFF7C7C7C),
            dayNameColor = Color(0xFFCCCCCC)
        )
    }
}