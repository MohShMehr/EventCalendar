package morz.eventcalendar.lib.util

import ir.huri.jcal.JalaliCalendar

/**
 * Utility class for JalaliCalendar operations
 */
object JalaliCalendarHelper {
    
    /**
     * Get all days of a month in Jalali calendar
     * @param year Jalali year
     * @param month Jalali month (1-12)
     * @return List of weeks, each week is a list of DayItem
     */
    fun getMonthDays(year: Int, month: Int): List<List<DayItem>> {
        val jalali = JalaliCalendar(year, month, 1)
        val monthLength = jalali.monthLength
        val firstDayOfWeek = jalali.dayOfWeek
        
        // Convert Jalali day of week to our week start (Saturday = 0)
        // Based on the actual JalaliCalendar.dayOfWeek values from debug output
        val weekStartOffset = when (firstDayOfWeek) {
            7 -> 0  // Saturday (ش) - rightmost column
            1 -> 1  // Sunday (ی)
            2 -> 2  // Monday (د)
            3 -> 3  // Tuesday (س)
            4 -> 4  // Wednesday (چ)
            5 -> 5  // Thursday (پ)
            6 -> 6  // Friday (ج) - leftmost column
            else -> 0
        }
        
        val weeks = mutableListOf<List<DayItem>>()
        var currentWeek = mutableListOf<DayItem>()
        
        // Add empty days for the beginning of the month
        repeat(weekStartOffset) {
            currentWeek.add(DayItem("", "", false, emptyList()))
        }
        
        // Add all days of the month
        for (day in 1..monthLength) {
            val jalaliDate = JalaliCalendar(year, month, day)
            val dayName = getDayName(jalaliDate.dayOfWeek)
            val persianDate = FormatHelper.toPersianNumber(day.toString())
            
            currentWeek.add(
                DayItem(
                    dayName = dayName,
                    date = persianDate,
                    isSelected = false,
                    events = emptyList() // TODO: Add events based on tasks
                )
            )
            
            // If we have 7 days, start a new week
            if (currentWeek.size == 7) {
                weeks.add(currentWeek.toList())
                currentWeek = mutableListOf()
            }
        }
        
        // Add remaining days to complete the last week
        while (currentWeek.size < 7) {
            currentWeek.add(DayItem("", "", false, emptyList()))
        }
        
        // Add the last week if it's not empty
        if (currentWeek.isNotEmpty()) {
            weeks.add(currentWeek)
        }
        
        return weeks
    }
    
    /**
     * Get current week days starting from Saturday
     * @param year Jalali year
     * @param month Jalali month
     * @param day Jalali day
     * @return List of DayItem for the current week
     */
    fun getCurrentWeekDays(year: Int, month: Int, day: Int): List<DayItem> {
        val jalali = JalaliCalendar(year, month, day)
        val currentDayOfWeek = jalali.dayOfWeek
        
        // Convert to our week start (Saturday = 0)
        // Based on the actual JalaliCalendar.dayOfWeek values from debug output
        val weekStartOffset = when (currentDayOfWeek) {
            7 -> 0  // Saturday (ش) - rightmost column
            1 -> 1  // Sunday (ی)
            2 -> 2  // Monday (د)
            3 -> 3  // Tuesday (س)
            4 -> 4  // Wednesday (چ)
            5 -> 5  // Thursday (پ)
            6 -> 6  // Friday (ج) - leftmost column
            else -> 0
        }
        
        val weekDays = mutableListOf<DayItem>()
        
        // Calculate the Saturday of current week
        val saturdayDate = JalaliCalendar(year, month, day - weekStartOffset)
        
        // Generate 7 days starting from Saturday
        for (i in 0..6) {
            val currentDate = JalaliCalendar(saturdayDate.year, saturdayDate.month, saturdayDate.day + i)
            val dayName = getDayName(currentDate.dayOfWeek)
            val persianDate = FormatHelper.toPersianNumber(currentDate.day.toString())
            
            weekDays.add(
                DayItem(
                    dayName = dayName,
                    date = persianDate,
                    isSelected = currentDate.day == day && currentDate.month == month && currentDate.year == year,
                    events = emptyList() // TODO: Add events based on tasks
                )
            )
        }
        
        return weekDays
    }
    
