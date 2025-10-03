package morz.eventcalendar.lib.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Stable
class CalendarTabsViewState(selectedIndex: Int = 0) {
    var selectedTabIndex by mutableIntStateOf(selectedIndex)
        private set

    fun updateSelected(index: Int) {
        selectedTabIndex = index
    }

    companion object {
        val Saver: Saver<CalendarTabsViewState, *> = listSaver(
            save = { listOf(it.selectedTabIndex) },
            restore = { CalendarTabsViewState(it.first()) }
        )
    }
}

@Composable
fun rememberCalendarTabsView(selectedIndex: Int): CalendarTabsViewState =
    rememberSaveable(saver = CalendarTabsViewState.Saver) { CalendarTabsViewState(selectedIndex) }

enum class CalendarTab(val value: String) {
    WEEKLY("تقویم هفتگی"),
    MONTHLY("تقویم ماهانه")
}

@Composable
fun CalendarTabsView(
    modifier: Modifier = Modifier,
    state: CalendarTabsViewState = rememberCalendarTabsView(
        0
    ),
    borderColor: Color,
    selectedColor: Color,
    showInHalfWidth: Boolean = false
) {
    val shape = RoundedCornerShape(20.dp)
    val fraction =
        if (showInHalfWidth) 0.5f else 1.0f
    Row(
        modifier
            .fillMaxWidth(fraction = fraction),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(shape)
                .background(Color.White)
                .border(
                    border = BorderStroke(1.dp, borderColor),
                    shape = shape
                )
                .testTag("top tabs")
                .padding(4.dp),
            contentAlignment = Alignment.Center
        )
        {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                CalendarTab.entries.forEach { tab ->
                    val isSelected = state.selectedTabIndex == tab.ordinal
                    val text = tab.value
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) selectedColor else Color.Transparent,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { state.updateSelected(tab.ordinal) }
                            .padding(
                                start = if (isSelected) 8.dp else 0.dp,
                                end = if (isSelected) 8.dp else 0.dp,
                                top = if (isSelected) 4.dp else 0.dp,
                                bottom = if (isSelected) 4.dp else 0.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = text,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
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
private fun CalendarTabsFullWidthPreview() {
    Box(
        modifier = Modifier.fillMaxWidth()
    ){

        CalendarTabsView(
            borderColor = Color(0xFFEEEEEE),
            selectedColor = Color(0xFF7B55D3)
        )
    }
}

@Preview(
    locale = "FA",
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
private fun CalendarTabsHalfWidthPreview() {
    Box(
        modifier = Modifier.fillMaxWidth()
    ){
        CalendarTabsView(
            borderColor = Color(0xFFEEEEEE),
            selectedColor = Color(0xFF7B55D3),
            showInHalfWidth = true
        )
    }
}
