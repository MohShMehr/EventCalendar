package morz.eventcalendar.lib.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.huri.jcal.JalaliCalendar
import morz.eventcalendar.lib.model.DateId
import morz.eventcalendar.lib.model.DayItem
import morz.eventcalendar.lib.model.events.CalendarEvent
import morz.eventcalendar.lib.model.registery.RendererRegistry
import morz.eventcalendar.lib.model.renederers.CircleColorRenderer
import morz.eventcalendar.lib.model.renederers.CustomRenderer
import morz.eventcalendar.lib.model.renederers.PictureRenderer
import morz.eventcalendar.lib.model.renederers.RectangleColorRenderer
import morz.eventcalendar.lib.model.renederers.TextRenderer
import morz.eventcalendar.lib.util.JalaliCalendarHelper
import morz.eventcalendar.lib.util.JalaliCalendarHelper.buildWeekDays
import morz.eventcalendar.lib.util.JalaliCalendarHelper.weekTitleFrom

@Stable
class CalendarWeekViewState(
    initialDate: JalaliCalendar = JalaliCalendarHelper.getCurrentDate()
) {

    companion object {
        val Saver: Saver<CalendarWeekViewState, *> = listSaver(
            save = {
                listOf(
                    it.weeklyCurrentDate.year,
                    it.weeklyCurrentDate.month,
                    it.weeklyCurrentDate.day
                )
            },
            restore = { CalendarWeekViewState(JalaliCalendar(it[0], it[1], it[2])) }
        )
    }

    var weeklyCurrentDate by mutableStateOf(initialDate)
        private set
    var weeklySelectedDate by mutableStateOf(initialDate)
        private set

    var weekDays by mutableStateOf<List<DayItem>>(emptyList())
        private set

    var weekEventsMap by mutableStateOf<Map<DateId, CalendarEvent>>(emptyMap())
        private set


    val isCurrentDateSelected
        get() =
            weeklySelectedDate.toString() ==
                    JalaliCalendarHelper.getCurrentDate().toString()

    init {
        recomputeWeekDays(weeklyCurrentDate)
    }

    fun setInitialDate() {
        weeklyCurrentDate = JalaliCalendarHelper.getCurrentDate()
        weeklySelectedDate = JalaliCalendarHelper.getCurrentDate()
        recomputeWeekDays(weeklyCurrentDate)
    }

    fun getCurrentJalaliDateByDay(dayNumber: Int) = JalaliCalendar(
        weeklyCurrentDate.year,
        weeklyCurrentDate.month,
        dayNumber
    )

    fun updateWeeklySelectedDate(dayNumber: Int) {
        weeklySelectedDate = getCurrentJalaliDateByDay(dayNumber)
    }

    fun onPreviousCLick() {

        // Calculate previous week date with proper month boundary handling
        val previousWeek = if (weeklyCurrentDate.day <= 7) {
            if (weeklyCurrentDate.month == 1) {
                val prevYear = weeklyCurrentDate.year - 1
                val prevMonth = JalaliCalendar(prevYear, 12, 1)
                val daysInPrevMonth = prevMonth.monthLength
                val adjustedDay = daysInPrevMonth - (7 - weeklyCurrentDate.day)
                JalaliCalendar(prevYear, 12, adjustedDay)
            } else {
                val prevMonth =
                    JalaliCalendar(weeklyCurrentDate.year, weeklyCurrentDate.month - 1, 1)
                val daysInPrevMonth = prevMonth.monthLength
                val adjustedDay = daysInPrevMonth - (7 - weeklyCurrentDate.day)
                JalaliCalendar(weeklyCurrentDate.year, weeklyCurrentDate.month - 1, adjustedDay)
            }
        } else {
            JalaliCalendar(
                weeklyCurrentDate.year,
                weeklyCurrentDate.month,
                weeklyCurrentDate.day - 7
            )
        }

        // Validate the date before calling onDateChange
        if (previousWeek.day > 0 && previousWeek.day <= JalaliCalendar(
                previousWeek.year,
                previousWeek.month,
                1
            ).monthLength
        ) {
            weeklyCurrentDate = previousWeek
            setWeekAnchor(previousWeek)

        }
    }

    fun onNextCLick() {
        // Calculate next week date with proper month boundary handling
        val currentMonthLength =
            JalaliCalendar(weeklyCurrentDate.year, weeklyCurrentDate.month, 1).monthLength
        val nextWeek = if (weeklyCurrentDate.day + 7 > currentMonthLength) {
            // Need to go to next month
            if (weeklyCurrentDate.month == 12) {
                val adjustedDay = (weeklyCurrentDate.day + 7) - currentMonthLength
                JalaliCalendar(weeklyCurrentDate.year + 1, 1, adjustedDay)
            } else {
                val adjustedDay = (weeklyCurrentDate.day + 7) - currentMonthLength
                JalaliCalendar(weeklyCurrentDate.year, weeklyCurrentDate.month + 1, adjustedDay)
            }
        } else {
            JalaliCalendar(
                weeklyCurrentDate.year,
                weeklyCurrentDate.month,
                weeklyCurrentDate.day + 7
            )
        }

        // Validate the date before calling onDateChange
        if (nextWeek.day > 0 && nextWeek.day <= JalaliCalendar(
                nextWeek.year,
                nextWeek.month,
                1
            ).monthLength
        ) {
            weeklyCurrentDate = nextWeek
            setWeekAnchor(nextWeek)
        }
    }

    fun isSelected(dayNumber: Int): Boolean {
        val jalaliDate = JalaliCalendar(
            weeklyCurrentDate.year,
            weeklyCurrentDate.month,
            dayNumber
        )
        return JalaliCalendarHelper.isSameDate(jalaliDate, weeklySelectedDate)
    }

    fun setWeekAnchor(date: JalaliCalendar) {
        recomputeWeekDays(date)
    }

    private fun recomputeWeekDays(anchor: JalaliCalendar) {
        weekDays = buildWeekDays(anchor)
    }

    @Composable
    fun rememberWeekTitle(weekDays: List<DayItem>): String {
        val title by remember(weekDays, weeklyCurrentDate) {
            derivedStateOf { weekTitleFrom(weekDays) }
        }
        return title
    }

    fun updateEvents(weekEventsMap: Map<DateId, CalendarEvent>) {
        this.weekEventsMap = weekEventsMap
    }
}