    /**
     * Get current Jalali date
     * @return JalaliCalendar for today
     */
    fun getCurrentDate(): JalaliCalendar {
        return JalaliCalendar()
    }
    
    /**
     * Get Persian day name
     * @param dayOfWeek Day of week (1-7, where 7 is Saturday)
     * @return Persian day name
     */
    fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            7 -> "شنبه"
            1 -> "یکشنبه"
            2 -> "دوشنبه"
            3 -> "سه‌شنبه"
            4 -> "چهارشنبه"
            5 -> "پنج‌شنبه"
            6 -> "جمعه"
            else -> ""
        }
    }
    
    /**
     * Get month name in Persian
     * @param month Month number (1-12)
     * @return Persian month name
     */
    fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "فروردین"
            2 -> "اردیبهشت"
            3 -> "خرداد"
            4 -> "تیر"
            5 -> "مرداد"
            6 -> "شهریور"
            7 -> "مهر"
            8 -> "آبان"
            9 -> "آذر"
            10 -> "دی"
            11 -> "بهمن"
            12 -> "اسفند"
            else -> ""
        }
    }
    
    /**
     * Convert Jalali date to readable Persian string
     * @param jalali JalaliCalendar instance
     * @return Formatted Persian date string
     */
    fun formatPersianDate(jalali: JalaliCalendar): String {
        val day = FormatHelper.toPersianNumber(jalali.day.toString())
        val month = getMonthName(jalali.month)
        val year = FormatHelper.toPersianNumber(jalali.year.toString())
        return "$day $month $year"
    }
    
    /**
     * Check if a date is today
     * @param jalali JalaliCalendar instance to check
     * @return true if the date is today
     */
    fun isToday(jalali: JalaliCalendar): Boolean {
        val today = JalaliCalendar()
        return jalali.year == today.year && jalali.month == today.month && jalali.day == today.day
    }
    
    /**
     * Check if a date is the same as another date
     * @param date1 First JalaliCalendar instance
     * @param date2 Second JalaliCalendar instance
     * @return true if both dates are the same
     */
    fun isSameDate(date1: JalaliCalendar, date2: JalaliCalendar): Boolean {
        return date1.year == date2.year && date1.month == date2.month && date1.day == date2.day
    }
    
    /**
     * Check if a date is Friday (جمعه) - holiday in Jalali calendar
     * @param jalali JalaliCalendar instance to check
     * @return true if the date is Friday
     */
    fun isFriday(jalali: JalaliCalendar): Boolean {
        // Get the actual Persian day name and check if it's Friday (جمعه)
        val dayName = getDayName(jalali.dayOfWeek)
        return dayName == "جمعه"
    }
    
    /**
     * Alternative method to check if a date is Friday based on month position
     * This bypasses the day-of-week mapping issues
     * @param jalali JalaliCalendar instance to check
     * @return true if the date should be Friday
     */
    fun isFridayAlternative(jalali: JalaliCalendar): Boolean {
        // Calculate what day of the week this should be based on the month start
        val monthStart = JalaliCalendar(jalali.year, jalali.month, 1)
        val daysSinceMonthStart = jalali.day - 1
        
        // If month starts on Saturday (ش), then:
        // Day 1 = Saturday (ش), Day 2 = Sunday (ی), Day 3 = Monday (د)
        // Day 4 = Tuesday (س), Day 5 = Wednesday (چ), Day 6 = Thursday (پ)
        // Day 7 = Friday (ج), Day 8 = Saturday (ش), Day 9 = Sunday (ی), etc.
        val dayOfWeek = (daysSinceMonthStart) % 7
        
        // Friday is the 6th day (0-indexed: 0=Sat, 1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri)
        // But since we start from Saturday, Friday is actually day 6 (0-indexed)
        return dayOfWeek == 6 // 6 represents Friday (0-indexed from Saturday)
    }
    
    /**
     * More robust method to check if a date is Friday
     * This handles different month start days correctly
     * @param jalali JalaliCalendar instance to check
     * @return true if the date should be Friday
     */
    fun isFridayRobust(jalali: JalaliCalendar): Boolean {
        // Get the month start and calculate the actual day of week
        val monthStart = JalaliCalendar(jalali.year, jalali.month, 1)
        val daysSinceMonthStart = jalali.day - 1
        
        // Calculate the actual day of week based on month start
        // We need to know what day of week the month starts on
        val monthStartDayOfWeek = monthStart.dayOfWeek
        
        // Convert to our Saturday-based system (0=Sat, 1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri)
        val adjustedMonthStart = when (monthStartDayOfWeek) {
            6 -> 0  // Saturday
            1 -> 1  // Sunday
            2 -> 2  // Monday
            3 -> 3  // Tuesday
            4 -> 4  // Wednesday
            5 -> 5  // Thursday
            7 -> 6  // Friday
            else -> 0
        }
        
        // Calculate the day of week for the current date
        val currentDayOfWeek = (adjustedMonthStart + daysSinceMonthStart) % 7
        
        // Friday is day 6 in our Saturday-based system
        return currentDayOfWeek == 6
    }
    
    /**
     * Check if a date is Friday based on the actual day of week
     * This method only marks actual Fridays as holidays
     * @param jalali JalaliCalendar instance to check
     * @return true if the date is Friday
     */
    fun isFridaySimple(jalali: JalaliCalendar): Boolean {
        // In JalaliCalendar, Friday is dayOfWeek = 6
        // Only return true for actual Fridays
        return jalali.dayOfWeek == 6
    }
    
    /**
     * Debug function to check day-of-week mapping
     * @param jalali JalaliCalendar instance to check
     * @return Debug string with day info
     */
    fun debugDayInfo(jalali: JalaliCalendar): String {
        val dayName = getDayName(jalali.dayOfWeek)
        return "Day: ${jalali.day}, DayOfWeek: ${jalali.dayOfWeek}, Name: $dayName"
    }
    

    
    /**
     * Get Persian day name from JalaliCalendar instance
     * @param jalali JalaliCalendar instance
     * @return Persian day name
     */
    fun getDayName(jalali: JalaliCalendar): String {
        return getDayName(jalali.dayOfWeek)
    }
    
    /**
     * Get the start of the week (Saturday) for a given date
     * @param jalali JalaliCalendar instance
     * @return JalaliCalendar for the Saturday of the week
     */
    fun getWeekStart(jalali: JalaliCalendar): JalaliCalendar {
        val dayOfWeek = jalali.dayOfWeek
        val daysToSubtract = when (dayOfWeek) {
            6 -> 0  // Saturday (ش) - rightmost column
            1 -> 1  // Sunday (ی)
            2 -> 2  // Monday (د)
            3 -> 3  // Tuesday (س)
            4 -> 4  // Wednesday (چ)
            5 -> 5  // Thursday (پ)
            7 -> 6  // Friday (ج) - leftmost column
            else -> 0
        }
        
        val targetDay = jalali.day - daysToSubtract
        
        // Handle month boundary crossing
        return if (targetDay <= 0) {
            // Need to go to previous month
            if (jalali.month == 1) {
                JalaliCalendar(jalali.year - 1, 12, 31 + targetDay)
            } else {
                val prevMonth = JalaliCalendar(jalali.year, jalali.month - 1, 1)
                val daysInPrevMonth = prevMonth.monthLength
                JalaliCalendar(jalali.year, jalali.month - 1, daysInPrevMonth + targetDay)
            }
        } else {
            JalaliCalendar(jalali.year, jalali.month, targetDay)
        }
    }
    
    /**
     * Get the end of the week (Friday) for a given date
     * @param jalali JalaliCalendar instance
     * @return JalaliCalendar for the Friday of the week
     */
    fun getWeekEnd(jalali: JalaliCalendar): JalaliCalendar {
        val weekStart = getWeekStart(jalali)
        val targetDay = weekStart.day + 6
        
        // Handle month boundary crossing
        return if (targetDay > JalaliCalendar(weekStart.year, weekStart.month, 1).monthLength) {
            // Need to go to next month
            if (weekStart.month == 12) {
                JalaliCalendar(weekStart.year + 1, 1, targetDay - JalaliCalendar(weekStart.year, weekStart.month, 1).monthLength)
            } else {
                JalaliCalendar(weekStart.year, weekStart.month + 1, targetDay - JalaliCalendar(weekStart.year, weekStart.month, 1).monthLength)
            }
        } else {
            JalaliCalendar(weekStart.year, weekStart.month, targetDay)
        }
    }
}

