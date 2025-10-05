package morz.eventcalendar.lib.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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
import morz.eventcalendar.lib.util.JalaliCalendarHelper.buildMonthGrid

@Stable
class CalendarMonthViewState(
    initialDate: JalaliCalendar = JalaliCalendarHelper.getCurrentDate()
) {

    companion object {
        val Saver: Saver<CalendarMonthViewState, *> = listSaver(
            save = {
                listOf(
                    it.monthlyCurrentDate.year,
                    it.monthlyCurrentDate.month,
                    it.monthlyCurrentDate.day
                )
            },
            restore = {
                CalendarMonthViewState(
                    JalaliCalendar(it[0], it[1], it[2])
                )
            }
        )
    }

    var monthlyCurrentDate by mutableStateOf(initialDate)
        private set

    var monthlySelectedDate by mutableStateOf(initialDate)
        private set

    var monthDays by mutableStateOf<List<List<DayItem>>>(emptyList())
        private set

    var monthEventsMap by mutableStateOf<Map<DateId, CalendarEvent>>(emptyMap())
        private set

    val isCurrentDateSelected
        get() =
            monthlySelectedDate.toString() ==
                    JalaliCalendarHelper.getCurrentDate().toString()

    init {
        recomputeMonthDays(monthlyCurrentDate)
    }

    fun onPreviousMonth() {
        val previousMonth = if (monthlyCurrentDate.month == 1) {
            JalaliCalendar(monthlyCurrentDate.year - 1, 12, 1)
        } else {
            JalaliCalendar(monthlyCurrentDate.year, monthlyCurrentDate.month - 1, 1)
        }
        monthlyCurrentDate = previousMonth
        setMonthAnchor(previousMonth)
    }

    fun onNextMonth() {
        val nextMonth = if (monthlyCurrentDate.month == 12) {
            JalaliCalendar(monthlyCurrentDate.year + 1, 1, 1)
        } else {
            JalaliCalendar(monthlyCurrentDate.year, monthlyCurrentDate.month + 1, 1)
        }
        monthlyCurrentDate = nextMonth
        setMonthAnchor(nextMonth)
    }

    fun updateMonthlySelectedDate(selectedDate: JalaliCalendar) {
        monthlySelectedDate = selectedDate
    }


    fun setInitialDate() {
        monthlyCurrentDate = JalaliCalendarHelper.getCurrentDate()
        monthlySelectedDate = JalaliCalendarHelper.getCurrentDate()
        recomputeMonthDays(monthlyCurrentDate)
    }

    fun setMonthAnchor(date: JalaliCalendar) {
        recomputeMonthDays(date)
    }

    private fun recomputeMonthDays(anchor: JalaliCalendar) {
        monthDays = buildMonthGrid(anchor.year, anchor.month)
    }

    fun updateEvents(weekEventsMap: Map<DateId, CalendarEvent>) {
        this.monthEventsMap = weekEventsMap
    }
}

@Composable
fun rememberCalendarMonthViewState(
    initialDate: JalaliCalendar = JalaliCalendarHelper.getCurrentDate()
): CalendarMonthViewState =
    rememberSaveable(saver = CalendarMonthViewState.Saver) {
        CalendarMonthViewState(initialDate)
    }


@Composable
fun CalendarMonthView(
    state: CalendarMonthViewState = rememberCalendarMonthViewState(JalaliCalendarHelper.getCurrentDate()),
    registry: RendererRegistry,
    selectedColor: Color,
    holidayColor: Color,
    todayColor: Color,
    dayColor: Color,
    dayNameColor: Color,
) {

    val monthDays = state.monthDays

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp) // Keep original compact spacing
    ) {
        // Header Section with Navigation
        MonthlyHeader(
            currentDate = state.monthlyCurrentDate,
            onPreviousMonth = state::onPreviousMonth,
            onNextMonth = state::onNextMonth
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid Container
        MonthlyCalendarGrid(
            monthDays = monthDays,
            currentDate = state.monthlyCurrentDate,
            selectedDate = state.monthlySelectedDate,
            onDaySelect = {
                state.updateMonthlySelectedDate(it)
            },
            monthEventsMap = state.monthEventsMap,
            registry = registry,
            selectedColor = selectedColor,
            holidayColor = holidayColor,
            todayColor = todayColor,
            dayColor = dayColor,
            dayNameColor = dayNameColor
        )
    }
}