@Composable
fun rememberCalendarWeekViewState(
    initialDate: JalaliCalendar = JalaliCalendarHelper.getCurrentDate()
): CalendarWeekViewState =
    rememberSaveable(saver = CalendarWeekViewState.Saver) {
        CalendarWeekViewState(initialDate)
    }


@Composable
fun CalendarWeekView(
    state: CalendarWeekViewState = rememberCalendarWeekViewState(),
    arrowBorderColor: Color = Color(0xFFEEEEEE),
    selectedColor: Color,
    holidayColor: Color,
    dayColor: Color,
    dayNameColor: Color,
    registry: RendererRegistry
) {

    val weekDays = state.weekDays

    val title = state.rememberWeekTitle(weekDays)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // Navigation arrows and week display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous week arrow
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, arrowBorderColor, CircleShape)
                    .clickable { state.onPreviousCLick() }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous week",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isDark = isSystemInDarkTheme()
                Text(
                    text = title, // ← use computed title
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            // Next week arrow
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, arrowBorderColor, CircleShape)
                    .clickable { state.onNextCLick() }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next week",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(weekDays) { dayItem ->
                if (dayItem.dayNumber.isNotEmpty()) {
                    val dayNumber = dayItem.dayNumber.toIntOrNull()
                    if (dayNumber != null) {
                        CalendarWeekDayItem(
                            dayItem = dayItem.copy(
                                isSelected = state.isSelected(dayNumber)
                            ),
                            onDayClick = {
                                state.updateWeeklySelectedDate(dayNumber)
                            },
                            selectedColor = selectedColor,
                            holidayColor = holidayColor,
                            dayColor = dayColor,
                            dayNameColor = dayNameColor,
                            eventContent = {
                                if (state.weekEventsMap.isNotEmpty()) {
                                    val dateId = DateId(
                                        year = state.weeklyCurrentDate.year,
                                        month = state.weeklyCurrentDate.month,
                                        day = dayNumber
                                    )
                                    state.weekEventsMap[dateId]?.let {
                                        registry.Render(it)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "fa")
@Composable
private fun CalendarWeekViewPreview() {
    CalendarWeekView(
        state = rememberCalendarWeekViewState(
        ),
        selectedColor = Color(0xFF9C7DFF),
        holidayColor = Color(0xFFCF3434),
        dayColor = Color.Black,
        dayNameColor = Color.Gray,
        registry = remember {
            RendererRegistry(
                setOf(
                    CircleColorRenderer, PictureRenderer, RectangleColorRenderer,
                    TextRenderer, CustomRenderer
                )
            )
        }
    )
}
