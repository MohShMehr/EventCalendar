package morz.eventcalendar.lib.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import morz.eventcalendar.lib.model.registery.RendererRegistry
import morz.eventcalendar.lib.model.renederers.CircleColorRenderer
import morz.eventcalendar.lib.model.renederers.PictureRenderer
import morz.eventcalendar.lib.model.renederers.RectangleColorRenderer
import morz.eventcalendar.lib.model.renederers.TextRenderer
import morz.eventcalendar.lib.util.JalaliCalendarHelper

@Stable
class CalendarEventsState internal constructor(
    val weekState: CalendarWeekViewState,
    val monthState: CalendarMonthViewState,
    val tabsState: CalendarTabsViewState,
    private val onWeeklySelectedDateChange: (String) -> Unit,
    private val onMonthlySelectedDateChange: (String) -> Unit,
    private val onCurrentWeekChange: (String) -> Unit,
    private val onCurrentMonthChange: (String) -> Unit,
) {
    val selectedTab: CalendarTab by derivedStateOf {
        if (tabsState.selectedTabIndex == 1) CalendarTab.MONTHLY else CalendarTab.WEEKLY
    }
}

@Composable
fun rememberCalendarEventsState(
    initialTabIndex: Int = 1, // 0=weekly, 1=monthly (match your current Tabs)
    onWeeklySelectedDateChange: (String) -> Unit = {},
    onMonthlySelectedDateChange: (String) -> Unit = {},
    onCurrentWeekChange: (String) -> Unit = {},
    onCurrentMonthChange: (String) -> Unit = {},
): CalendarEventsState {
    val weekState = rememberCalendarWeekViewState()
    val monthState = rememberCalendarMonthViewState(JalaliCalendarHelper.getCurrentDate())
    val tabsState = rememberCalendarTabsView(selectedIndex = initialTabIndex)

    // Wire callbacks once, without forcing the parent to duplicate LaunchedEffects.
    LaunchedEffect(weekState) {
        snapshotFlow { weekState.weeklySelectedDate.toString() }.collect(onWeeklySelectedDateChange)
    }
    LaunchedEffect(weekState) {
        snapshotFlow { weekState.weeklyCurrentDate.toString() }.collect(onCurrentWeekChange)
    }
    LaunchedEffect(monthState) {
        snapshotFlow { monthState.monthlySelectedDate.toString() }.collect(
            onMonthlySelectedDateChange
        )
    }
    LaunchedEffect(monthState) {
        snapshotFlow { monthState.monthlyCurrentDate.toString() }.collect(onCurrentMonthChange)
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
    registry: RendererRegistry = remember {
        RendererRegistry(
            setOf(
                CircleColorRenderer, PictureRenderer, RectangleColorRenderer,
                TextRenderer
            )
        )
    }
) {

    CalendarEventsContent(
        modifier = modifier,
        state = state,
        registry = registry
    )

}

@Composable
private fun CalendarEventsContent(
    modifier: Modifier = Modifier,
    state: CalendarEventsState,
    registry: RendererRegistry
) {

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier) {
            CalendarTabsView(
                state = state.tabsState
            )

            Spacer(modifier = Modifier.height(10.dp))

            when (state.selectedTab) {
                CalendarTab.MONTHLY -> {
                    CalendarMonthView(
                        state = state.monthState,
                        registry = registry
                    )
                }

                CalendarTab.WEEKLY -> {
                    CalendarWeekView(
                        state = state.weekState,
                        registry = registry
                    )
                }
            }


        }
    }
}

@Preview(
    locale = "FA",
    showBackground = true
)
@Composable
private fun CalendarEventsViewPreview() {
    val calendarState = rememberCalendarEventsState(
        initialTabIndex = 1, // monthly by default
        onWeeklySelectedDateChange = { /* update VM or analytics */ },
        onMonthlySelectedDateChange = { /* ... */ },
        onCurrentWeekChange = { /* ... */ },
        onCurrentMonthChange = { /* ... */ },
    )

    CalendarEventsView(
        state = calendarState,
        modifier = Modifier.fillMaxWidth(),
    )
}