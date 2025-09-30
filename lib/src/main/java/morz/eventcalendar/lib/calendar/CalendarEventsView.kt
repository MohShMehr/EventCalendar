package morz.eventcalendar.lib.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.huri.jcal.JalaliCalendar
import morz.eventcalendar.lib.util.DayItem
import morz.eventcalendar.lib.util.EventDot
import morz.eventcalendar.lib.util.JalaliCalendarHelper

@Stable
class CalendarEventsState internal constructor(
    val weekState: CalendarWeekViewState,
    val monthState: CalendarMonthViewState,
    val tabsState: CalendarTabsViewState,
    private val onWeeklySelectedDateChange: (JalaliCalendar) -> Unit,
    private val onMonthlySelectedDateChange: (JalaliCalendar) -> Unit,
    private val onCurrentWeekChange: (JalaliCalendar) -> Unit,
    private val onCurrentMonthChange: (JalaliCalendar) -> Unit,
) {
    val selectedTab: CalendarTab by derivedStateOf {
        if (tabsState.selectedTabIndex == 1) CalendarTab.MONTHLY else CalendarTab.WEEKLY
    }
}

@Composable
fun rememberCalendarEventsState(
    initialTabIndex: Int = 1, // 0=weekly, 1=monthly (match your current Tabs)
    onWeeklySelectedDateChange: (JalaliCalendar) -> Unit = {},
    onMonthlySelectedDateChange: (JalaliCalendar) -> Unit = {},
    onCurrentWeekChange: (JalaliCalendar) -> Unit = {},
    onCurrentMonthChange: (JalaliCalendar) -> Unit = {},
): CalendarEventsState {
    val weekState = rememberCalendarWeekViewState()
    val monthState = rememberCalendarMonthViewState(JalaliCalendarHelper.getCurrentDate())
    val tabsState = rememberCalendarTabsView(selectedIndex = initialTabIndex)

    // Wire callbacks once, without forcing the parent to duplicate LaunchedEffects.
    LaunchedEffect(weekState) {
        snapshotFlow { weekState.weeklySelectedDate }.collect(onWeeklySelectedDateChange)
    }
    LaunchedEffect(weekState) {
        snapshotFlow { weekState.weeklyCurrentDate }.collect(onCurrentWeekChange)
    }
    LaunchedEffect(monthState) {
        snapshotFlow { monthState.monthlySelectedDate }.collect(onMonthlySelectedDateChange)
    }
    LaunchedEffect(monthState) {
        snapshotFlow { monthState.monthlyCurrentDate }.collect(onCurrentMonthChange)
    }

    return remember(
        weekState, monthState, tabsState,
        onWeeklySelectedDateChange, onMonthlySelectedDateChange,
        onCurrentWeekChange, onCurrentMonthChange
    ) {
        CalendarEventsState(
            weekState = weekState,
            monthState = monthState,
            tabsState = tabsState,
            onWeeklySelectedDateChange = onWeeklySelectedDateChange,
            onMonthlySelectedDateChange = onMonthlySelectedDateChange,
            onCurrentWeekChange = onCurrentWeekChange,
            onCurrentMonthChange = onCurrentMonthChange,
        )
    }
}



@Composable
fun CalendarEventsView(
    modifier: Modifier = Modifier,
    state: CalendarEventsState,
    monthDays: List<List<DayItem>>,
    weekDays: List<DayItem>,
    weekEventContents: List<@Composable ColumnScope.() -> Unit> = emptyList(),
    monthEventContents: List<@Composable ColumnScope.() -> Unit> = emptyList(),
) {

    CalendarEventsContent(
        modifier = modifier,
        state = state,
        monthDays = monthDays,
        weekDays = weekDays,
        weekEventContents = weekEventContents,
        monthEventContents = monthEventContents
    )

}

@Composable
private fun CalendarEventsContent(
    modifier: Modifier = Modifier,
    state: CalendarEventsState,
    monthDays: List<List<DayItem>>,
    weekDays: List<DayItem>,
    weekEventContents: List<@Composable ColumnScope.() -> Unit> = emptyList(),
    monthEventContents: List<@Composable ColumnScope.() -> Unit> = emptyList(),
) {
    Column(modifier) {
        CalendarTabsView(
            state = state.tabsState
        )

        Spacer(modifier = Modifier.height(10.dp))

        when (state.selectedTab) {
            CalendarTab.MONTHLY -> {
                CalendarMonthView(
                    state = state.monthState,
                    monthDays = monthDays,
                    eventContents = monthEventContents
                )
            }

            CalendarTab.WEEKLY -> {
                CalendarWeekView(
                    weekDays = weekDays,
                    state = state.weekState,
                    eventContents = weekEventContents
                )
            }
        }


    }
}

@Preview(
    locale = "FA",
    showBackground = true)
@Composable
private fun CalendarEventsViewPreview() {
    val monthDays = JalaliCalendarHelper.getMonthDays(1404, 7)
    monthDays.apply {
        this[0][0].copy(events = listOf(EventDot(color = 0xFF5BCD85)))
        this[0][3].copy(events = listOf(EventDot(color = 0xFF5BCD85), EventDot(color = 0xFFFF3304)))
    }

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
                events = if (day % 2 == 0 )listOf(
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
        onWeeklySelectedDateChange = { /* update VM or analytics */ },
        onMonthlySelectedDateChange = { /* ... */ },
        onCurrentWeekChange = { /* ... */ },
        onCurrentMonthChange = { /* ... */ },
    )

    CalendarEventsView(
        state = calendarState,
        weekDays = weekDays,        // stable list if possible
        monthDays =monthDays,
        modifier = Modifier.fillMaxWidth(),
        weekEventContents = weekEvContents,
        monthEventContents = monthEvContents
    )
}