package morz.eventcalendar.lib.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import morz.eventcalendar.lib.util.DayItem
import morz.eventcalendar.lib.util.JalaliCalendarHelper

@Stable
class CalendarMonthViewState(
    initialDate: JalaliCalendar = JalaliCalendarHelper.getCurrentDate()
) {
    var monthlyCurrentDate by mutableStateOf(initialDate)
        private set

    var monthlySelectedDate by mutableStateOf(initialDate)
        private set

    fun onPreviousMonth() {
        val previousMonth = if (monthlyCurrentDate.month == 1) {
            JalaliCalendar(monthlyCurrentDate.year - 1, 12, 1)
        } else {
            JalaliCalendar(monthlyCurrentDate.year, monthlyCurrentDate.month - 1, 1)
        }
        monthlyCurrentDate = previousMonth
    }

    fun onNextMonth() {
        val nextMonth = if (monthlyCurrentDate.month == 12) {
            JalaliCalendar(monthlyCurrentDate.year + 1, 1, 1)
        } else {
            JalaliCalendar(monthlyCurrentDate.year, monthlyCurrentDate.month + 1, 1)
        }
        monthlyCurrentDate = nextMonth
    }

    fun updateMonthlySelectedDate(selectedDate: JalaliCalendar) {
        monthlySelectedDate = selectedDate
    }


    fun setInitialDate() {
        monthlyCurrentDate = JalaliCalendarHelper.getCurrentDate()
        monthlySelectedDate = JalaliCalendarHelper.getCurrentDate()
    }

    val isCurrentDateSelected
        get() =
        monthlySelectedDate.toString() ==
                JalaliCalendarHelper.getCurrentDate().toString()

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
    monthDays: List<List<DayItem>> = emptyList(),
    eventContents: List<@Composable ColumnScope.() -> Unit> = emptyList(),
) {
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
            eventContents = eventContents
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
            Text(
                text = "${JalaliCalendarHelper.getMonthName(currentDate.month)} ${currentDate.year}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
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
    eventContents: List<@Composable ColumnScope.() -> Unit> = emptyList(),
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Day headers row
        MonthlyDayHeaders()

        // Calendar grid - no extra spacing
        if (monthDays.isEmpty()) {
            MonthlyLoadingState()
        } else {
            MonthlyDaysGrid(
                monthDays = monthDays,
                currentDate = currentDate,
                selectedDate = selectedDate,
                onDaySelect = onDaySelect,
                eventContents = eventContents
            )
        }
    }
}

@Composable
private fun MonthlyDayHeaders() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("ش", "ی", "د", "س", "چ", "پ", "ج").forEach { dayHeader ->
            Text(
                text = dayHeader,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
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
    eventContents: List<@Composable ColumnScope.() -> Unit> = emptyList(),
) {
    // Flatten the monthDays list but DON'T filter out empty days
    // Empty days are needed for proper calendar grid layout
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
            val dayNumber = dayItem.date.toIntOrNull()
            if (dayNumber != null) {
                val jalaliDate = JalaliCalendar(
                    currentDate.year,
                    currentDate.month,
                    dayNumber
                )
                val isToday = JalaliCalendarHelper.isToday(jalaliDate)
                val isSelected = JalaliCalendarHelper.isSameDate(jalaliDate, selectedDate)
                val isHoliday = JalaliCalendarHelper.isFridaySimple(jalaliDate)

                CalendarMonthDayItem(
                    dayItem = dayItem.copy(
                        isSelected = isSelected,
                        isHoliday = isHoliday
                    ),
                    onDayClick = { onDaySelect(jalaliDate) },
                    eventContent = eventContents[index]
                )
            } else {
                // This is an empty cell - show it as empty
                CalendarMonthDayItem(
                    dayItem = dayItem,
                    onDayClick = { /* Empty cell - no action */ },
                )
            }
        }
    }
}

@Composable
private fun MonthlyLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "در حال بارگذاری...",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "لطفاً صبر کنید",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

typealias EventContent = @Composable ColumnScope.() -> Unit
@Preview(showBackground = true)
@Composable
private fun CalendarWeekViewPreview() {
    val monthDays = JalaliCalendarHelper.getMonthDays(1402,6)

    val evContents: List<EventContent> = monthDays.flatten().map { dayItem ->
        if (dayItem.events.isNotEmpty()) {
            {
                DayItemEventView(dayItem, 2)
            }
        } else {
            { /* empty */ }
        }
    }
    CalendarMonthView(
        monthDays = monthDays,
        eventContents = evContents
    )
}