@Composable
private fun MonthlyHeader(
    currentDate: JalaliCalendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    arrowBorderColor: Color = Color(0xFFEEEEEE)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous month arrow
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, arrowBorderColor, CircleShape)
                .clickable { onPreviousMonth() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous month",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }

        // Current month and year display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isDark = isSystemInDarkTheme()
            Text(
                text = "${currentDate.monthString} ${currentDate.year}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black,
                textAlign = TextAlign.Center
            )
        }

        // Next month arrow
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, arrowBorderColor, CircleShape)
                .clickable { onNextMonth() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next month",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun MonthlyCalendarGrid(
    monthDays: List<List<DayItem>>,
    currentDate: JalaliCalendar,
    selectedDate: JalaliCalendar,
    onDaySelect: (JalaliCalendar) -> Unit,
    monthEventsMap: Map<DateId, CalendarEvent>,
    registry: RendererRegistry,
    selectedColor: Color,
    holidayColor: Color,
    todayColor: Color,
    dayColor: Color,
    dayNameColor: Color,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        MonthlyDayHeaders(
            dayNameColor = dayNameColor
        )

        if (monthDays.isNotEmpty()) {
            MonthlyDaysGrid(
                monthDays = monthDays,
                currentDate = currentDate,
                selectedDate = selectedDate,
                onDaySelect = onDaySelect,
                monthEventsMap = monthEventsMap,
                registry = registry,
                selectedColor = selectedColor,
                holidayColor = holidayColor,
                todayColor = todayColor,
                dayColor = dayColor,
            )
        }
    }
}

@Composable
private fun MonthlyDayHeaders(
    dayNameColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("ش", "ی", "د", "س", "چ", "پ", "ج").forEach { dayHeader ->
            Text(
                text = dayHeader,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = dayNameColor,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MonthlyDaysGrid(
    monthDays: List<List<DayItem>>,
    currentDate: JalaliCalendar,
    selectedDate: JalaliCalendar,
    onDaySelect: (JalaliCalendar) -> Unit,
    monthEventsMap: Map<DateId, CalendarEvent>,
    registry: RendererRegistry,
    selectedColor: Color,
    holidayColor: Color,
    todayColor: Color,
    dayColor: Color,
) {

    val allDays = monthDays.flatten()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 0.dp, max = 300.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),  // Adjust horizontal spacing between columns
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 4.dp)// Adjust vertical spacing between rows
    ) {
        items(allDays.size) { index ->
            val dayItem = allDays[index]
            val dayNumber = dayItem.dayNumber.toIntOrNull()
            if (dayNumber != null) {
                val jalaliDate = JalaliCalendar(
                    currentDate.year,
                    currentDate.month,
                    dayNumber
                )

                val isSelected = JalaliCalendarHelper.isSameDate(jalaliDate, selectedDate)

                CalendarMonthDayItem(
                    dayItem = dayItem.copy(
                        isSelected = isSelected,
                    ),
                    onDayClick = { onDaySelect(jalaliDate) },
                    eventContent = {
                        if (monthEventsMap.isNotEmpty()) {
                            val dateId = DateId(
                                year = currentDate.year,
                                month = currentDate.month,
                                day = dayNumber
                            )
                            monthEventsMap[dateId]?.let {
                                registry.Render(it)
                            }
                        }
                    },
                    selectedColor = selectedColor,
                    holidayColor = holidayColor,
                    todayColor = todayColor,
                    dayColor = dayColor
                )
            } else {
                // This is an empty cell - show it as empty
                CalendarMonthDayItem(
                    dayItem = dayItem,
                    selectedColor = selectedColor,
                    holidayColor = holidayColor,
                    todayColor = todayColor,
                    dayColor = dayColor,
                    onDayClick = { },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarMonthViewPreview() {
    CalendarMonthView(
        registry = remember {
            RendererRegistry(
                setOf(
                    CircleColorRenderer, PictureRenderer, RectangleColorRenderer,
                    TextRenderer, CustomRenderer
                )
            )
        },
        selectedColor = Color(0xFF9C7DFF),
        holidayColor = Color(0xFFCF3434),
        todayColor = Color(0xFF673AB7),
        dayColor = Color.Black,
        dayNameColor = Color.Gray
    )
}