package morz.eventcalendar.lib.util

object FormatHelper {
    private val persianNumbers = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")

    fun toPersianNumber(text: String): String {
        return text.map { char ->
            when (char) {
                in '0'..'9' -> persianNumbers[char - '0']
                '٫' -> '،'
                else -> char
            }
        }.joinToString("")
    }
}
